package com.zykj.demo.redis.string;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @author kira
 */
public class RedisDemo {
    public static void main(String[] args) {
        //实现简单的缓存机制
        Jedis jedis=new Jedis("localhost");
        jedis.set("k1", "v1");
        System.out.println(jedis.get("k1"));
        jedis.del("k1");
        // 实现最简单的分布式锁
       String setResult =jedis.set("lock_key", "lock_value");
        System.out.println(setResult);
       Long setNxResult =jedis.setnx("lock_key", "lock_value");
        System.out.println(setNxResult);
        jedis.del("lock_key");
      Long setNxResult2=  jedis.setnx("lock_key2", "lock_value2");

        System.out.println(setNxResult2);
        jedis.del("lock_key2");

        //博客的查看和修改,比如博客有内容，作者，标题，时间这几个属性，博客的字数统计与预览
       String  publishBlogResult=jedis.mset("article:1:tittle","学习redis",
                "article:1:content","如何学习redis",
                "article:1:author","kira",
                 "article:1:gmt_created","2021-01-01 20:48:57");
        System.out.println("发布博客的结果 "+publishBlogResult);

        List<String> blog = jedis.mget("article:1:author","article:1:tittle","article:1:content","article:1:gmt_created");

        System.out.println("查看博客 "+blog);

     String  updateBlogResult= jedis.mset("article:1:tittle","修改后的学习redis","article:1:content","修改后的如何学习redis");
        System.out.println("更新blog的结果" +updateBlogResult);

   List<String>blogAfterUpdate     =jedis.mget("article:1:author","article:1:tittle","article:1:content","article:1:gmt_created");
        System.out.println("修改后的blog "+blogAfterUpdate);
        //博客字数统计
      Long contentLength  =jedis.strlen("article:1:content");
        System.out.println("博客字数为 "+contentLength);
        //博客内容预览
        String contentStr=jedis.getrange("article:1:content", 0, 14);
        System.out.println("博客预览内容为 "+contentStr);
        //实现用户操作日志审计功能
        jedis.del("operation_log_2021-01-04");
       jedis.setnx("operation_log_2021-01-04", "null");
       for (int i=0;i<10;i++){
           jedis.append("operation_2021-01-04", "今天第"+i+"次操作");
       }
       // 实现一个id生成器
        jedis.del("id::counter");
        Long idCounter = jedis.incr("id::counter");
        System.out.println("id 生成器 "+idCounter);

        // 博客点赞数和踩数,
        jedis.del("article:1:dianzan");
        jedis.del("article:1:cai");
        for (int j=0;j<10;j++){
            jedis.incr("article:1:dianzan");
        }
        System.out.println("博客被点赞的次数" +jedis.get("article:1:dianzan"));
        jedis.decr("article:1:dianzan");
        System.out.println("再次查看博客被点赞的次数" +Long.valueOf(jedis.get("article:1:dianzan")));

        for (int k=0;k<19;k++){
            jedis.incr("article:1:cai");

        }
        System.out.println("博客被踩的次数"+Long.valueOf(jedis.get("article:1:cai")));
        jedis.decr("article:1:cai");
        System.out.println("再次查看博客被踩的次数"+Long.valueOf(jedis.get("article:1:cai")));




    }
}
