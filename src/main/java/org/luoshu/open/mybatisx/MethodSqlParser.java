package org.luoshu.open.mybatisx;


import org.luoshu.open.mybatisx.exception.AnalysisException;

import java.util.List;

/**
 *
 * 把一个方法，解析成为一个SQL
 *
 * 时间： 2020/11/21 - 19:37
 * @author 朱小杰
 */
public class MethodSqlParser {

    private String tableName;

    private String queryField;

    private List<WordCondition> wordConditions;
    private SqlType sqlType;

    private String sql;

    public MethodSqlParser(String tableName , WordAnalysis wordAnalysis) throws AnalysisException {
        this(tableName , wordAnalysis.getSqlType() , wordAnalysis.getQueryField() , wordAnalysis.getConditions());
    }

    public MethodSqlParser(String tableName , SqlType sqlType, String queryField , List<WordCondition> wordConditions) {
        this.tableName = tableName;
        this.sqlType = sqlType;
        this.queryField = queryField;
        this.wordConditions = wordConditions;

        if(this.wordConditions == null || this.wordConditions.size() == 0){
            throw new NullPointerException("word condition is null");
        }
    }


    public String analysis(){
        if(this.sql != null){
            return this.sql;
        }
        StringBuffer sb = new StringBuffer();

        sb.append("SELECT ");
        if(SqlType.COUNT.equals(this.sqlType)){
            sb.append("count(1) ");
        }else if(SqlType.SELECT.equals(this.sqlType)){
            if(this.queryField != null){
                sb.append(this.queryField);
                sb.append(" ");
            }else{
                sb.append("* ");
            }
        }else{
            throw new RuntimeException("not support sql type : " + this.sqlType);
        }
        sb.append("FROM ");
        sb.append(this.tableName);
        sb.append(" ");
        sb.append("WHERE ");

        for (int i = 0; i < this.wordConditions.size(); i++) {
            WordCondition condition = this.wordConditions.get(i);
            if(i > 0){
                sb.append(condition.getConditionType().toString());
                sb.append(" ");
            }

            sb.append(WordAnalysis.toSqlField(condition.getWord()));
            sb.append(" = ");
            sb.append("#{");
            sb.append(condition.getWord());
            sb.append("}");
            if(i != this.wordConditions.size() -1){
                sb.append(" ");
            }

        }
        this.sql = sb.toString();

        return this.sql;
    }
}
