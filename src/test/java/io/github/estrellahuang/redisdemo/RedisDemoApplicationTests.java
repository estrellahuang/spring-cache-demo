package io.github.estrellahuang.redisdemo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.estrellahuang.redisdemo.pojo.Animation;
import io.github.estrellahuang.redisdemo.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisDemoApplicationTests {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void contextLoads() {
    }

    /**
     * 测试普通对象缓存
     */
    @Test
    public void normalObject() throws JsonProcessingException {
        Animation animation = new Animation();
        animation.setName("千与千寻");
        String[] characters = new String[]{"千寻","琥珀川","小玲","锅炉爷爷","钱婆婆","汤婆婆"};
        animation.setCharacters(characters);
        animation.setTimeLength("2.5h");
        animation.setType("宫崎骏系动漫");
        redisUtil.set("2",animation);
        Object o = redisUtil.get("2");
        System.out.println(objectMapper.writeValueAsString(o));
    }


    /**
     * 测试hash缓存
     */
    @Test
    public void hashObject() throws JsonProcessingException {
        // 存值
        Animation animation = new Animation();
        animation.setName("千与千寻");
        String[] characters = new String[]{"千寻","琥珀川","小玲","锅炉爷爷","钱婆婆","汤婆婆"};
        animation.setCharacters(characters);
        animation.setTimeLength("2.5h");
        animation.setType("宫崎骏系动漫");
        Animation animation2 = new Animation();
        animation2.setName("魔女宅急便");
        String[] characters2= new String[]{"琪琪","kiki","蜻蜓"};
        animation2.setCharacters(characters2);
        animation2.setTimeLength("2.5h");
        animation2.setType("宫崎骏系动漫");
        redisUtil.hset("animations","1",animation);
        redisUtil.hset("animations","2",animation2);
        // 取值
        Object animationc1 = redisUtil.hget("animations", "1");
        System.out.println(objectMapper.writeValueAsString(animationc1));
        Object animationc2 = redisUtil.hget("animations", "2");
        System.out.println(objectMapper.writeValueAsString(animationc2));
    }

    /**
     * StringRedisTemplate普通存取
     * @throws JsonProcessingException
     */
    @Test
    public void testStringRedisTemplate() throws JsonProcessingException {
        Animation animation = new Animation();
        animation.setName("千与千寻");
        String[] characters = new String[]{"千寻","琥珀川","小玲","锅炉爷爷","钱婆婆","汤婆婆"};
        animation.setCharacters(characters);
        animation.setTimeLength("2.5h");
        animation.setType("宫崎骏系动漫");
        stringRedisTemplate.opsForValue().set("stringTestWithoutCustomeBean",objectMapper.writeValueAsString(animation));
        String a = stringRedisTemplate.opsForValue().get("stringTestWithoutCustomeBean");
        System.out.println(a);
    }

    /**
     * StringRedisTemplate的Hash存取
     * @throws JsonProcessingException
     */
    @Test
    public void testHashStringRedisTemplate() throws JsonProcessingException {
        Animation animation = new Animation();
        animation.setName("千与千寻");
        String[] characters = new String[]{"千寻","琥珀川","小玲","锅炉爷爷","无脸男","钱婆婆","汤婆婆"};
        animation.setCharacters(characters);
        animation.setTimeLength("2.5h");
        animation.setType("宫崎骏系动漫");
        stringRedisTemplate.opsForHash().put("stringRedisTemplate","stringTestWithoutCustomeBean",objectMapper.writeValueAsString(animation));
        Object s = stringRedisTemplate.opsForHash().get("stringRedisTemplate", "stringTestWithoutCustomeBean");
        System.out.println(s);
    }

}
