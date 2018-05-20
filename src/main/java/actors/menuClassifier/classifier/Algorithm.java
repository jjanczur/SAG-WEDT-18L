package actors.menuClassifier.classifier;

import actors.menuClassifier.classifier.OpenNLP.OpenNLP;
import actors.menuClassifier.classifier.StanfordNLP.StanfordNLP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/***
 * Algorytm do klasyfikowania i wyszuzkiwania menu najlepiej dopasowanego do preferencji
 */
public class Algorithm {

    private final Logger logger = LoggerFactory.getLogger(Algorithm.class);

    public static void main(String[] args) throws IOException {

        String filePath = "data/tests/tk0.txt";
        String encoding = "UTF-8";

        OpenNLP.tokenization(filePath, encoding);
        StanfordNLP.tokenization(filePath, encoding);


    }
}