package okr.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import okr.mapping.schema.DocumentInstance;
import okr.mapping.schema.ExpressionBasedMapper;
import okr.mapping.schema.LocalJsonRepository;
import okr.mapping.schema.SchemaInstance;
import okr.neo4j.repository.BaseNode;

public class JsonMapperTest {
	
	LocalJsonRepository jsonRepo = new LocalJsonRepository();

	/**
	 * Schema parsing test
	 */
	@Test
	public void simpleSchemaParsersTest() {
		SchemaInstance gSchema = jsonRepo.retrieveSchema("simple-team-schema.json");

		assertEquals("Not all nodes were mapepd", 4, gSchema.nodes.values().size());
		assertEquals("Not all edges were mapped", 5, gSchema.edges.values().size()); 
	}
	
	/**
	 * Document parsing Test
	 */
	@Test
	public void simpleDcoumentParsersTest() {
		DocumentInstance docGraph = jsonRepo.retrieveDocument("simple-team-document.json");
		
		assertEquals("Not all schema elements were cached", 7, docGraph.documentGraph.vertexSet().size()); 
	}	
	
	
	@Test
	public void mapSchemaToDocument() {
		SchemaInstance schemaGraph = jsonRepo.retrieveSchema("simple-team-schema.json");
		DocumentInstance docGraph = jsonRepo.retrieveDocument("simple-team-document.json");
		
		ExpressionBasedMapper mapper = new ExpressionBasedMapper(BaseNode.class);
		mapper.mapDocumentUsingSchema(docGraph, schemaGraph);
		 Set<BaseNode> result = mapper.mappedGraph.vertexSet();
		
		assertEquals("More nodes mapped than defined in schema", 4, result.size());
		
		Iterator<BaseNode> rItr = result.iterator();
		while (rItr.hasNext()) {
			BaseNode baseNode = rItr.next();
			assertNotEquals("Noise instance got mapped", "noise", baseNode.getLabels().iterator().next());
			assertFalse("Noise property mapped to proper instance", baseNode.getProperties().containsKey("noise"));
		}
	}
	
	@Test
	public void singleInstanceContextFullGraphMapping() {
		SchemaInstance schemaGraph = jsonRepo.retrieveSchema("two-role-team-schema.json");
		DocumentInstance docGraph = jsonRepo.retrieveDocument("two-role-one-team-document.json");
		
		ExpressionBasedMapper mapper = new ExpressionBasedMapper(BaseNode.class);
		mapper.mapDocumentUsingSchema(docGraph, schemaGraph);
		
		assertEquals("More node mapped than defined in schema", 4, mapper.mappedGraph.vertexSet().size());
		assertEquals("Amount of Edges is incorrect", 5, mapper.mappedGraph.edgeSet().size());
		
		
		assertTrue("Missing edge from QA_1 to WSO", containsEdge(mapper.mappedGraph, "quality_1@email.com", "workstream@email.com"));
		assertTrue("Missing edge from QA_1 to Squad", containsEdge(mapper.mappedGraph, "quality_1@email.com", "Team B"));
		
		assertTrue("Missing edge from QA_2 to WSO", containsEdge(mapper.mappedGraph, "quality_2@email.com", "workstream@email.com"));
		assertTrue("Missing edge from QA_2 to Squad", containsEdge(mapper.mappedGraph, "quality_2@email.com", "Team B"));
		
		assertTrue("Missing edge from WSO to Squad", containsEdge(mapper.mappedGraph, "workstream@email.com", "Team B"));
		
		assertFalse("Edge direction is incorrect", containsEdge(mapper.mappedGraph, "Team B", "workstream@email.com"));
	}
	
	private boolean containsEdge(Graph<BaseNode, DefaultEdge> iGraph, String fromID, String toId) {
		Iterator<DefaultEdge> eItr = iGraph.edgeSet().iterator();
		while(eItr.hasNext()) {
			DefaultEdge edge = eItr.next();
			BaseNode source = iGraph.getEdgeSource(edge);
			BaseNode target = iGraph.getEdgeTarget(edge);
			if (source.getUuid().equals(fromID) && target.getUuid().equals(toId)) {
				return true;
			}
		}
		return false;
	}
	
	@Test
	public void multipleInstanceContextFullGraphMapping() {
		SchemaInstance schemaGraph = jsonRepo.retrieveSchema("unit-schema.json");
		DocumentInstance docGraph = jsonRepo.retrieveDocument("unit-document.json");
		
		ExpressionBasedMapper mapper = new ExpressionBasedMapper(BaseNode.class);
		mapper.mapDocumentUsingSchema(docGraph, schemaGraph);
		
		assertEquals("More node mapped than defined in schema", 3, mapper.qualifiedNodesCache.get("qa_node").size());
		assertEquals("More node mapped than defined in schema", 2, mapper.qualifiedNodesCache.get("wso_node").size());
		assertEquals("More node mapped than defined in schema", 2, mapper.qualifiedNodesCache.get("sq_node").size());
		assertEquals("More node mapped than defined in schema", 1, mapper.qualifiedNodesCache.get("unit_node").size());
		
		assertEquals("Inccorect amount of nodes initialized", 8, mapper.mappedGraph.vertexSet().size());
		assertEquals("Amount of Edges is incorrect", 10, mapper.mappedGraph.edgeSet().size());
		
	}

}
