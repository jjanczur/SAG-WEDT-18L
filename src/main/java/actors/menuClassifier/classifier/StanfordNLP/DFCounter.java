package actors.menuClassifier.classifier.StanfordNLP;

import edu.stanford.nlp.io.ReaderInputStream;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class DFCounter {

    private static final MaxentTagger tagger = new MaxentTagger(MaxentTagger.DEFAULT_JAR_PATH);
    private static final Pattern headingSeparator = Pattern.compile("[-=]{3,}");
    private static final Pattern paragraphMarker =
            Pattern.compile("</?(?:TEXT|P)>(\n|$)");


    /**
     * Get an IDF map for the given document string.
     *
     * @param document
     * @return
     */
    private static Counter<String> getIDFMapForDocument(String document) {
        // Clean up -- remove some Gigaword patterns that slow things down
        // / don't help anything
        document = headingSeparator.matcher(document).replaceAll("");

        DocumentPreprocessor preprocessor = new DocumentPreprocessor(new StringReader(document));
        //preprocessor.setTokenizerFactory(tokenizerFactory);

        Counter<String> idfMap = new ClassicCounter<String>();
        for (List<HasWord> sentence : preprocessor) {

            List<TaggedWord> tagged = tagger.tagSentence(sentence);

            //todo: Ogarnąć to
            //O co tutaj chodzi? Było to zrobione pod Hiszpański być może bierzemy tylko rzeczowniki ?
            for (TaggedWord w : tagged) {
                //zakomentowane bo jest to bez sesnu dla języka angielskiego
                /*if (w.tag().startsWith("N"))*/
                idfMap.incrementCount(w.word());
            }
        }
        return idfMap;
    }

    private static final String TAG_DOCUMENT = "doc";
    /* private static final String TAG_TEXT = "TEXT";*/

    /**
     * Get an IDF map for all the documents in the given file.
     *
     * @param file
     * @return
     */
    private static Counter<String> getIDFMapForFile(Reader file)
            throws SAXException, IOException, TransformerException {

        DocumentBuilder parser = XMLUtils.getXmlParser();
        Document xml = parser.parse(new ReaderInputStream(file));
        NodeList docNodes = xml.getElementsByTagName(TAG_DOCUMENT);

        Element doc;
        Counter<String> idfMap = new ClassicCounter<String>();
        for (int i = 0; i < docNodes.getLength(); i++) {
            doc = (Element) docNodes.item(i);
            /*
            NodeList texts = doc.getElementsByTagName(TAG_TEXT);
            assert texts.getLength() == 1;

            Element text = (Element) texts.item(0);
            String textContent = getFullTextContent(text);
            */

            String textContent = doc.getFirstChild().getTextContent();
            idfMap.addAll(getIDFMapForDocument(textContent));
            // Increment magic counter
            idfMap.incrementCount("__all__");
        }

        return idfMap;
    }


    public static Counter<String> countDF(String content) throws ExecutionException, InterruptedException, TransformerException, SAXException, IOException, ParserConfigurationException {

        List<Counter<String>> list = new ArrayList<Counter<String>>();
        String taggedContent = "<docs>" + content + "</docs>";

        list.add(getIDFMapForFile(new StringReader(taggedContent)));
        Counter<String> overall = new ClassicCounter<String>();

        for (Counter<String> counter : list) {
            overall.addAll(counter);
        }

        return overall;
    }

}