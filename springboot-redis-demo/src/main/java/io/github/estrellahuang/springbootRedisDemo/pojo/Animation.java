package io.github.estrellahuang.springbootRedisDemo.pojo;

import lombok.Data;

/**
 * 实体类：动漫
 * @author Huang Yuxin
 * @date 2019-06-18
 */
@Data
public class Animation {

    /**
     * 名称
     */
    private String name;

    /**
     * 动画角色
     */
    private String[] characters;

    /**
     * 时长
     */
    private String timeLength;

    /**
     * 动画类型
     */
    private String type;
}
