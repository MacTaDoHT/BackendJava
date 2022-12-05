package HW3;

import org.junit.jupiter.api.BeforeAll;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class AbstractTest {

    static Properties prop = new Properties();
    private static String apiKey;
    private static String baseUrl;

    @BeforeAll
    static void initTest() throws IOException {
        InputStream configFile = new FileInputStream("src/main/my.properties");
        prop.load(configFile);

        apiKey =  prop.getProperty("apiKey");
        baseUrl= prop.getProperty("base_url");
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }
}