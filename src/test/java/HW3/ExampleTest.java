package HW3;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExampleTest extends AbstractTest {
    public String get_path = "recipes/complexSearch?";
    public String post_path = "recipes/cuisine";
    private static String recipeTitle;
    private static final String hashYuriy1305 = "b5b9315a52d967293d78c4a875641bb7a14615a7";

    @BeforeAll
    static void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void getTests() {
        Integer totalResults = given()
                .when()
                .get(getBaseUrl() + get_path + "apiKey=" + getApiKey())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("totalResults");
        if (totalResults > 0) {
            System.out.println("GETs:\n1: Успешно, код: 200.\n" + "Рецептов нашли, штук - " + totalResults);
        } else {
            System.out.println("1: Успешно, код: 200,\n" + " но ничего не нашли.");
        }
        totalResults = given()
                .queryParam("cuisine", "Italian")
                .queryParam("intolerances", "gluten")
                .queryParam("maxCalories", "25")
                .log().all()
                .response()
                .expect()
                .body("totalResults", equalTo(2))
                //
                .when()
                .get(getBaseUrl() + get_path + "apiKey=" + getApiKey())
                .then()
                .extract()
                .jsonPath()
                .get("totalResults");
        System.out.println("2: Нашли " + totalResults + " низколорийных безглютеновых блюда итальянской кухни.");
        totalResults = given()
                .queryParam("includeIngredients", "1,2,3")
                // "предпроверка" на 0 блюда:
                .response()
                .expect()
                .body("totalResults", equalTo(0))
                //
                .when()
                .get(getBaseUrl() + get_path + "apiKey=" + getApiKey())
                .then()
                .extract()
                .jsonPath()
                .get("totalResults");
        System.out.println("3: Нашли " + totalResults + " блюд с несуществующими ингредиентами.");
        totalResults = given()
                .queryParam("includeIngredients", "meat,fish,potato")
                .queryParam("minIron", "10")
                .queryParam("minSugar", "10")
                .queryParam("minCalories", "100")
                .queryParam("minMagnesium", "10")
                .response()
                .expect()
                .body("totalResults", equalTo(1))
                //
                .when()
                .get(getBaseUrl() + get_path + "apiKey=" + getApiKey())
                .then()
                .extract()
                .jsonPath()
                .get("totalResults");
        System.out.println("4: Нашли рыбно-мясных блюд с прочими ограничениями - " + totalResults + ".");
        JsonPath response = given()
                .queryParam("cuisine", "Indian")
                .when()
                .get(getBaseUrl() + get_path + "apiKey=" + getApiKey())
                .body()
                .jsonPath();
        assertThat(response.get("totalResults"), CoreMatchers.<Object>not(0));
        recipeTitle = response.get("results.title[0]");
        System.out.println("5: Название блюда индийской кухни: " + recipeTitle);
    }

    @Test
    void postTests() {
        String cuisine = given()
                .queryParam("apiKey", getApiKey())
                .when()
                .post(getBaseUrl() + post_path)
                .path("cuisine");
        assertThat(cuisine, anyOf(containsString("Italian"),
                containsString("Mediterranean"),
                containsString("European")));
        System.out.println("POSTs\n1: Кухня при запросе без параметров (ответ по умолчанию) итальянско-средиземноморско-европейская: " + cuisine);
        cuisine = given()
                .queryParam("apiKey", getApiKey())
                .contentType("application/x-www-form-urlencoded")
                .formParam("title", "Burger")
                .formParam("ingredientList", "tuna")
                .when()
                .post(getBaseUrl() + post_path)
                .then().extract()
                .jsonPath()
                .get("cuisine")
                .toString();
        System.out.println("2: Бургер с тунцом оказался - " + cuisine + "!");
        cuisine = given()
                .queryParam("apiKey", getApiKey())
                .formParam("title", "Traditional Chinese Recipes")
                .when()
                .post(getBaseUrl() + post_path)
                .path("cuisine");
        assertThat(cuisine, equalTo("Chinese"));
        System.out.println("3: Традиционная Китайская кухня - " + cuisine);
// 4й POST: Ну и проверим чьи только что съеденные чурросы:
        JsonPath response = given()
                .queryParam("apiKey", getApiKey())
                .formParam("title", "Churros")
                .when()
                .post(getBaseUrl() + post_path)
                .body()
                .jsonPath();
        assertThat(response.get("cuisines[1]"), CoreMatchers.<Object>equalTo("Spanish"));
        System.out.println("4: Чурросы - " + response.get("cuisines[1]") + ". Угадал! :)");
// 5й POST: С title из GET (индийский рецерт - правда ли, индийская кухня?)
// !!!  и не забываем запускать 5й GET тест, т.к. переменная=рецепт оттуда!!!
        cuisine = given()
                .queryParam("apiKey", getApiKey())
                .formParam("title", recipeTitle)
                .when()
                .post(getBaseUrl() + post_path)
                .prettyPeek()
                .path("cuisine");
        assertThat(cuisine, equalTo("Indian"));
        System.out.println("5: Индийский рецепт из GET действительно индийский? " + cuisine.equals("Indian"));
    }

    @Test
    void addDish_getList_deleteDishTest() {
        String urlChain = getBaseUrl() + "mealplanner/yuriy1305/shopping-list";

        String idCreate = given()
                .queryParam("hash", hashYuriy1305)
                .queryParam("apiKey", getApiKey())
                .body("{\n"
                        + " \"item\": \"РусскаяКухня\",\n"
                        + " \"aisle\": \"КартошкаССеледкой\",\n"
                        + " \"parse\": true \n"
                        + "}")
                .when()
                .post(urlChain+"/items")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("id")
                .toString();
        System.out.println("1й шаг: Создано блюдо с id= " + idCreate);

        String idDish=given()
                .when()
                .get(urlChain + "?hash="+ hashYuriy1305+"&apiKey=" + getApiKey())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("aisles.items[0].id[0]")
                .toString();
        System.out.println("2й шаг: Есть ли блюдо с созданным id? " + idCreate.equals(idDish));

        given()
                .queryParam("hash", hashYuriy1305)
                .queryParam("apiKey", getApiKey())
                .delete(urlChain +"/items/" + idCreate)
                .then()
                .statusCode(200);
        System.out.println("3й шаг: Успешно удалено блюдо с id= " + idDish);
    }
}