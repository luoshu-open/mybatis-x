package org.luoshu.open.mybatisx;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;

/**
 * 触发 mybatis SQL 自动生成的，可以认为是入口
 * 时间： 2020/11/21 - 19:37
 * @author 朱小杰
 */
public class MybatisxTrigger implements BeanPostProcessor , ApplicationContextAware {
    private final MybatisStatementCache statementCache = MybatisStatementCache.getInstance();

    /**
     * 通过这个方法触发对已注册 mapper 进行方法识别。
     * 该方法可以重复调用，如果之前已经注册过，将不会再次注册
     * @param sqlSessionFactory sqlSessionFactory
     */
    public void trigger(SqlSessionFactory sqlSessionFactory){
        Configuration configuration = sqlSessionFactory.getConfiguration();
        Collection<Class<?>> mappers = configuration.getMapperRegistry().getMappers();
        if(mappers != null && mappers.size() > 0){
            for (Class<?> mapper : mappers) {
                if(! statementCache.hasMapper(mapper)){
                    statementCache.register(mapper);
                    MapperView mapperView = new MapperView(configuration , mapper);
                    mapperView.resolve();
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
        this.trigger(sqlSessionFactory);
    }



    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof SqlSessionFactory){
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) bean;

            this.trigger(sqlSessionFactory);
        }
        return bean;
    }
}
