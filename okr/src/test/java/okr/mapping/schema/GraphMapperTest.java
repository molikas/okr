package okr.mapping.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import okr.mapping.repositories.LocalJsonRepository;
import okr.neo4j.repository.BaseNode;

public class GraphMapperTest {
	
	LocalJsonRepository jsonRepo = new LocalJsonRepository();

	/**
	 * Schema parsing test
	 */
	@Test
	public void simpleSchemaParsersTest() {
		GraphSchema gSchema = jsonRepo.retrieveSchema("simple-team-schema.json");

		assertEquals("Not all nodes were mapepd", 4, gSchema.nodes.values().size());
		assertEquals("Not all edges were mapped", 5, gSchema.edges.values().size()); 
	}
	
	/**
	 * Document parsing Test
	 */
	@Test
	public void simpleDcoumentParsersTest() {
		JsonNode docGraph = jsonRepo.retrieveDocument("simple-team-document.json");
		assertEquals("Not all schema elements were cached", 7, new GraphConverter().fromJson(docGraph).vertexSet().size()); 
	}	
	
	
	@Test
	public void mapSchemaToDocument() {
		GraphSchema gSchema = jsonRepo.retrieveSchema("simple-team-schema.json");
		JsonNode document = jsonRepo.retrieveDocument("simple-team-document.json");
		
		GraphMapper mapper = new BaseGraphMapper(BaseNode.class);
		Graph<BaseNode, DefaultWeightedEdge> rez = mapper.map(document, gSchema);
		 Set<BaseNode> result = rez.vertexSet();
		
		assertEquals("More nodes mapped than defined in schema", 4, result.size());
		
		Iterator<BaseNode> rItr = result.iterator();
		while (rItr.hasNext()) {
			BaseNode node = rItr.next();
			assertNotEquals("Noise instance got mapped", "noise", node.getLabels().iterator().next());
			assertFalse("Noise property mapped to proper instance", node.getProperties().containsKey("noise"));
		}
	}
	
	@Test
	public void singleInstanceContextFullGraphMapping() {
		GraphSchema gSchema = jsonRepo.retrieveSchema("two-role-team-schema.json");
		JsonNode document = jsonRepo.retrieveDocument("two-role-one-team-document.json");
		
		GraphMapper mapper = new BaseGraphMapper(BaseNode.class);
		Graph<BaseNode, DefaultWeightedEdge> result = mapper.map(document, gSchema);
		
		assertEquals("More node mapped than defined in schema", 4, result.vertexSet().size());
		assertEquals("Amount of Edges is incorrect", 5, result.edgeSet().size());
		
		
		assertTrue("Missing edge from QA_1 to WSO", containsEdge(result, "quality_1@email.com", "workstream@email.com"));
		assertTrue("Missing edge from QA_1 to Squad", containsEdge(result, "quality_1@email.com", "Team B"));
		
		assertTrue("Missing edge from QA_2 to WSO", containsEdge(result, "quality_2@email.com", "workstream@email.com"));
		assertTrue("Missing edge from QA_2 to Squad", containsEdge(result, "quality_2@email.com", "Team B"));
		
		assertTrue("Missing edge from WSO to Squad", containsEdge(result, "workstream@email.com", "Team B"));
		
		assertFalse("Edge direction is incorrect", containsEdge(result, "Team B", "workstream@email.com"));
	}
	
	private boolean containsEdge(Graph<BaseNode, DefaultWeightedEdge> dGraph, String fromID, String toId) {
		Iterator<DefaultWeightedEdge> eItr = dGraph.edgeSet().iterator();
		while(eItr.hasNext()) {
			DefaultWeightedEdge edge = eItr.next();
			BaseNode source = dGraph.getEdgeSource(edge);
			BaseNode target = dGraph.getEdgeTarget(edge);
			if (source.getUuid().equals(fromID) && target.getUuid().equals(toId)) {
				return true;
			}
		}
		return false;
	}
	
	@Test
	public void multipleInstanceContextFullGraphMapping() {
		GraphSchema gSchema = jsonRepo.retrieveSchema("unit-schema.json");
		JsonNode document = jsonRepo.retrieveDocument("unit-document.json");
		
		GraphMapper mapper = new BaseGraphMapper(BaseNode.class);
		Graph<BaseNode, DefaultWeightedEdge> result = mapper.map(document, gSchema);
		
		Map<String, List<BaseNode>> qualifiedNodes = GraphUtils.groupBySchemaId(result);
		
		assertEquals("More node mapped than defined in schema", 3, qualifiedNodes.get("qa_node").size());
		assertEquals("More node mapped than defined in schema", 2, qualifiedNodes.get("wso_node").size());
		assertEquals("More node mapped than defined in schema", 2, qualifiedNodes.get("sq_node").size());
		assertEquals("More node mapped than defined in schema", 1, qualifiedNodes.get("unit_node").size());		
		
		assertEquals("Inccorect amount of nodes initialized", 8, result.vertexSet().size());
		assertEquals("Amount of Edges is incorrect", 10, result.edgeSet().size());
		
	}

}
