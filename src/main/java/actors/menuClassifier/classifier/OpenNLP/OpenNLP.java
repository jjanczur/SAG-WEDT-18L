package actors.menuClassifier.classifier.OpenNLP;

import actors.menuClassifier.classifier.Algorithm;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import opennlp.tools.util.normalizer.*;
import utils.Util;

import java.io.IOException;
import java.io.InputStream;

public class OpenNLP {




    public static void tokenization(String filePath, String encoding) throws IOException {


        String sentence = Util.readFiletoString(filePath,encoding);


        //Loading the Tokenizer model
        InputStream inputStream = Algorithm.class.getClassLoader().getResourceAsStream("data/tokenizer/en-token.bin");
        TokenizerModel tokenModel = new TokenizerModel(inputStream);

        //Instantiating the TokenizerME class
        TokenizerME tokenizer = new TokenizerME(tokenModel);

        /*  //Tokenizing the given raw text
        String tokens[] = tokenizer.tokenize(sentence);

        */
        //Retrieving the positions of the tokens
        Span tokens[] = tokenizer.tokenizePos(sentence);

        //Getting the probabilities of the recent calls to tokenizePos() method
        double[] probs = tokenizer.getTokenProbabilities();

        //Printing the spans of tokens
        for(Span token : tokens){
            System.out.println(token +" "+sentence.substring(token.getStart(), token.getEnd()));
            System.out.println("Token przed normalizacją: " +sentence.substring(token.getStart(), token.getEnd())+ "   i Po: "+
                    OpenNLP.EmojiCharSequenceNormalizer(sentence.substring(token.getStart(), token.getEnd())));

        }
        System.out.println("  ");
        for(int i = 0; i<probs.length; i++)
            System.out.println(probs[i]);
    }


    /***
     * Replaces emojis by blank space
     * @param token
     * @return
     */
    public static String EmojiCharSequenceNormalizer(String token){

        return  EmojiCharSequenceNormalizer.getInstance().normalize((CharSequence)token).toString();

    }

    /***
     * Replaces URLs and E-Mails by a blank space.
     * @param token
     * @return
     */
    public static String UrlCharSequenceNormalizer(String token){

        return  UrlCharSequenceNormalizer.getInstance().normalize((CharSequence)token).toString();

    }

    /***
     *	Replaces hashtags and Twitter user names by blank spaces.
     * @param token
     * @return
     */
    public static String TwitterCharSequenceNormalizer(String token){

        return  TwitterCharSequenceNormalizer.getInstance().normalize((CharSequence)token).toString();

    }


    /***
     *	Replaces number sequences by blank spaces
     * @param token
     * @return
     */
    public static String NumberCharSequenceNormalizer(String token){

        return  NumberCharSequenceNormalizer.getInstance().normalize((CharSequence)token).toString();

    }

    /***
     * Shrink characters that repeats three or more times to only two repetitions
     * @param token
     * @return
     */
    public static String ShrinkCharSequenceNormalizer(String token){

        return  ShrinkCharSequenceNormalizer.getInstance().normalize((CharSequence)token).toString();

    }
}
