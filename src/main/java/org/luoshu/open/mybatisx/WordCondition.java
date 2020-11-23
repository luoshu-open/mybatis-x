package org.luoshu.open.mybatisx;

/**
 * 切割出来每一小块的内容
 * 时间： 2020/11/21 - 19:37
 * @author 朱小杰
 */
public class WordCondition {

    ConditionType conditionType;

    String word;

    public WordCondition(ConditionType conditionType, String word) {
        this.setConditionType(conditionType);
        this.setWord(word);
    }

    public WordCondition() {
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = WordAnalysis.toFirstLow(word);
    }
}
