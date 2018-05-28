package utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Util {

    public static String log4jConfPath = "/conf/log4j.properties";

    public static String testFilePath = "data/tests/";

    public static String encodingUTF8 = "UTF-8";

    public static String readFiletoString(String filePath, String encoding) {

        String data = "";
        File file = getFileFromResource(filePath);
        try {
            data = FileUtils.readFileToString(file, encoding);

        } catch (IOException e) {
            e.printStackTrace();

        }
        return data;

    }

    public static File getFileFromResource(String filePath) {

        ClassLoader classLoader = Util.class.getClassLoader();
        File file = new File(classLoader.getResource(filePath).getFile());
        return file;
    }

    public static File getTestFile() {

        ClassLoader classLoader = Util.class.getClassLoader();
        File file = new File(classLoader.getResource(Util.testFilePath).getFile());
        return file;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }


}
