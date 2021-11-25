package com.tiny.mybatis;

import com.example.demo.entity.Blog;
import com.example.demo.mapper.BlogMapper;
import com.tiny.mybatis.builder.xml.XMLConfigBuilder;
import com.tiny.mybatis.io.Resources;
import com.tiny.mybatis.session.Configuration;
import com.tiny.mybatis.session.SqlSession;
import com.tiny.mybatis.session.SqlSessionFactory;
import com.tiny.mybatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//@SpringBootTest
//@MapperScan("com.*")
public class TinyMybatisApplicationTests {

    @Test
    void testResources() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");

        Assertions.assertNotNull(inputStream);

    }


    @Test
    void testXMlConfigBuilder() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");

        XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, null, null);

        Configuration configuration = parser.parse();

        System.out.println(configuration);
    }

    @Test
    void contextLoads1() throws IOException {

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        try (SqlSession session = sqlSessionFactory.openSession()) {
            //Blog blog = (Blog) session.selectOne("com.example.demo.mapper.BlogMapper.selectBlog", 101);
            //List<Blog> blog =  session.selectList("com.example.demo.mapper.BlogMapper.selectAll");

            BlogMapper mapper = session.getMapper(BlogMapper.class);

            Blog blog = mapper.selectBlog(102);


            System.out.println(blog);
        }


    }

    @Test
    void contextLoads2() throws IOException {

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        try (SqlSession session = sqlSessionFactory.openSession()) {
            //Blog blog = (Blog) session.selectOne("com.example.demo.mapper.BlogMapper.selectBlog", 101);
            //List<Blog> blog =  session.selectList("com.example.demo.mapper.BlogMapper.selectAll");

            BlogMapper mapper = session.getMapper(BlogMapper.class);

            List<Blog> blogs = mapper.selectAll();


            System.out.println(blogs);
        }


    }


    @Test
    void contextLoads3() throws IOException {

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        try (SqlSession session = sqlSessionFactory.openSession()) {
            //Blog blog = (Blog) session.selectOne("com.example.demo.mapper.BlogMapper.selectBlog", 101);
            //List<Blog> blog =  session.selectList("com.example.demo.mapper.BlogMapper.selectAll");

            BlogMapper mapper = session.getMapper(BlogMapper.class);

            List<Blog> blogs = mapper.selectBlogOr(101,103);


            System.out.println(blogs);
        }


    }

    @Test
    void contextLoads4() throws IOException {

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        try (SqlSession session = sqlSessionFactory.openSession()) {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            Integer count = mapper.count();
            System.out.println(count);
        }
    }


    @Test
    void contextLoads5() throws IOException {

        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        try (SqlSession session = sqlSessionFactory.openSession()) {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            Integer count = mapper.countT(101);
            System.out.println(count);
        }
    }

}
