package com.xtatone.JiraIssue.services;

import com.xtatone.JiraIssue.models.Issue;

import java.util.List;

public interface XmlParsingService {

    List<Issue> parsingXml() throws Exception;
}
