package HW6;

import HW6.db.dao.ProductsMapper;
import HW6.db.model.Products;
import HW6.db.model.ProductsExample;
import com.github.javafaker.Faker;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestsProducts {
    static SqlSession session = null;
    static Faker faker = new Faker();

    @BeforeAll
    static void beforeAll() {
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        SqlSessionFactory sqlSessionFactory;
        {
            try {
                inputStream = Resources.getResourceAsStream(resource);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        }
        session = sqlSessionFactory.openSession();
    }
    @Test
    void create_deleteProduct() {
        Long idNewProduct;

        Products products = new Products();
        products.setTitle(faker.food().ingredient());
        products.setCategory_id(1L);
        products.setPrice((int) (Math.random() * 10000));
        ProductsMapper productsMapper = session.getMapper(ProductsMapper.class);
        productsMapper.insert(products);
        idNewProduct=products.getId();
        System.out.println("Смотрим новый продукт: " + idNewProduct +
                "\t" + products.getTitle() + "\t" + products.getPrice());
        session.commit();

        productsMapper.deleteByPrimaryKey(idNewProduct);
        session.commit();
            ProductsExample example = new ProductsExample();
            example.createCriteria().andIdEqualTo(idNewProduct);
            List<Products> list = productsMapper.selectByExample(example);
        System.out.println("Смотрим удалили ли продукт с " + idNewProduct+"? Да, если ответ пустой -"+list);
        assertThat(list.isEmpty(), is(true));
    }
    @Test
    void getProductById() {
        ProductsExample example = new ProductsExample();
        example.createCriteria().andIdEqualTo(1L);
        ProductsMapper productsMapper = session.getMapper(ProductsMapper.class);
        List<Products> list = productsMapper.selectByExample(example);
        Products products = list.get(0);

        assertThat(products.getTitle(),
                anyOf(containsString("Новой продукт"), containsString("Milk")));
        System.out.println("Было в getProductById-"+products.getId()+"\t"+products.getTitle()+
                "\t"+products.getPrice()+"\t"+products.getCategory_id());
    }
    @Test
    void modifyProduct() {
        ProductsExample example = new ProductsExample();
        example.createCriteria().andIdEqualTo(1L);
        ProductsMapper productsMapper = session.getMapper(ProductsMapper.class);
        List<Products> list = productsMapper.selectByExample(example);
        Products products = list.get(0);
        products.setTitle("Новой продукт");
        products.setPrice(123456);
        productsMapper.updateByPrimaryKey(products);
        session.commit();

        System.out.println("Стало после modifyProduct-"+products.getId()+"\t"+products.getTitle()+
                "\t"+products.getPrice()+"\t"+products.getCategory_id());
        assertThat(products.getTitle(), equalTo("Новой продукт"));
    }
    @AfterAll
    static void after() {
        {
            session.close();
        }
    }
}