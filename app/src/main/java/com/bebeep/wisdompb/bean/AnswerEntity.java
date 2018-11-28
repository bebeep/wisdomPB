package com.bebeep.wisdompb.bean;

public class AnswerEntity {

    private boolean checked;
    private boolean right;


    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    public AnswerEntity(boolean right) {
        this.right = right;
    }


    public AnswerEntity() {
    }
}
