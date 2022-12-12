package miniMarketApiTest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gb.back.lesson5.api.MiniMarketApiService;
import gb.back.lesson5.model.Product;
import org.junit.jupiter.api.*;


import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MiniMarketApiTest {

    private static MiniMarketApiService apiService;
    private static Gson gson;
    private static Long id = 0L;

    @BeforeAll
    static void beforeAll() {
        apiService = new MiniMarketApiService();
        gson = new Gson();
    }

    @Test
    void testGetProductByIdProductExist() throws IOException {

        Product product = apiService.getProduct(1);
        Assertions.assertEquals(1L, product.getId());
        Assertions.assertEquals("Milk", product.getTitle());
        Assertions.assertEquals("Food", product.getCategoryTitle());
    }

    @Test
    void testGetProductByIdProductNotExists() {

        Assertions.assertThrows(RuntimeException.class, () -> {
            Product product = apiService.getProduct(100);
        });
    }

    @Test
    void testGetProducts() throws IOException {
        Type type = new TypeToken<ArrayList<Product>>() {
        }.getType();
        String json = getJsonResource("testGetProducts/expected.json");
        List<Product> expected = gson.fromJson(json, type);
        List<Product> actually = apiService.getProducts();
        Assertions.assertEquals(expected.size(), actually.size());
        for (int i = 0; i < expected.size(); i++) {
            assertProduct(expected.get(i), actually.get(i));
        }
    }

    @Test
    @Order(1)
    void testCreateNewProduct() throws IOException {
        Product product = Product.builder()
                .categoryTitle("Food")
                .price(300)
                .title("Fish")
                .build();
        id = apiService.createProduct(product);
        Product expected = apiService.getProduct(id);
        Assertions.assertEquals(id, expected.getId());
    }

    @Test
    @Order(2)
    void testDeleteById() throws IOException {

        Assertions.assertThrows(EOFException.class, () -> {
            apiService.deleteProduct(id);
        });

        Assertions.assertThrows(RuntimeException.class, () -> {
            apiService.getProduct(id);
        });

    }

    String getJsonResource(String resource) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(getClass().getResource(resource).getFile()));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    void assertProduct(Product expected, Product actually) {
        Assertions.assertEquals(expected.getId(), actually.getId());
        Assertions.assertEquals(expected.getTitle(), actually.getTitle());
        Assertions.assertEquals(expected.getCategoryTitle(), actually.getCategoryTitle());
        Assertions.assertEquals(expected.getPrice(), actually.getPrice());
    }

}
