package com.xtatone.JiraIssue.controllers;

import com.xtatone.JiraIssue.services.JiraCreatorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JiraController {

    private final JiraCreatorService jiraService;

    public JiraController(JiraCreatorService jiraService) {
        this.jiraService = jiraService;
    }

    @PostMapping("/issues")
    public String createIssues() {
        return jiraService.createIssues();
    }

}
