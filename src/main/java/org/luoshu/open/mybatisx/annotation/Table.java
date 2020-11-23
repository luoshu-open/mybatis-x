package org.luoshu.open.mybatisx.annotation;

import java.lang.annotation.*;

/**
 *
 * 要在 mybatis 的接口上，标识这个注解，才会去扫描这个 mapper
 *
 * 时间： 2020/11/21 - 19:37
 * @author 朱小杰
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Table {

    /**
     * 表名
     * @return
     */
    String value();
}
