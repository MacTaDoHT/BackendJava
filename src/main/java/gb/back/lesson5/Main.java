package gb.back.lesson5;

import gb.back.lesson5.api.MiniMarketApi;
import gb.back.lesson5.model.Product;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8189/market/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MiniMarketApi api = retrofit.create(MiniMarketApi.class);
//        Call<Object> productsCall = api.getProducts();
//
//        Response<Object> response= productsCall.execute();
//        ArrayList body = (ArrayList) response.body();
//        System.out.println(body.get(0).getClass());
//        System.out.println(body);

        Response<List<Product>> response = api.getProducts().execute();

        List<Product> products = response.body();

        System.out.println(products);
    }
}
