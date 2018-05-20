package actors.menuClassifier.classifier.StanfordNLP;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.patterns.PhraseScorer;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.util.CoreMap;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.xml.sax.SAXException;
import utils.Util;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author Jon Gauthier
 */
public class Summarizer {

    private static final StanfordCoreNLP pipeline;

    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        pipeline = new StanfordCoreNLP(props);
    }

    private final Counter<String> dfCounter;
    private final int numDocuments;

    public Summarizer(Counter<String> dfCounter) {
        this.dfCounter = dfCounter;
        this.numDocuments = (int) dfCounter.getCount("__all__");
    }

    private static Counter<String> getTermFrequencies(List<CoreMap> sentences) {
        Counter<String> ret = new ClassicCounter<String>();

        for (CoreMap sentence : sentences)
            for (CoreLabel cl : sentence.get(CoreAnnotations.TokensAnnotation.class))
                ret.incrementCount(cl.get(CoreAnnotations.TextAnnotation.class));

        return ret;
    }

    private class SentenceComparator implements Comparator<CoreMap> {
        private final Counter<String> termFrequencies;

        public SentenceComparator(Counter<String> termFrequencies) {
            this.termFrequencies = termFrequencies;
        }

        @Override
        public int compare(CoreMap o1, CoreMap o2) {
            return (int) Math.round(score(o2) - score(o1));
        }

        /**
         * Compute sentence score (higher is better).
         */
        private double score(CoreMap sentence) {
            double tfidf = tfIDFWeights(sentence);

            //TODO: Chyba nam to niepotrzebne?
            // Weight by position of sentence in document
/*            int index = sentence.get(CoreAnnotations.SentenceIndexAnnotation.class);
            double indexWeight = 5.0 / index;

            return indexWeight * tfidf * 100;*/
            //* 100 żeby ładniej wyglądało
            return  tfidf * 100;
        }

        private double tfIDFWeights(CoreMap sentence) {
            double total = 0;
            for (CoreLabel cl : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                //TODO: ogarnąć po co to
                //Znowu starts with n
                /* if (cl.get(CoreAnnotations.PartOfSpeechAnnotation.class).startsWith("n"))*/
                total += tfIDFWeight(cl.get(CoreAnnotations.TextAnnotation.class));
            }

            return total;
        }

        private double tfIDFWeight(String word) {
            if (dfCounter.getCount(word) == 0)
                return 0;

            double tf = 1 + Math.log(termFrequencies.getCount(word));
            double idf = Math.log(numDocuments / (1 + dfCounter.getCount(word)));
            return tf * idf;
        }
    }

    private List<CoreMap> rankSentences(List<CoreMap> sentences, Counter<String> tfs) {
        Collections.sort(sentences, new SentenceComparator(tfs));
        return sentences;
    }

    public String summarize(String document) {
        Annotation annotation = pipeline.process(document);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        Counter<String> tfs = getTermFrequencies(sentences);

        sentences = rankSentences(sentences, tfs);

        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < sentences.size(); i++) {
            ret.append(sentences.get(i));
            ret.append("\n");
        }

        return ret.toString();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException, TransformerException, SAXException, ParserConfigurationException {


        String contentDF = Util.readFiletoString(Util.testFilePath + "tk0.txt", Util.encodingUTF8);
        String content = Util.readFiletoString(Util.testFilePath + "tk1.txt", Util.encodingUTF8);
        Counter<String> dfCounter = DFCounter.countDF(contentDF);

        Summarizer summarizer = new Summarizer(dfCounter);
        String result = summarizer.summarize(content);

        System.out.println(result);
    }

}