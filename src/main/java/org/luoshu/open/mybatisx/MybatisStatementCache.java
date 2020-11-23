package org.luoshu.open.mybatisx;

import org.apache.ibatis.mapping.MappedStatement;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存数据
 * 时间： 2020/11/21 - 19:37
 * @author 朱小杰
 */
public class MybatisStatementCache {
    private MybatisStatementCache(){}

    private final ConcurrentHashMap<Method , MappedStatement> data = new ConcurrentHashMap<>();
    private final List<Class<?>> classes = new LinkedList<>();




    public MappedStatement getMappedStatement(Method method){
        MappedStatement mappedStatement = data.get(method);
        return mappedStatement;
    }


    public void register(Method method , MappedStatement statement){
        this.data.put(method , statement);
    }
    public void register(Class<?> clazz){
        this.classes.add(clazz);
    }

    public boolean hasMapper(Class<?> clazz){
        return this.classes.contains(clazz);
    }












    private static final MybatisStatementCache INSTANCE = new MybatisStatementCache();

    public static MybatisStatementCache getInstance() {
        return INSTANCE;
    }
}
