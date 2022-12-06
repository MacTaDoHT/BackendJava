package HW4;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class AbstractTest {
    static Properties prop = new Properties();
    //        private static String apiKey;
    private static String baseUrl;
    private static String hashYuriy1305;
    protected static ResponseSpecification responseSpecification;
    protected static RequestSpecification requestSpecification;

    @BeforeAll
    static void initTest() throws IOException {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        InputStream configFile = new FileInputStream("src/main/my.properties");
        prop.load(configFile);

        baseUrl = prop.getProperty("base_url");
        hashYuriy1305 = prop.getProperty("hashYuriy1305");

        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .log(LogDetail.ALL)
                .build();

        requestSpecification = new RequestSpecBuilder()
                .addQueryParam("apiKey", prop.getProperty("apiKey"))
                .addQueryParam("language", "en")
                .setContentType(ContentType.JSON)
                .build();

        RestAssured.responseSpecification = responseSpecification;
        RestAssured.requestSpecification = requestSpecification;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }
    public static String gethash() { return hashYuriy1305; }
}