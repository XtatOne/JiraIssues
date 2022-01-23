package com.xtatone.JiraIssue.models;

public class Issue {

    private int id;
    private String type;
    private String priority;
    private Description description;

    public Issue() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", priority='" + priority + '\'' +
                ", description=" + description +
                '}';
    }
}
