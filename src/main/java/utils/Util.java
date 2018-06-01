package utils;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Util {
    private static final Logger log = Logger.getLogger(Util.class);

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


    /**
     * Method to create random menu. It reads nOfLines from dish_list_rand.txt starting from a random line
     * @param nOfLines number of lines to be read from the dish_list_rand.txt file
     * @return concatenated nOfLines from  dish_list_rand.txt
     */
    public String createRandomMenu(int nOfLines){
        String fileName = "dish_list_rand.txt";
        ClassLoader classLoader = Util.class.getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());        int salt = ThreadLocalRandom.current().nextInt(0, 424000 + 1);
        StringBuilder menuBuilder = new StringBuilder();

        List fileLinesList = null;
        try {
            fileLinesList = FileUtils.readLines(file, "UTF-8");
        } catch (IOException e) {
            log.error("Couldn't read the file " + fileName + e.getMessage(), e);
            e.printStackTrace();
        }

        for(int i = salt; i<salt+nOfLines; i++) {
            menuBuilder.append(fileLinesList != null ? fileLinesList.get(i) : "");
        }

        return menuBuilder.toString();
    }




}
