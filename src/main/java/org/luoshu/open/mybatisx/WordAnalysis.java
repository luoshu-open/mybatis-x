package org.luoshu.open.mybatisx;

import org.luoshu.open.mybatisx.exception.AnalysisException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个方法不是线程安全的，不适合多线程情况下运行
 * 必须是 findBy 开头，才会被识别 ，
 * 类似于 findByName , findById , findByTel
 * 也可以是 findByNameAndAge , findByNameOrId
 *
 *
 *
 * @author 朱小杰
 */
public class WordAnalysis {

    private static final String FIND_BY_PREFIX = "findBy";
    private static final String FIND_PREFIX = "find";
    private static final String COUNT_BY_PREFIX = "countBy";
    private static final String AND = "And";
    private static final String OR = "Or";


    private String word;

    public WordAnalysis(String word) {
        if(StringUtils.isEmpty(word)){
            throw new NullPointerException("word is null");
        }
        this.word = word;
    }

    /**
     * 查找该内容的前缀匹配，如果匹配不到，则会返回 -1
     *
     * 匹配内容为 selectBy , countBy , findBy
     * @return
     */
    private int findPrefixLength(){
        if(this.word.startsWith(FIND_PREFIX)){
            //find
            if(this.word.startsWith(FIND_BY_PREFIX)){
                //findBy
                return FIND_BY_PREFIX.length();
            }else{
                //findNameBy
                String tmp = this.word.substring(FIND_PREFIX.length());
                int byIndex = tmp.indexOf("By");
                if(byIndex == -1){
                    return -1;
                }else{
                    return FIND_PREFIX.length() + byIndex + 2;
                }
            }
        }else if(this.word.startsWith(COUNT_BY_PREFIX)){
            return COUNT_BY_PREFIX.length();
        }
        return  -1;

    }

    public String getQueryField(){
        if(this.word.startsWith(FIND_PREFIX)){
            if(this.word.startsWith(FIND_BY_PREFIX)){
                return null;
            }

            int byIndex = this.word.indexOf("By");
            if(byIndex == -1){
                return null;
            }else{
                String queryField = this.word.substring(FIND_PREFIX.length() , byIndex);
                return toSqlField(queryField);
            }


        }
        return null;
    }

    /**
     * 获取切割的单词内容
     * @return
     * @throws AnalysisException
     */
    public List<String> getWords() throws AnalysisException {
        int prefixLength = findPrefixLength();
        if(prefixLength == -1){
            throw new AnalysisException("mybatis method is invalid : " + this.word);
        }
        String text = word.substring(prefixLength);
        int index = -1;
        boolean findKeyWork = false;

        List<String> words = new ArrayList<>();

        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if(Character.isUpperCase(c)){
                if(index == -1){
                    index = i;
                }else{
                    String word2 = getIndexWord(text, i, 2);
                    String word3 = getIndexWord(text, i, 3);
                    if(AND.equals(word3) || OR.equals(word2) || findKeyWork){
                        // 开始截取单词
                        if(findKeyWork){
                            findKeyWork = false;
                        }else{
                            findKeyWork = true;
                        }
                        String word = text.substring(index, i);
                        index = i;
                        words.add(word);
                    }

                }
            }
        }
        String word = text.substring(index);
        words.add(word);

        return words;
    }

    /**
     * 获取当前 word 指定的 SQL 执行方式
     * @return
     */
    public SqlType getSqlType(){
        if(this.word.startsWith(COUNT_BY_PREFIX)){
            return SqlType.COUNT;
        }else if(this.word.startsWith(FIND_PREFIX)){
            return SqlType.SELECT;
        }else{
            throw new RuntimeException("not support prefix : " + this.word + " , just support count , find");
        }
    }

    /**
     * 获取一段内容
     * @param text 文本内容
     * @param index 从该位置开始截取
     * @param len   从该位置截取指定长度，如果该长度没有内容，则为空
     * @return
     */
    private String getIndexWord(String text , int index , int len){
        if(text.length() -1 <= index){
            return null;
        }

        if(text.length() -1 >= index + len){
            return text.substring(index , index + len);
        }else{
            return text.substring(index);
        }
    }

    public List<WordCondition> getConditions() throws AnalysisException {
        List<String> words = getWords();
        if(words == null || words.size() == 0){
            return null;
        }

        List<WordCondition> conditions = new ArrayList<>();
        String keyWord = null;

        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            if(i == 0){
                conditions.add(new WordCondition(ConditionType.AND, word));
            }else{
                if(keyWord == null){
                    keyWord = word;
                }else{
                    ConditionType conditionType = getConditionType(keyWord);
                    keyWord = null;
                    conditions.add(new WordCondition(conditionType, word));
                }
            }
        }


        return conditions;
    }

    private ConditionType getConditionType(String word){
        if(AND.equalsIgnoreCase(word)){
            return ConditionType.AND;
        }else if(OR.equalsIgnoreCase(word)){
            return ConditionType.OR;
        }

        throw new IllegalArgumentException("key word invalid , just support { And , Or }");
    }

    /**
     * 把单词的第一个字母改为小写，比如 Name -> name , UserName -> userName
     * @param word
     * @return
     */
    public static String toFirstLow(String word){
        String start = word.substring(0, 1);
        String end = word.substring(1);
        return start.toLowerCase() + end;
    }

    /**
     * 把一个单词转换为下划线的类型，如 User -> user , UserName -> user_name ,
     * @param word
     * @return
     */
    public static String toSqlField(String word) {
        char[] chars = word.toCharArray();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if(Character.isUpperCase(c)){
                if(i > 0){
                    sb.append("_");
                }
                sb.append(Character.toLowerCase(c));
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

