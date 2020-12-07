package org.luoshu.open.mybatisx.test;

import org.junit.Assert;
import org.junit.Test;
import org.luoshu.open.mybatisx.WordAnalysis;

public class WordTest {

    @Test
    public void test1(){
        String userName = WordAnalysis.toSqlField("userName");
        Assert.assertEquals("user_name" , userName);
    }

    @Test
    public void test2(){
        String userName = WordAnalysis.toSqlField("UserName");
        Assert.assertEquals("user_name" , userName);
    }

    @Test
    public void test3(){
        String userName = WordAnalysis.toSqlField("OgCodeName");
        Assert.assertEquals("og_code_name" , userName);
    }

    @Test
    public void test4(){
        String userName = WordAnalysis.toSqlField("ogCodeName");
        Assert.assertEquals("og_code_name" , userName);
    }
}
