package actors.menuClassifier.classifier;

import edu.stanford.nlp.stats.Counter;

public class TfIdf {
    public static double tfIDFWeight(String word, Counter<String> termFrequencies, int numDocuments, Counter<String> dfCounter) {
        if (dfCounter.getCount(word) == 0)
            return 0;

        double tf = 1 + Math.log(termFrequencies.getCount(word));
        double idf = Math.log(numDocuments / (1 + dfCounter.getCount(word)));
        return tf * idf;
    }
}
