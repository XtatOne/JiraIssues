package com.xtatone.JiraIssue.services;

import com.xtatone.JiraIssue.baeldung.JiraClient;
import com.xtatone.JiraIssue.models.Issue;

import java.util.List;

public interface JiraIntegratorService {

    String integrateIssueToJira(JiraClient jiraClient, List<Issue> issue);

}
