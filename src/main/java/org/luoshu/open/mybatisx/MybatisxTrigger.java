package org.luoshu.open.mybatisx;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * 触发 mybatis SQL 自动生成的，可以认为是入口
 * 时间： 2020/11/21 - 19:37
 * @author 朱小杰
 */
public class MybatisxTrigger {

    /**
     * 通过这个方法触发对已注册 mapper 进行方法识别。
     * 该方法可以重复调用，如果之前已经注册过，将不会再次注册
     * @param sqlSessionFactory sqlSessionFactory
     */
    public void trigger(SqlSessionFactory sqlSessionFactory){

    }
}
