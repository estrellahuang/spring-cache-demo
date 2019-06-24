## 示例内容

Springboot整合Redis精简优雅版

## 主要文件

RedisConfiguration：Redis自定义的配置类

RedisUtil：Redis操作各类数据的工具类

关于自定义配置类，之前看过网上很多人的配置文件都不够精简，这边给出一个最精简的版本。

### 不需要keyGenerator

```
@Override
    public KeyGenerator keyGenerator() {
        // 匿名内部类
        return (o, method, objects) -> {
            //格式化缓存key字符串
            StringBuilder sb = new StringBuilder();
            //追加类名
            sb.append(o.getClass().getName());
            //追加方法名
            sb.append(method.getName());
            //遍历参数并且追加
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            log.debug("调用Redis缓存Key : " + sb.toString());
            return sb.toString();
        };
    }
```



springboot整合redis用于存取值有两种方式：

1. redisTemplate

2. 注解@Cacheable、@CachePut、 @CacheEvict

   

#### keyGenerator使用场景

使用注解方式操作redis数据：在需要缓存的方法上可以指定keyGenerator（键生成策略）。但是对于使用redisTemplate操作缓存数据，完成没有必要重写keyGenerator。自定义keyGenerator生效的前提是在注解中指定了keyGenerator，不指定的话，还是会用默认的。

### redisTemplate和注解使用选择

博主，这边使用redisTemplate，大部分使用场景并不是直接缓存从数据库中读取的数据，而是业务运行所生成的。需要时用redisUtils随时存取，无需依赖方法。

### 不需要指定StringRedisTemplate键值序列化方式

```
@Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        template.setConnectionFactory(redisConnectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson序列化方式
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson序列化方式
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        return template;
    }
```

有的人将上面这段代码加入到自己的配置文件中，然而测试证明，no need！

上面这段代码在指定StringRedisTemplate序列化键值的序列化类，但是我们不指定的时候默认使用的就是StringRedisSerializer.UTF_8来序列化，而不是RedisTemplate的默认序列化器JdkSerializationRedisSerializer，缓存数据后在redis客户端里面看到的仍然是肉眼能识别的数据。稍稍翻看下RedisTemlate和StringRedisTemplate的源码即可知。源码如下：

```
package org.springframework.data.redis.core;

import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;

public class StringRedisTemplate extends RedisTemplate<String, String> {
    public StringRedisTemplate() {
        this.setKeySerializer(RedisSerializer.string());
        this.setValueSerializer(RedisSerializer.string());
        this.setHashKeySerializer(RedisSerializer.string());
        this.setHashValueSerializer(RedisSerializer.string());
    }

    public StringRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

    protected RedisConnection preProcessConnection(RedisConnection connection, boolean existingConnection) {
        return new DefaultStringRedisConnection(connection);
    }
}

```

### RedisTemplate和StringRedisTemplate区别与连系

使用场景：

StringRedisTemplate：顾名思义，存取数据String类型较多时使用，键值都为String类型；

RedisTemplate：适合存取对象类型数据

默认序列化类：

StringRedisTemplate：StringRedisSerializer.UTF_8

RedisTemplate：JdkSerializationRedisSerializer

数据：

StringRedisTemplate和RedisTemplate管理的数据不共通，各自只能存取各自的。

联系：

StringRedisTemplate继承自RedisTemplate<K,V>

## 测试用例

测试用例见RedisDemoApplicationTests类