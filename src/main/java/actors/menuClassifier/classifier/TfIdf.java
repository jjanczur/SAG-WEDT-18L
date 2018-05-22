package actors.menuClassifier.classifier;

import edu.stanford.nlp.stats.Counter;

public class TfIdf {
    public static double tfIDFWeightNormalize(String word, Counter<String> termFrequencies, int numDocuments, Counter<String> dfCounter, double tfMax) {
        if (dfCounter.getCount(word) == 0)
            return 0;

        //Maximum tf normalization
        /*
        The main idea of maximum tf normalization is to mitigate the following anomaly:
        we observe higher term frequencies in longer documents, merely because longer
        documents tend to repeat the same words over and over again.
        */

        double a = 0.4;
        double tf = a + (1 - a) * (termFrequencies.getCount(word))/tfMax;
        double idf = Math.log(numDocuments / (dfCounter.getCount(word)));
        return tf * idf;
    }


    public static double tfIDFWeightSublinear(String word, Counter<String> termFrequencies, int numDocuments, Counter<String> dfCounter) {
        if (dfCounter.getCount(word) == 0)
            return 0;

        //Sublinear tf scaling
        double tf = 1 + Math.log(termFrequencies.getCount(word));
        double idf = Math.log(numDocuments / (dfCounter.getCount(word)));
        return tf * idf;
    }
}
