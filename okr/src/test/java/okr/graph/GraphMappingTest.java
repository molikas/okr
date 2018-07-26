package okr.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.DepthFirstIterator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.JsonNode;

import okr.mapping.schema.DocumentGraph;
import okr.mapping.schema.ExpressionBasedMapper;
import okr.mapping.schema.GraphElementDTO;
import okr.mapping.schema.GraphHolder;
import okr.mapping.schema.SchemaElement;
import okr.mapping.schema.SchemaGraph;
import okr.neo4j.repository.BaseNode;

@RunWith(SpringJUnit4ClassRunner.class)
public class GraphMappingTest extends BaseGraphTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Schema parsing test
	 */
	@Test
	public void simpleSchemaParsersTest() {
		JsonNode schema = jsonTestData.get("simple-team-schema.json");
		
		SchemaGraph gSchema = new SchemaGraph();
		gSchema.init(schema);
		
		DepthFirstIterator<String, DefaultWeightedEdge> itr = new DepthFirstIterator<>(gSchema.graph);
		while (itr.hasNext()) {
			String node = itr.next();
			assertTrue("Node data missing from cache: " + node, gSchema.cache.containsKey(node));
		}
		assertEquals("Not all schema elements were cached", 9, gSchema.cache.size()); 
	}
	
	/**
	 * Document parsing Test
	 */
	@Test
	public void simpleDcoumentParsersTest() {
		JsonNode document = jsonTestData.get("simple-team-document.json");
		
		ExpressionBasedMapper mapper = new ExpressionBasedMapper(BaseNode.class);
		DocumentGraph docGraph = new DocumentGraph();
		docGraph.init(document);
		
		
		
		DepthFirstIterator<String, DefaultWeightedEdge> itr = new DepthFirstIterator<>(docGraph.documentGraph);
		while (itr.hasNext()) {
			assertTrue("Missing", docGraph.cache.containsKey(itr.next()));
		}
		assertEquals("Not all schema elements were cached", 7, docGraph.cache.size()); 
	}	
	
	
	@Test
	public void mapSchemaToDocument() {
		JsonNode schema = jsonTestData.get("simple-team-schema.json");
		JsonNode document = jsonTestData.get("simple-team-document.json");
		
		ExpressionBasedMapper mapper = new ExpressionBasedMapper(BaseNode.class);
		DocumentGraph docGraph = new DocumentGraph();
		docGraph.init(document);
		
		SchemaGraph schemaGraph = new SchemaGraph();
		schemaGraph.init(schema);
		Collection<BaseNode> result = mapper.mapDocumentUsingSchema(docGraph, schemaGraph);
		
		assertEquals("More node mapped than defined in schema", 4, result.size());
		for (BaseNode baseNode : result) {
			assertNotEquals("Noise instance got mapped", "noise", baseNode.getLabels().iterator().next());
			assertFalse("Noise property mapped to proper instance", baseNode.getProperties().containsKey("noise"));
		}
	}
	
	@Test
	public void singleInstanceContextFullGraphMapping() {
		JsonNode schema = jsonTestData.get("two-role-team-schema.json");
		JsonNode document = jsonTestData.get("two-role-one-team-document.json");
		
		ExpressionBasedMapper mapper = new ExpressionBasedMapper(BaseNode.class);
		DocumentGraph docGraph = new DocumentGraph();
		docGraph.init(document);
		
		SchemaGraph schemaGraph = new SchemaGraph();
		schemaGraph.init(schema);
		mapper.mapDocumentUsingSchema(docGraph, schemaGraph);
		
		assertEquals("More node mapped than defined in schema", 4, mapper.iGraph.cache.size());
		assertEquals("Amount of Edges is incorrect", 5, mapper.iGraph.graph.edgeSet().size());
		
		assertNotNull("Missing edge from QA_1 to WSO", mapper.iGraph.graph.getEdge("quality_1@email.com", "workstream@email.com"));
		assertNotNull("Missing edge from QA_1 to Squad", mapper.iGraph.graph.getEdge("quality_1@email.com", "Team B"));
		
		assertNotNull("Missing edge from QA_2 to WSO", mapper.iGraph.graph.getEdge("quality_2@email.com", "workstream@email.com"));
		assertNotNull("Missing edge from QA_2 to Squad", mapper.iGraph.graph.getEdge("quality_2@email.com", "Team B"));
		
		assertNotNull("Missing edge from WSO to Squad", mapper.iGraph.graph.getEdge("workstream@email.com", "Team B"));
		
		assertNull("Edge direction is incorrect", mapper.iGraph.graph.getEdge("Team B", "workstream@email.com"));
	}
	
	@Test
	public void multipleInstanceContextFullGraphMapping() {
		JsonNode schema = jsonTestData.get("unit-schema.json");
		JsonNode document = jsonTestData.get("unit-document.json");
		
		ExpressionBasedMapper mapper = new ExpressionBasedMapper(BaseNode.class);
		DocumentGraph docGraph = new DocumentGraph();
		docGraph.init(document);
		
		SchemaGraph schemaGraph = new SchemaGraph();
		schemaGraph.init(schema);
		mapper.mapDocumentUsingSchema(docGraph, schemaGraph);
		
		assertEquals("More node mapped than defined in schema", 3, docGraph.qualifiedNodesCache.get("qa_node").size());
		assertEquals("More node mapped than defined in schema", 2, docGraph.qualifiedNodesCache.get("wso_node").size());
		assertEquals("More node mapped than defined in schema", 2, docGraph.qualifiedNodesCache.get("sq_node").size());
		assertEquals("More node mapped than defined in schema", 1, docGraph.qualifiedNodesCache.get("unit_node").size());
		assertEquals("Amount of Edges is incorrect", 10, docGraph.qualifiedNodesGraph.edgeSet().size());
		
	}

}
