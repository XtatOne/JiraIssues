package com.xtatone.JiraIssue.models;

public class Description {

    private int id;
    private String summary;

    public Description() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "Description{" +
                "id=" + id +
                ", summary='" + summary + '\'' +
                '}';
    }
}
