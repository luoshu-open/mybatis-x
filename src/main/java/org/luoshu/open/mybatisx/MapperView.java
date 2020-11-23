package org.luoshu.open.mybatisx;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.luoshu.open.mybatisx.annotation.AutoSql;
import org.luoshu.open.mybatisx.annotation.Table;
import org.luoshu.open.mybatisx.exception.AnalysisException;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 识别每一个 mapper
 * 时间： 2020/11/21 - 19:37
 * @author 朱小杰
 */
public class MapperView {

    private Configuration configuration;
    private Class<?> mapperClass;
    private MybatisStatementCache statementCache = MybatisStatementCache.getInstance();

    public MapperView(Configuration configuration, Class<?> mapperClass) {
        this.configuration = configuration;
        this.mapperClass = mapperClass;
    }

    public void resolve(){
        Table table = AnnotationUtils.findAnnotation(this.mapperClass, Table.class);
        if(table == null){
            return;
        }
        String tableName = table.value();

        List<Method> methods = findAutoMethod(this.mapperClass);
        if(methods == null || methods.size() == 0){
            return;
        }

        for (Method method : methods) {
            if(this.statementCache.getMappedStatement(method) == null){

//                resolveMethodSql(method);
                MappedStatement statement = createMappedStatement(tableName , method);
                this.statementCache.register(method , statement);

                this.configuration.addMappedStatement(statement);
            }
        }

    }


    private MappedStatement createMappedStatement(String tableName , Method method){
        NoKeyGenerator keyGenerator = new NoKeyGenerator();
        LanguageDriver languageDriver = new XMLLanguageDriver();
        String id = getMybatisId(method);
        WordAnalysis wordAnalysis = new WordAnalysis(method.getName());
        List<WordCondition> conditions = null;
        try {
            conditions = wordAnalysis.getConditions();
        } catch (AnalysisException e) {
            throw new RuntimeException(id + " reslove method name error , \n" + e.getMessage() , e);
        }

        SqlType sqlType = wordAnalysis.getSqlType();
        MethodSqlParser sqlParser = new MethodSqlParser(tableName, sqlType , wordAnalysis.getQueryField(), conditions);
        String sql = sqlParser.analysis();

        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        SqlSource sqlSource = sqlSourceParser.parse(sql, Object.class, new HashMap<>());

        MappedStatement.Builder builder = new MappedStatement.Builder(configuration , id,  sqlSource, SqlCommandType.SELECT)
                .resource(null)
                .fetchSize(null)
                .timeout(null)
                .statementType(StatementType.PREPARED)
                .keyGenerator(keyGenerator)
                .keyProperty(null)
                .keyColumn(null)
                .databaseId(null)
                .lang(languageDriver)
                .resultOrdered(false)
                .resultSets(null)
                .resultMaps(getStatementResultMaps(configuration,null, getReturnClass(method), id))
                .resultSetType(null)
                .flushCacheRequired(valueOrDefault(true, true))
                .useCache(valueOrDefault(false, false))
                .cache(null);

        MappedStatement statement = builder.build();

        return statement;
    }

    private Class<?> getReturnClass(Method method){
        Class<?> returnType = method.getReturnType();
        if(returnType.isAssignableFrom(List.class)){
            Type type = method.getGenericReturnType();
            if(type instanceof ParameterizedType){
                Type[] arguments = ((ParameterizedType) type).getActualTypeArguments();
                if(arguments != null && arguments.length > 0){
                    return (Class<?>) arguments[0];
                }
            }
            return Map.class;

        }else{
            return returnType;
        }
    }

    private  List<ResultMap> getStatementResultMaps(
            Configuration configuration,
            String resultMap,
            Class<?> resultType,
            String statementId) {
        resultMap = applyCurrentNamespace(resultMap, true);

        List<ResultMap> resultMaps = new ArrayList<>();
        if (resultMap != null) {
            String[] resultMapNames = resultMap.split(",");
            for (String resultMapName : resultMapNames) {
                try {
                    resultMaps.add(configuration.getResultMap(resultMapName.trim()));
                } catch (IllegalArgumentException e) {
                    throw new IncompleteElementException("Could not find result map '" + resultMapName + "' referenced from '" + statementId + "'", e);
                }
            }
        } else if (resultType != null) {
            ResultMap inlineResultMap = new ResultMap.Builder(
                    configuration,
                    statementId + "-Inline",
                    resultType,
                    new ArrayList<>(),
                    null).build();
            resultMaps.add(inlineResultMap);
        }
        return resultMaps;
    }


    private List<Method> findAutoMethod(Class<?> mapper){
        Method[] methods = mapper.getDeclaredMethods();
        List<Method> autoMethod = new ArrayList<>();

        for (Method method : methods) {
            AutoSql autoSql = AnnotationUtils.findAnnotation(method, AutoSql.class);
            if(autoSql != null){
                autoMethod.add(method);
            }
        }
        return autoMethod;
    }

    private String applyCurrentNamespace(String base, boolean isReference) {
//        String currentNamespace = "org.luoshu.admin.mapper.UserMapper";
        String currentNamespace = this.mapperClass.getName();
        if (base == null) {
            return null;
        }
        if (isReference) {
            // is it qualified with any namespace yet?
            if (base.contains(".")) {
                return base;
            }
        } else {
            // is it qualified with this namespace yet?
            if (base.startsWith(currentNamespace + ".")) {
                return base;
            }
            if (base.contains(".")) {
                throw new BuilderException("Dots are not allowed in element names, please remove it from " + base);
            }
        }
        return currentNamespace + "." + base;
    }

    private <T> T valueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static String getMybatisId(Method method){
        Class<?> declaringClass = method.getDeclaringClass();
        return declaringClass.getName() + "." + method.getName();
    }

}
