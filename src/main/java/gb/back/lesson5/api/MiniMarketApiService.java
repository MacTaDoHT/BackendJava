package gb.back.lesson5.api;

import gb.back.lesson5.model.Product;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

public class MiniMarketApiService {

    private final MiniMarketApi api;

    public MiniMarketApiService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8189/market/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(MiniMarketApi.class);
    }

    public List<Product> getProducts() throws IOException {
        return api.getProducts()
                .execute()
                .body();
    }

    public Product getProduct(long id) throws IOException {
        Response<Product> response = api.getProduct(id).execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new RuntimeException(response.errorBody().string());
        }
    }

    public Long createProduct(Product product) throws IOException {
        return api.createProduct(product)
                .execute()
                .body()
                .getId();
    }

    public void deleteProduct(long id) throws IOException {
        api.deleteProduct(id).execute();
    }

    public void updateProduct(Product product) throws IOException {
        api.updateProduct(product).execute();
    }
}
