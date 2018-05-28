package actors.menuClassifier.classifier;


import actors.restaurantResearcher.CommonRestaurant;
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
    private List<RestaurantClassifierWrapper> restaurantsCW;
    private List<String> tokensSearching;
    private double[] vecSearching;
    /*
    1 - Maximum tf normalization
    2 - Sublinear tf scaling
     */
    private int selectTfIDFWeight = 1;

    public Algorithm(Counter<String> dfCounter, int numDocuments) {
        this.dfCounter = dfCounter;
        this.numDocuments = numDocuments;
    }

    public Algorithm(List<CommonRestaurant> restaurants, List<String> searchingMenus) {
        this.numDocuments = restaurants.size();

        String searchingMenu = searchingMenus.get(0);
        this.tokensSearching = getLemmizationAndUseStopWords((getAnnotatedDocument(searchingMenu)).tokens());

        List<RestaurantClassifierWrapper> restaurantsCW = new ArrayList<RestaurantClassifierWrapper>();

        Counter<String> dfCounter = new ClassicCounter<String>();

        for (CommonRestaurant restaurant : restaurants) {

            RestaurantClassifierWrapper restaurantCW = new RestaurantClassifierWrapper(restaurant);

            CoreDocument document = getAnnotatedDocument(restaurant.getDailyMenu());
            List<String> tokensAfterLemma = getLemmizationAndUseStopWords(document.tokens());
            Counter<String> termFrequencies = getTermFrequencies(tokensAfterLemma);

            restaurantCW.setTokensAfterLemma(tokensAfterLemma);
            restaurantCW.setTermFrequencies(termFrequencies);
            dfCounter.addAll(termFrequencies);
            restaurantsCW.add(restaurantCW);
        }

        this.dfCounter = dfCounter;
        this.restaurantsCW = restaurantsCW;
    }

    private void copmputeTfIdfAndTransformToVec() {
        for (RestaurantClassifierWrapper restaurantCW : this.getRestaurantsCW()) {
            restaurantCW.setTfIdfs(getTfIdfs(restaurantCW));
            restaurantCW.setVec(transformToVec(tokensSearching, restaurantCW.getTfIdfs()));
        }
        this.setVecSearching(transformToVec(tokensSearching, getTfIdfs(tokensSearching)));
    }

    private void computeCosineSimilarity() {
        for (RestaurantClassifierWrapper restaurantCW : this.getRestaurantsCW()) {
            restaurantCW.setCosineSimilarity(CosineSimilarity.cosineSimilarity(restaurantCW.getVec(), this.getVecSearching()));
        }
    }

    private void sort() {
        Collections.sort(this.restaurantsCW, new CosineSimilarityComaprator());
    }

    private static List<String> loadStopWords() {

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

    private static List<String> getLemmizationAndUseStopWords(List<CoreLabel> tokens) {
        List<String> tokensAfterLemma = new ArrayList<String>();

        for (CoreLabel token : tokens) {
            if (!stopWords.contains(token.value())) {
                tokensAfterLemma.add(token.lemma());
            }
        }
        return tokensAfterLemma;
    }

    private static CoreDocument getAnnotatedDocument(String documentContent) {
        CoreDocument document = new CoreDocument(documentContent);
        pipeline.annotate(document);
        return document;
    }


    private Map<String, Double> getTfIdfs(String documentContent) {

        CoreDocument document = getAnnotatedDocument(documentContent);

        List<String> tokensAfterLemma = getLemmizationAndUseStopWords(document.tokens());

        Counter<String> tfs = getTermFrequencies(tokensAfterLemma);

        double tfMax = Collections.max(tfs.entrySet(), Map.Entry.comparingByValue()).getValue();

        Map<String, Double> tfIdfs = new HashMap<String, Double>();

        for (String token : tokensAfterLemma) {
            if (selectTfIDFWeight == 1) {
                tfIdfs.put(token, TfIdf.tfIDFWeightNormalize(token, tfs, this.numDocuments, this.dfCounter, tfMax));
            } else {
                tfIdfs.put(token, TfIdf.tfIDFWeightSublinear(token, tfs, this.numDocuments, this.dfCounter));
            }
        }

        return tfIdfs;

    }

    private Map<String, Double> getTfIdfs(RestaurantClassifierWrapper restaurantCW) {

        Counter<String> tfs = restaurantCW.getTermFrequencies();

        Map<String, Double> tfIdfs = new HashMap<String, Double>();

        double tfMax = Collections.max(tfs.entrySet(), Map.Entry.comparingByValue()).getValue();

        for (String token : restaurantCW.getTokensAfterLemma()) {
            if (selectTfIDFWeight == 1) {
                tfIdfs.put(token, TfIdf.tfIDFWeightNormalize(token, tfs, this.numDocuments, this.dfCounter, tfMax));
            } else {
                tfIdfs.put(token, TfIdf.tfIDFWeightSublinear(token, tfs, this.numDocuments, this.dfCounter));
            }
        }

        return tfIdfs;

    }

    private Map<String, Double> getTfIdfs(List<String> tokensAfterLemma) {

        Counter<String> tfs = getTermFrequencies(tokensAfterLemma);

        double tfMax = Collections.max(tfs.entrySet(), Map.Entry.comparingByValue()).getValue();

        Map<String, Double> tfIdfs = new HashMap<String, Double>();

        for (String token : tokensAfterLemma) {
            if (selectTfIDFWeight == 1) {
                tfIdfs.put(token, TfIdf.tfIDFWeightNormalize(token, tfs, this.numDocuments, this.dfCounter, tfMax));
            } else {
                tfIdfs.put(token, TfIdf.tfIDFWeightSublinear(token, tfs, this.numDocuments, this.dfCounter));
            }
        }

        return tfIdfs;

    }

    private static double[] transformToVec(List<String> tokensSearching, Map<String, Double> tfIdfs) {
        double[] vec = new double[tokensSearching.size()];
        int i = 0;
        for (String token : tokensSearching) {
            if (tfIdfs.containsKey(token)) {
                vec[i] = tfIdfs.get(token);
            } else {
                vec[i] = 0.0;
            }
            i++;
        }

        return vec;
    }


    public static void main(String[] args) {
        test1();
    }

    public static void test1() {
        String searchingMenu1 = "Roasted fish and chips and sauce and cucumber.";
        String searchingMenu2 = "Lemon-sesame salsa.";
        List<String> searchingMenus = new ArrayList<String>();
        searchingMenus.add(searchingMenu1);
        searchingMenus.add(searchingMenu2);

        List<CommonRestaurant> restaurantsCollection = new ArrayList<CommonRestaurant>();

        restaurantsCollection.add(new CommonRestaurant(1, "zomato", "Roasted fish and chips.",
                "U Szymona", "Plac Szymiego 23/147"));

        restaurantsCollection.add(new CommonRestaurant(2, "zomato", "Gravlax in lemon-sesame salsa with " +
                "fresh coriander, chilli and cucumber.",
                "U Jacka", "Plac Jackowsikiego"));

        restaurantsCollection.add(new CommonRestaurant("1", "google", searchingMenu1 + " " + searchingMenu2,
                "Pod potężnym Dominikiem", "Plac Wielkiego Dzika 21/37"));

        Algorithm algorithm = new Algorithm(restaurantsCollection, searchingMenus);
        algorithm.classifyRestaurants();

        log.info("Scores tfidf Method 1:");
        for (RestaurantClassifierWrapper restaurantCW : algorithm.getRestaurantsCW()) {
            log.info("Score :" + restaurantCW.getCosineSimilarity() + "\tMenu: " + restaurantCW.getRestaurant().getDailyMenu());
        }


    }

    public void classifyRestaurants() {
        this.copmputeTfIdfAndTransformToVec();
        this.computeCosineSimilarity();
        this.sort();
    }


}