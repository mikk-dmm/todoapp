package com.example.todoapp.entity;

public enum Status {
    TODO("未着手"),
    IN_PROGRESS("進行中"),
    DONE("完了");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
