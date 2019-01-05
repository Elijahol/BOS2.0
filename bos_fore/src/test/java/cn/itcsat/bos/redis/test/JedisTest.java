package cn.itcsat.bos.redis.test;

import org.junit.Test;

import redis.clients.jedis.Jedis;

public class JedisTest {
	@Test
	public void testRedis(){
		//连接localhost默认端口6379
		Jedis jedis = new Jedis("localhost");
		jedis.setex("company",15, "黑马程序员");
		System.out.println(jedis.get("company"));
	}
}
