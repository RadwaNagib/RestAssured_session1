package Utilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Properties;

public class DataUtils {

    public final static String Test_Data="src/main/resources/testData.user/user.json";
    public final static String Enviroment_Path="src/main/resources/testData/Enviroment.properties";


    //TODO: Read data from json file
    public static String getJsonData(String jsonFilename, String field) {
        try {
            // Define object of file Reader
            FileReader reader = new FileReader(Test_Data + jsonFilename + ".json");
            // Parse the JSON directly into a JsonElement
            JsonElement jsonElement = JsonParser.parseReader(reader);
            return jsonElement.getAsJsonObject().get(field).getAsString();
        } catch (Exception e) {
            System.out.println("error on getting data from test-data file");;
        }
        return "";
    }


    //TODO: get properties from .properties file
    public static String getEnvironmentPropertyValue(String key) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(Enviroment_Path));
            return properties.getProperty(key);
        } catch (Exception e) {
            System.out.println("can't Access BASE URL");
            return "";
        }
    }

}
