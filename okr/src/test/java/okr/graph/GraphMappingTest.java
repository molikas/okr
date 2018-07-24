package okr.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jgrapht.graph.DefaultEdge;
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
		GraphHolder<SchemaElement> gSchema = new SchemaGraph(schema);
		
		DepthFirstIterator<String, DefaultEdge> itr = new DepthFirstIterator<>(gSchema.graph);
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
		GraphHolder<GraphElementDTO> dInstance = new DocumentGraph(document);
		
		DepthFirstIterator<String, DefaultEdge> itr = new DepthFirstIterator<>(dInstance.graph);
		while (itr.hasNext()) {
			assertTrue("Missing", dInstance.cache.containsKey(itr.next()));
		}
		assertEquals("Not all schema elements were cached", 6, dInstance.cache.size()); 
	}	
	
	
	@Test
	public void mapSchemaToDocument() {
		JsonNode schema = jsonTestData.get("simple-team-schema.json");
		JsonNode document = jsonTestData.get("simple-team-document.json");
		
		ExpressionBasedMapper mapper = new ExpressionBasedMapper(BaseNode.class);
		mapper.map(new DocumentGraph(document), new SchemaGraph(schema));
	}

}
