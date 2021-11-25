package com.example.demo.mapper;

import com.example.demo.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogMapper {

    Blog selectBlog(int id);

    List<Blog> selectAll();

    List<Blog> selectBlogOr(int id,int id2);

    Integer count();

    Integer countT(int id);
}
