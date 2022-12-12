package HW6;

import HW6.db.dao.CategoriesMapper;
import HW6.db.model.Categories;
import HW6.db.model.CategoriesExample;
import lombok.SneakyThrows;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class TestsCategories {
    static SqlSession session = null;

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
    void getCategoryByIdPositiveTest() {
        Integer iD = 1;
        CategoriesMapper categoriesMapper = session.getMapper(CategoriesMapper.class);
        CategoriesExample example = new CategoriesExample();
        List<Categories> list = categoriesMapper.selectByExample(example);

        Categories categories = list.get(iD);
        System.out.println("Тест1: Категория с id=" + iD + " - " + categories.getTitle());
        assertThat(categories.getTitle(), equalTo("Electronic"));
    }
    @SneakyThrows
    @Test
    void getCategoryByIdNegativeTest() {
        int iD = 2;
        CategoriesMapper categoriesMapper = session.getMapper(CategoriesMapper.class);
        CategoriesExample example = new CategoriesExample();
        List<Categories> list = categoriesMapper.selectByExample(example);

        Categories categories = list.get(iD);
        System.out.println("Тест2: Категория с id=" + iD + " - " + categories.getTitle() + ", а не Electronic.");
        assertThat(categories.getTitle(), not("Electronic"));
    }
    @AfterAll
    static void after() {
        {
            session.close();
        }
    }
}