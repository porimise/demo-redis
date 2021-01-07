package com.zykj.demo.redis.hash;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kira
 */
public class BlogDemo {
    // ，更新blog，发表blog，修改blog，blog浏览次数，blog 点赞数

    private Jedis jedis=new Jedis("localhost",6379 );

    public long getBlogId(){
        return jedis.incr("blog_id_counter");
    }

    /**
     * 发布blog
     * @param id
     * @param blog
     * @return
     */
   public boolean publishBlog(long id, Map<String,String>blog){
    if (jedis.hexists("article::"+id, "tittle")){
        return false;

    }
     blog.put("contentLength", String.valueOf(blog.get("content").length()));
    jedis.hset("article::"+id, blog);
    return true;
   }

    /**
     *
     * @param id
     * @param updateBlog
     */
    public void updateBlog(long id,Map<String,String>updateBlog){
           String content=updateBlog.get("content");
        if (content!=null&&!" ".equals(content)){
            updateBlog.put("contentLength", String.valueOf(content.length()));
        }

     jedis.hset("article::"+id, updateBlog);
    }

    /**
     * blog点赞次数
     * @param id
     * @return
     */
   public Long incrementBlogLikeCount(long id){
       Long likeCount = jedis.hincrBy("article::"+id, "like_count", 1);
       return likeCount;
   }

    /**
     *  blog访问次数
     * @param id
     * @return
     */
   public Long blogViewCount(long id){
       return jedis.hincrBy("article::"+id, "view_count", 1);
   }

    public static void main(String[] args) {

        BlogDemo blogDemo=new BlogDemo();
        long id=blogDemo.getBlogId();
        Map<String,String>blog=new HashMap<>();
        blog.put("id",String.valueOf(id));
        blog.put("tittle", "学习redis");
        blog.put("content", "如何学习redis");
        blog.put("author", "kira");
        blog.put("gmt_created", "2021-01-05");
        blogDemo.publishBlog(id, blog);
        System.out.println("发布了一篇blog"+ blog);

        Map<String, String> updatedBlog = new HashMap<String, String>();
        updatedBlog.put("title", "我特别的喜欢学习Redis");
        updatedBlog.put("content", "我平时喜欢到官方网站上去学习Redis");
        blogDemo.updateBlog(id, updatedBlog);
        System.out.println("更新了一篇blog "+updatedBlog);

        Long blogViewCount = blogDemo.blogViewCount(id);
        System.out.println("id 为"+id+"的blog" +"被浏览了"+blogViewCount);
        Long likeCount = blogDemo.incrementBlogLikeCount(id);
        System.out.println("id 为"+id+"的blog" +"被点赞了"+likeCount);
    }
}
