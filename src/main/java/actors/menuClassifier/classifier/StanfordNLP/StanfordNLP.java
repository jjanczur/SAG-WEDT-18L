package actors.menuClassifier.classifier.StanfordNLP;

import actors.menuClassifier.classifier.Algorithm;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class StanfordNLP {

    /***
     * Do normalizjacji w stanfordNLP mozemy użyć nastepujacych opcji
     *     normalizeSpace: Whether any spaces in tokens (phone numbers, fractions get turned into U+00A0 (non-breaking space). It's dangerous to turn this off for most of our Stanford NLP software, which assumes no spaces in tokens.
     *     normalizeAmpersandEntity: Whether to map the XML &amp; to an ampersand
     *     normalizeCurrency: Whether to do some awful lossy currency mappings to turn common currency characters into $, #, or "cents", reflecting the fact that nothing else appears in the old PTB3 WSJ. (No Euro!)
     *     normalizeFractions: Whether to map certain common composed fraction characters to spelled out letter forms like "1/2"
     *     normalizeParentheses: Whether to map round parentheses to -LRB-, -RRB-, as in the Penn Treebank
     *     normalizeOtherBrackets: Whether to map other common bracket characters to -LCB-, -LRB-, -RCB-, -RRB-, roughly as in the Penn Treebank
     * @param filePath
     * @param encoding
     * @throws IOException
     */

    public static void tokenization(String filePath, String encoding) throws IOException {


        ClassLoader classLoader = Algorithm.class.getClassLoader();
        File file = new File(classLoader.getResource(filePath).getFile());

        Reader reader = new FileReader(file);

        // option #1: By sentence.
        DocumentPreprocessor dp = new DocumentPreprocessor(reader);
        for (List<HasWord> sentence : dp) {
            System.out.println(sentence);
        }

        // option #2: By token
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>( new FileReader(file),
                new CoreLabelTokenFactory(),
                "invertible,normalizeSpace,normalizeCurrency,normalizeParentheses,normalizeFractions=true");

        while (ptbt.hasNext()) {
            CoreLabel label = ptbt.next();
            System.out.println(label);
        }


    }


}
