package com.xtatone.JiraIssue.services;

import com.xtatone.JiraIssue.models.Description;
import com.xtatone.JiraIssue.models.Issue;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class XmlParsingServiceImpl implements XmlParsingService{

    private String projectName = "";

    /**
     * Парсим XML для полечения списка issues
     * @return List issues для отправки в Jira
     * @throws Exception
     */
    @Override
    public List<Issue> parsingXml() throws Exception {

        Document document = buildDocument();

        Map<String, NodeList> secondNodesMap = getSecondLvlNods(document);

        List<NodeList> issuesNodes = getThirdLvlNodes(secondNodesMap.get("issues"));
        List<Issue> issues = getIssuesFromXML(issuesNodes);

        List<NodeList> descriptionsNodes = getThirdLvlNodes(secondNodesMap.get("descriptions"));
        List<Description> descriptions = getDescriptionsFromXML(descriptionsNodes);

        issues.forEach(issue -> {
            setSummaryInIssue(issue, descriptions);
            issue.setProjectName(projectName);
        });

        return issues;

    }

    /**
     * Создает Document на основании InputFile.xml.
     * @return Document для дальнейшего его обхода программой.
     * @throws Exception
     */
    private Document buildDocument() throws Exception {

        File file = new File("InputFile.xml");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        return dbf.newDocumentBuilder().parse(file);

    }

    /**
     * Получает ключ проекта в Jira и Map с нодами 2 уровня XML.
     * @param document - Document для обхода.
     * @return Возвращает Map с нодами 2 уровня XML.
     */
    private Map<String, NodeList> getSecondLvlNods(Document document) {

        Node firstLvlNode = document.getFirstChild();
        NodeList secondLvlNodes = firstLvlNode.getChildNodes();

        Map<String, NodeList> secondNodesMap = new HashMap<>();

        for (int i = 0; i < secondLvlNodes.getLength(); i++) {

            Node currentNode = secondLvlNodes.item(i);

            if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            switch (currentNode.getNodeName()) {

                case "projectKey": {
                    projectName = currentNode.getTextContent();
                    break;
                }

                case "issues":

                case "descriptions": {
                    secondNodesMap.put(currentNode.getNodeName(), currentNode.getChildNodes());
                    break;
                }

                default: {
                    throw  new RuntimeException("Ошибка при обходе 2 уровня xml.");
                }
            }
        }

        return secondNodesMap;

    }

    /**
     * Получает итоговые ноды XML.
     * @param issuesNode - List нод 3 уровня XML.
     * @return Возвращает List с нодами Issue или Description последнего уровня XML.
     */
    private List<NodeList> getThirdLvlNodes(NodeList issuesNode) {

        List<NodeList> thirdLvlNodes = new ArrayList<>();

        for (int i = 0; i < issuesNode.getLength(); i++) {

            Node currentNode = issuesNode.item(i);

            if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            thirdLvlNodes.add(currentNode.getChildNodes());

        }

        return thirdLvlNodes;
    }

    /**
     * Возвращает List Issues из XML.
     * @param issuesNodes - List с нодами Issue.
     * @return List Issue для дальнейшего создания задач в Jira.
     */
    private  List<Issue> getIssuesFromXML(List<NodeList> issuesNodes) {

        List<Issue> issuesList = new ArrayList<>();

        for (NodeList issueNode : issuesNodes) {

            Issue issue = new Issue();

            for (int j = 0; j < issueNode.getLength(); j++) {

                Node currentNode = issueNode.item(j);

                if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                switch (currentNode.getNodeName()) {

                    case "id": {
                        issue.setId(Integer.parseInt(currentNode.getTextContent()));
                        break;
                    }

                    case "type": {
                        issue.setType(currentNode.getTextContent());
                        break;
                    }

                    case "priority": {
                        issue.setPriority(currentNode.getTextContent());
                        break;
                    }

                    default: {
                        System.out.println("Незапланированный тег в XML");
                        break;
                    }
                }
            }

            issuesList.add(issue);

        }

        return issuesList;

    }

    /**
     * Возвращает List Description из XML.
     * @param descriptionsNodes - List с нодами Description.
     * @return List Description для получения summary для Issue.
     */
    private  List<Description> getDescriptionsFromXML(List<NodeList> descriptionsNodes) {

        List<Description> descriptionsList = new ArrayList<>();

        for (NodeList descriptionNode : descriptionsNodes) {

            Description description = new Description();

            for (int j = 0; j < descriptionNode.getLength(); j++) {

                Node currentNode = descriptionNode.item(j);

                if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                switch (currentNode.getNodeName()) {

                    case "id": {
                        description.setId(Integer.parseInt(currentNode.getTextContent()));
                        break;
                    }

                    case "summary": {
                        description.setSummary(currentNode.getTextContent());
                        break;
                    }

                    default: {
                        System.out.println("Незапланированный тег в XML");
                        break;
                    }
                }
            }

            descriptionsList.add(description);

        }

        return descriptionsList;

    }

    /**
     * Для каждого Issue находит соответствующий Description по связке через id и заполняет
     * summary в Issue.
     * @param issue - issue для которого ищем description
     * @param descriptions - List Description.
     */
    private void setSummaryInIssue(Issue issue, List<Description> descriptions) {

        descriptions.forEach(description -> {
            if (issue.getId() == description.getId()) {
                issue.setSummary(description.getSummary());
            }
        });

    }

}
