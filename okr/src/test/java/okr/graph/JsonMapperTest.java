package okr.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.DepthFirstIterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import okr.mapping.schema.DocumentGraph;
import okr.mapping.schema.ExpressionBasedMapper;
import okr.mapping.schema.LocalJsonRepository;
import okr.mapping.schema.SchemaGraph;
import okr.neo4j.repository.BaseNode;

@RunWith(SpringJUnit4ClassRunner.class)
public class JsonMapperTest {
	
	LocalJsonRepository jsonRepo = new LocalJsonRepository();

	/**
	 * Schema parsing test
	 */
	@Test
	public void simpleSchemaParsersTest() {
		SchemaGraph gSchema = jsonRepo.retrieveSchema("simple-team-schema.json");
		
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
		DocumentGraph docGraph = jsonRepo.retrieveDocument("simple-team-document.json");
		
		DepthFirstIterator<String, DefaultWeightedEdge> itr = new DepthFirstIterator<>(docGraph.documentGraph);
		while (itr.hasNext()) {
			assertTrue("Missing", docGraph.cache.containsKey(itr.next()));
		}
		assertEquals("Not all schema elements were cached", 7, docGraph.cache.size()); 
	}	
	
	
	@Test
	public void mapSchemaToDocument() {
		SchemaGraph schemaGraph = jsonRepo.retrieveSchema("simple-team-schema.json");
		DocumentGraph docGraph = jsonRepo.retrieveDocument("simple-team-document.json");
		
		ExpressionBasedMapper mapper = new ExpressionBasedMapper(BaseNode.class);
		mapper.mapDocumentUsingSchema(docGraph, schemaGraph);
		Set<String> result = mapper.iGraph.graph.vertexSet();
		
		assertEquals("More nodes mapped than defined in schema", 4, result.size());
		
		Iterator<String> rItr = result.iterator();
		while (rItr.hasNext()) {
			BaseNode baseNode = mapper.iGraph.cache.get(rItr.next());
			assertNotEquals("Noise instance got mapped", "noise", baseNode.getLabels().iterator().next());
			assertFalse("Noise property mapped to proper instance", baseNode.getProperties().containsKey("noise"));
		}
	}
	
	@Test
	public void singleInstanceContextFullGraphMapping() {
		SchemaGraph schemaGraph = jsonRepo.retrieveSchema("two-role-team-schema.json");
		DocumentGraph docGraph = jsonRepo.retrieveDocument("two-role-one-team-document.json");
		
		ExpressionBasedMapper mapper = new ExpressionBasedMapper(BaseNode.class);
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
		SchemaGraph schemaGraph = jsonRepo.retrieveSchema("unit-schema.json");
		DocumentGraph docGraph = jsonRepo.retrieveDocument("unit-document.json");
		
		ExpressionBasedMapper mapper = new ExpressionBasedMapper(BaseNode.class);
		mapper.mapDocumentUsingSchema(docGraph, schemaGraph);
		
		assertEquals("More node mapped than defined in schema", 3, mapper.qualifiedNodesCache.get("qa_node").size());
		assertEquals("More node mapped than defined in schema", 2, mapper.qualifiedNodesCache.get("wso_node").size());
		assertEquals("More node mapped than defined in schema", 2, mapper.qualifiedNodesCache.get("sq_node").size());
		assertEquals("More node mapped than defined in schema", 1, mapper.qualifiedNodesCache.get("unit_node").size());
//		assertEquals("Amount of Edges is incorrect", 10, mapper.qualifiedNodesGraph.edgeSet().size());
		
		assertEquals("Inccorect amount of nodes initialized", 8, mapper.iGraph.cache.size());
		assertEquals("Amount of Edges is incorrect", 10, mapper.iGraph.graph.edgeSet().size());
		
	}

}
