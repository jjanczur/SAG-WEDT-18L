package actors.menuClassifier.classifier;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import utils.Util;

import java.io.*;
import java.util.*;


@Getter
@Setter
public class Algorithm {

    private static final StanfordCoreNLP pipeline;
    private final static Logger log = Logger.getLogger(Algorithm.class);
    private final static List<String> stopWords;

    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        pipeline = new StanfordCoreNLP(props);
        stopWords = loadStopWords();
        PropertyConfigurator.configure(Algorithm.class.getResource(Util.log4jConfPath));
    }

    private final Counter<String> dfCounter;
    private final int numDocuments;

    public Algorithm(Counter<String> dfCounter, int numDocuments) {
        this.dfCounter = dfCounter;
        this.numDocuments = numDocuments;

    }

    public static List<String> loadStopWords() {

        List<String> stopList = new ArrayList<String>();
        File stopWords = Util.getFileFromResource("conf/stopwords.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(stopWords))) {
            for (String line; (line = br.readLine()) != null; ) {
                stopList.add(line);
            }

        } catch (FileNotFoundException e) {
            log.error("File not found. Stack: " + e.getMessage());
        } catch (IOException e) {
            log.error("IOException: " + e.getMessage());
        }

        return stopList;

    }

    private static Counter<String> getTermFrequencies(List<String> tokens) {

        Counter<String> ret = new ClassicCounter<String>();

        for (String cl : tokens) {
            ret.incrementCount(cl);
        }
        return ret;
    }

    public static List<String> getLemmizationAndUseStopWords(List<CoreLabel> tokens) {
        List<String> tokensAfterLemma = new ArrayList<String>();

        for (CoreLabel token : tokens) {
            if (!stopWords.contains(token.value())) {
                tokensAfterLemma.add(token.lemma());
            }
        }
        return tokensAfterLemma;
    }

    public static CoreDocument getAnootatedDocument(String documentContent) {
        CoreDocument document = new CoreDocument(documentContent);
        pipeline.annotate(document);
        return document;
    }

    public Map<String, Double> getTfIdfs(String documentContent) {

        CoreDocument document = getAnootatedDocument(documentContent);

        List<String> tokensAfterLemma = getLemmizationAndUseStopWords(document.tokens());

        Counter<String> tfs = getTermFrequencies(tokensAfterLemma);

        Map<String, Double> tfIdfs = new HashMap<String, Double>();

        for (String token : tokensAfterLemma) {
            tfIdfs.put(token, TfIdf.tfIDFWeight(token, tfs, this.numDocuments, this.dfCounter));
        }

        return tfIdfs;

    }

    public Map<String, Double> getTfIdfs(List<String> tokensAfterLemma) {

        Counter<String> tfs = getTermFrequencies(tokensAfterLemma);

        Map<String, Double> tfIdfs = new HashMap<String, Double>();

        for (String token : tokensAfterLemma) {
            tfIdfs.put(token, TfIdf.tfIDFWeight(token, tfs, this.numDocuments, this.dfCounter));
        }

        return tfIdfs;

    }

    public static Double[] transformToVec(List<String> tokensSearching, Map<String, Double> tfIdfs) {
        Double[] vec = new Double[tokensSearching.size()];
        int i = 0;
        for (String token : tokensSearching) {
            if (tfIdfs.containsKey(token)) {
                vec[i] = tfIdfs.get(token);
            } else {
                vec[i] = Double.valueOf(0);
            }
            i++;
        }

        return vec;
    }


    public static void main(String[] args) {


        String serachingContent = "roasted fish and chips and sauce and chilli";
        String contentDF = Util.readFiletoString(Util.testFilePath + "tk0.txt", Util.encodingUTF8);
        String content = Util.readFiletoString(Util.testFilePath + "tk1.txt", Util.encodingUTF8);

        List<String> menus = new ArrayList<String>();
        menus.add(contentDF);
        menus.add(serachingContent);


        StringBuilder stringBuilder = new StringBuilder();
        for (String menu : menus) {
            stringBuilder.append(menu);
        }

        //Annotate all aveliable docs
        CoreDocument documents = getAnootatedDocument(stringBuilder.toString());
        List<String> tokensAfterLemma = getLemmizationAndUseStopWords(documents.tokens());
        Counter<String> dfCounter = getTermFrequencies(tokensAfterLemma);
        int numRestaurants = menus.size();


        /* Counter<String> dfCounter = DFCounter.countDF(contentDF);*/

        Algorithm algorithm = new Algorithm(dfCounter, numRestaurants);

        List<String> tokensSearching = algorithm.getLemmizationAndUseStopWords(
                (getAnootatedDocument(serachingContent)).tokens());

        Double[] vec = transformToVec(tokensSearching, algorithm.getTfIdfs(content));


        Double[] vecSearching = transformToVec(tokensSearching, algorithm.getTfIdfs(tokensSearching));

        log.info(CosineSimilarity.cosineSimilarity(vecSearching, vec));

        //test
        String content1 = Util.readFiletoString(Util.testFilePath + "tk2.txt", Util.encodingUTF8);

        Double[] vec1 = transformToVec(tokensSearching, algorithm.getTfIdfs(content1));

        log.info(CosineSimilarity.cosineSimilarity(vecSearching, vec1));


    }

}