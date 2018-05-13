package actors.menuClassifier.classifier;

import java.io.*;
import java.util.List;

import actors.menuClassifier.classifier.OpenNLP.OpenNLP;
import actors.menuClassifier.classifier.StanfordNLP.StanfordNLP;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import opennlp.tools.util.normalizer.*;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Algorytm do klasyfikowania i wyszuzkiwania menu najlepiej dopasowanego do preferencji
 */
public class Algorithm {

    private final Logger logger = LoggerFactory.getLogger(Algorithm.class);

    public static void main(String[] args) throws IOException {

        String filePath = "data/tests/tk.txt";
        String encoding = "UTF-8";

        OpenNLP.tokenization(filePath,encoding);
        StanfordNLP.tokenization(filePath, encoding);


    }
}