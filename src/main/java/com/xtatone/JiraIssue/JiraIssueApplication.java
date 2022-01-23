package com.xtatone.JiraIssue;

import com.xtatone.JiraIssue.baeldung.JiraClient;
import com.xtatone.JiraIssue.models.Description;
import com.xtatone.JiraIssue.models.Issue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class JiraIssueApplication {

	public static void main(String[] args) {
		SpringApplication.run(JiraIssueApplication.class, args);
		createIssues();
	}

	public static void createIssues() {

		Document 	document 		= null;
		String 		projectName 	= "";

		try {
			document = buildDocumet();
		} catch (Exception e) {
			System.out.println("Ошибка при парсинге - " + e.toString());
			return;
		}

		Map<String ,NodeList> secondNodesMap = getSecondLvlNods(document, projectName);

		List<NodeList> 	issuesNodes 	= getThirdLvlNodes(secondNodesMap.get("issues"));
		List<Issue> 	issues 			= getIssuesFromXML(issuesNodes);

		List<NodeList> 		descriptionsNodes 	= getThirdLvlNodes(secondNodesMap.get("descriptions"));
		List<Description> 	descriptions 		= getDescriptionsFromXML(descriptionsNodes);

		setDescriptionInIssue(issues, descriptions);
		createIssuesInJira(projectName, issues);

	}

	private static Document buildDocumet() throws Exception {

		File file = new File("InputFile.xml");

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		return dbf.newDocumentBuilder().parse(file);

	}

	private static Map<String ,NodeList> getSecondLvlNods(Document document, String projectName) {

		Node firstLvlNode 			= document.getFirstChild();
		NodeList secondLvlNodes 	= firstLvlNode.getChildNodes();

		Map<String ,NodeList> secondNodesMap = new HashMap<>();

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
					System.out.println("Незапланированный тег в XML");
					break;
				}
			}
		}

		return secondNodesMap;

	}

	private static List<NodeList> getThirdLvlNodes(NodeList issuesNode) {

		List<NodeList> thirdLvlNodes = new ArrayList<>();

		for(int i = 0; i < issuesNode.getLength(); i++ ) {

			Node currentNode = issuesNode.item(i);

			if (currentNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			thirdLvlNodes.add(currentNode.getChildNodes());

		}

		return thirdLvlNodes;
	}

	private static List<Issue> getIssuesFromXML(List<NodeList> issuesNodes) {

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

	private static List<Description> getDescriptionsFromXML(List<NodeList> descriptionsNodes) {

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

	private static void setDescriptionInIssue(List<Issue>issues, List<Description> descriptions) {

		issues.stream()
			.forEach(issue -> {int i = issue.getId();
				Description des = descriptions.stream().
						filter(description -> description.getId() == i)
						.findFirst().get();
				if(des != null) {
					issue.setDescription(des);
				}
			});

	}

	private static void createIssuesInJira(String projectName, List<Issue> issues) {

		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// projectName!!!!!!!!!!!!!!!!!!!!!!!!!!!
		JiraClient jiraClient = new JiraClient(
				"jaroslavM@yandex.ru",
				"bQeu8PREEVKOZM1bPkpz3E1C",
				"https://xtatone.atlassian.net/");

		issues.forEach(issue -> {
			String issueKey = jiraClient.createIssue(
					"PR",
					getIssueTypeId(issue.getType()),
					issue.getDescription().getSummary());
			jiraClient.setIssuePriority(issueKey, 1L);
//			System.out.println("Задача создана. Ключ задачи -" + issueKey);
		});

	}

	private static Long getIssueTypeId(String type) {

		long typeID = 0;
		
		switch (type.toLowerCase()) {

			case "epic": {
				typeID = 1000L;
				break;
			}

			case "task": {
				typeID = 10001L;
				break;
			}

			default: {
				System.out.println("Не удалось найти id type.");
				break;
			}
		}

		return typeID;
	}

}
