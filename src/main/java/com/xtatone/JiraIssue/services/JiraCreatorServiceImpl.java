package com.xtatone.JiraIssue.services;

import com.xtatone.JiraIssue.baeldung.JiraClient;
import com.xtatone.JiraIssue.models.Issue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JiraCreatorServiceImpl implements JiraCreatorService {

    private final JiraClient jiraClient;
    private final XmlParsingService xmlParsingService;
    private final JiraIntegratorService jiraIntegratorService;

    public JiraCreatorServiceImpl(
            @Value("${jiraAccount}") String jiraAccount,
            @Value("${token}") String token,
            @Value("${jiraUrl}") String jiraUrl,
            XmlParsingService xmlParsingService,
            JiraIntegratorService jiraIntegratorService
    ) {
        this.jiraClient = new JiraClient(jiraAccount, token, jiraUrl);
        this.xmlParsingService = xmlParsingService;
        this.jiraIntegratorService = jiraIntegratorService;
    }

    /**
     * Создаем задачи в Jira
     */
    @Override
    public String createIssues() {

        List<Issue> issues = new ArrayList<>();

        try {
            issues = xmlParsingService.parsingXml();
        } catch (Exception exception) {
            return "Ошибка при парсинге XML.";
        }

        return jiraIntegratorService.integrateIssueToJira(jiraClient, issues);

    }

}
