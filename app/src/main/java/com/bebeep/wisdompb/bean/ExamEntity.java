package com.bebeep.wisdompb.bean;

/**
 * 考试
 */
public class ExamEntity {

    private String title;// 待考标题,
    private String id;// 1,
    private String startTime;//
    private String endTime;//
    private int examinationTime;// 考试时间  分钟
    private String state;// 状态 0 未开始 1 进行中 2已过期  3正在考试
    private String totalScore;// 总分
    private String passingGrade;// 及格分
    private String totalNumberQuestions;// 总题数
    private String examinationNotes;// 考试说明
    private String templateId;//模板id


    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public String getPassingGrade() {
        return passingGrade;
    }

    public void setPassingGrade(String passingGrade) {
        this.passingGrade = passingGrade;
    }

    public String getTotalNumberQuestions() {
        return totalNumberQuestions;
    }

    public void setTotalNumberQuestions(String totalNumberQuestions) {
        this.totalNumberQuestions = totalNumberQuestions;
    }

    public String getExaminationNotes() {
        return examinationNotes;
    }

    public void setExaminationNotes(String examinationNotes) {
        this.examinationNotes = examinationNotes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getExaminationTime() {
        return examinationTime;
    }

    public void setExaminationTime(int examinationTime) {
        this.examinationTime = examinationTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
