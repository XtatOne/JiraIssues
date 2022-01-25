package com.xtatone.JiraIssue.models;

public class Issue {

    private int id;
    private String type;
    private String priority;
    private String summary;
    private String projectName;

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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", priority='" + priority + '\'' +
                ", summary='" + summary + '\'' +
                ", projectName='" + projectName + '\'' +
                '}';
    }
}
