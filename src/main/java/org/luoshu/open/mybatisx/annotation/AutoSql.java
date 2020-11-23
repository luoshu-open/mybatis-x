package org.luoshu.open.mybatisx.annotation;

import java.lang.annotation.*;

/**
 *
 * 在接口的方法上面加上这个SQL ， 才会去自动生成对应的SQL
 *
 * 时间： 2020/11/21 - 19:37
 * @author 朱小杰
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AutoSql {
}
