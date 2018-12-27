package com.bebeep.wisdompb.bean;

import java.util.List;

public class ExamResultEntity {
    private int totalScore; //总得分
    private int examTime; //考试时间
    private int passingGrade; //及格分数

    private List<ExamResultItemEntity> itemBackList;

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getExamTime() {
        return examTime;
    }

    public void setExamTime(int examTime) {
        this.examTime = examTime;
    }

    public int getPassingGrade() {
        return passingGrade;
    }

    public void setPassingGrade(int passingGrade) {
        this.passingGrade = passingGrade;
    }

    public List<ExamResultItemEntity> getItemBackList() {
        return itemBackList;
    }

    public void setItemBackList(List<ExamResultItemEntity> itemBackList) {
        this.itemBackList = itemBackList;
    }
}
