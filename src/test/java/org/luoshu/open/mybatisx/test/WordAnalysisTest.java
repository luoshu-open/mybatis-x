package org.luoshu.open.mybatisx.test;

import org.junit.Assert;
import org.junit.Test;
import org.luoshu.open.mybatisx.MethodSqlParser;
import org.luoshu.open.mybatisx.WordAnalysis;
import org.luoshu.open.mybatisx.WordCondition;
import org.luoshu.open.mybatisx.exception.AnalysisException;

import java.util.List;

public class WordAnalysisTest {
    private String tableName = "t_user";



    @Test
    public void test1() throws AnalysisException {
        WordAnalysis wordAnalysis = new WordAnalysis("findByName");
        List<String> words = wordAnalysis.getWords();
        Assert.assertEquals("Name" , toString(words));
    }

    @Test
    public void test2() throws AnalysisException {
        WordAnalysis wordAnalysis = new WordAnalysis("findByNameAndAge");
        List<String> words = wordAnalysis.getWords();
        Assert.assertEquals("Name,And,Age" , toString(words));
    }
    @Test
    public void test3() throws AnalysisException {
        WordAnalysis wordAnalysis = new WordAnalysis("findByNameAndAgeAndTel");
        List<String> words = wordAnalysis.getWords();
        Assert.assertEquals("Name,And,Age,And,Tel" , toString(words));
    }
    @Test
    public void test4() throws AnalysisException {
        WordAnalysis wordAnalysis = new WordAnalysis("findByNameOrCode");
        List<String> words = wordAnalysis.getWords();
        Assert.assertEquals("Name,Or,Code" , toString(words));
    }
    @Test
    public void test5() throws AnalysisException {
        WordAnalysis wordAnalysis = new WordAnalysis("findByNameOrDriverId");
        List<String> words = wordAnalysis.getWords();
        Assert.assertEquals("Name,Or,DriverId" , toString(words));
    }

    @Test
    public void test6() throws AnalysisException {
        WordAnalysis wordAnalysis = new WordAnalysis("findByNameOrDriverIdOrOgCodeName");
        List<String> words = wordAnalysis.getWords();
        Assert.assertEquals("Name,Or,DriverId,Or,OgCodeName" , toString(words));
    }

    @Test
    public void test7() throws AnalysisException {
        WordAnalysis wordAnalysis = new WordAnalysis("findParentByNameOrDriverIdOrOgCodeName");
        List<String> words = wordAnalysis.getWords();
        Assert.assertEquals("Name,Or,DriverId,Or,OgCodeName" , toString(words));
    }

    @Test
    public void test8() throws AnalysisException {
        WordAnalysis wordAnalysis = new WordAnalysis("findParentByNameOrDriverIdOrOgCodeName");

        MethodSqlParser sqlParser = new MethodSqlParser(this.tableName , wordAnalysis);
        String sql = sqlParser.analysis();
        System.out.println(sql);
        Assert.assertEquals("SELECT parent FROM t_user WHERE name = #{name} OR driver_id = #{driverId} OR og_code_name = #{ogCodeName}" , sql);
    }

    @Test
    public void testc8() throws AnalysisException {
        WordAnalysis wordAnalysis = new WordAnalysis("findByNameOrDriverIdOrOgCodeName");
        List<WordCondition> conditions = wordAnalysis.getConditions();
        System.out.println(conditions);
    }






    public String toString(List<String> words){
        if(words == null || words.size() == 0){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (String word : words) {
            sb.append(word).append(",");
        }
        return sb.substring(0 , sb.length() - 1);
    }
}
