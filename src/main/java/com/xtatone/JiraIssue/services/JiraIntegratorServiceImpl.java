package com.xtatone.JiraIssue.services;
import com.xtatone.JiraIssue.baeldung.JiraClient;
import com.xtatone.JiraIssue.models.Issue;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JiraIntegratorServiceImpl implements JiraIntegratorService{

    @Override
    public String integrateIssueToJira(JiraClient jiraClient, List<Issue> issues) {

        return issues.stream().map(issue -> {

            try {
                return createIssue(jiraClient, issue);
            } catch (Exception exception) {
                return exception.getMessage();
            }

        }).collect(Collectors.joining("; ", "[", "]"));

    }

    /**
     * Создаем задачу в Jira на основании переданного issue
     * @param jiraClient - клиент Jira для работы с сервисом
     * @param issue - задача, которую пытаемся создать в Jira с указанным приоритетом
     * @return - ключ задачи на стороне Jira
     * @throws RuntimeException
     */
    public String createIssue(JiraClient jiraClient, Issue issue) {

        if (issue.getProjectName().isEmpty()) {
            throw  new RuntimeException("Имя проекта не может быть пустым!");
        }

        try {

            String issueKey = createIssuesInJira(jiraClient, issue);
            jiraClient.setIssuePriority(issueKey, getPriorityId(issue.getPriority(), issue.getId()));
            return issueKey;

        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }


    }

    /**
     * Создает задачи в Jira на основании подготовленного List Issue.
     * Обрабатывает ситуации, когда задачи не могут быть созданы.
     * @param issue - List Issue c подготовленными данными для создания задач в Jira.
     */
    private String createIssuesInJira(JiraClient jiraClient, Issue issue) {

        try {

            return jiraClient.createIssue(
                                issue.getProjectName(),
                                getIssueTypeId(issue.getType(), issue.getId()),
                                issue.getSummary());

        } catch (Exception exception) {
            throw  new RuntimeException("Не удалось создать задачу в Jira c id " + issue.getId()
                            +  " Ошибка - " + exception.toString());
        }

    }

    /**
     * Получает typeId из срокового представления type из XML
     * @param type - строковое представление типа задачи в Jira.
     * @return id для типа задач в Jira
     */
    private Long getIssueTypeId(String type, int issueId) {

        switch (type.toLowerCase()) {

            case "epic": {
                return 1000L;
            }

            case "task": {
                return 10001L;
            }

            default: {
                throw new RuntimeException("Не удалось найти type для issue с id " + issueId);
            }
        }

    }

    /**
     *  Получает priorityId из срокового представления priority из XML
     * @param priority - строковое представление приоритета задачи в Jira.
     * @return id для приоритета задач в Jira
     */
    private Long getPriorityId(String priority, int issueId) {

        switch (priority.toLowerCase()) {

            case "highest": {
                return 1L;
            }

            case "high": {
                return 2L;
            }

            case "medium": {
                return 3L;
            }

            case "low": {
                return 4L;
            }

            case "lowest": {
                return 5L;
            }

            default: {
                throw new RuntimeException("Не удалось найти priority для issue с id " + issueId);
            }

        }

    }

}
