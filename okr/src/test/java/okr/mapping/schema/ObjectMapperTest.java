package okr.mapping.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Resolution;
import com.atlassian.jira.rest.client.api.domain.Status;

import okr.neo4j.repository.Objective;

/*
 * Generic schema mapper tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ObjectMapperTest {

	private GraphMapper gMapper = new ExpressionBasedMapper(Objective.class);
	
	LocalJsonRepository jsonRepo = new LocalJsonRepository();
	
	/*
	 * Sanity check for create operation
	 */
	@Test
	public void saveObjectiveTest() {
		Iterable<Issue> source = createIssues();
		SchemaGraph schemaGraph = jsonRepo.retrieveSchema("okr-schema.json");
		
		Collection<Objective> results = gMapper.mapNodes(source, schemaGraph);
		
		assertTrue("No results returned while mapping an issue", results != null);
		assertEquals("Mapping returned empty list of results", true, results.iterator().hasNext());
		
		Objective objective = results.iterator().next();
		
		assertEquals("Labels were not mapped", false, objective.getLabels().isEmpty());
		assertEquals("Issue Type was not set as a node label", "Mock Type", objective.getLabels().iterator().next());
		
		assertEquals("Properties were not mapped", false, objective.getProperties().isEmpty());
	}
	
	/* ---Helper Methods--- */
	private Iterable<Issue> createIssues() {
		ArrayList<Issue> set = new ArrayList<>();
		set.add(createIssue());
		return set;
	}

	private Issue createIssue() {
		IssueType mockIssueType = new IssueType(null, 0l, "Mock Type", false, "", null);
		Status mockStatus = new Status(null, 0l, "Open", "", null);
		Resolution resolution = new Resolution(null, 0l, "Resolved", "");
		
		Issue issue = new Issue("Summary", null, "Ticket-1", 0l, null, mockIssueType, mockStatus, null, null, resolution,
				null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null,
				null, null, null, null, null);
		return issue;

	}
}
