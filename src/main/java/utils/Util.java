package utils;

import actors.menuClassifier.classifier.Algorithm;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Util {


    public static String readFiletoString(String filePath, String encoding) {

        String data = "";
        ClassLoader classLoader = Algorithm.class.getClassLoader();
        File file = new File(classLoader.getResource(filePath).getFile());
        try {
            data = FileUtils.readFileToString(file, encoding);

        } catch (IOException e) {
            e.printStackTrace();

        }
        return data;

    }
}
