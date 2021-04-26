package com.example.yourdaymobile.ui.data;

public class Todo {
    private String text;
    private Boolean isDone;

    public Todo(String text, Boolean isDone) {
        this.text = text;
        this.isDone = isDone;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }
}
