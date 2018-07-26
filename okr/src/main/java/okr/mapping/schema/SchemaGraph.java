package okr.mapping.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Holds configuration of available graph schema mappers
 * 
 * @author isidenica
 */
public class SchemaGraph extends GraphHolder<SchemaElement>{

	
	public final Graph<String, DefaultWeightedEdge> graph = new DirectedMultigraph<>(DefaultWeightedEdge.class);
	
	public static final String NODE_LABELS_KEY = "node-labels";
	public static final String NODE_PROPERTIES_KEY = "node-properties";
	public static final String RELATIONSHIPS_KEY = "graph-relationships";
	
	protected Map<String, String[]> cfg = new HashMap<>();
	
	//Configuration + default mapping if there is no external configuration
	private String[] labels = new String[] {"issueType.name"};
	private String[] properties = new String[] {"key", "summary", "resolution.name", "status.name"};
	//	--------------
	
	public SchemaGraph() {
		super();
		cfg.put(NODE_LABELS_KEY, labels);
		cfg.put(NODE_PROPERTIES_KEY, properties);
	}
	
	/**
	 * Naive schema mapping with direct knowledge of schema representation
	 * format 
	 * 
	 * @param schema
	 */
	@Override
	public void init(JsonNode schema) {
		JsonNode nodes = schema.get("nodes");
		JsonNode edges = schema.get("edges");
		
		if (!nodes.isArray() || !edges.isArray()){
			throw new IllegalArgumentException("Illlegal schema expecting array of nodes and edges");
		}
		
		mapScehmaNodes(nodes);
		mapSchemaEdges(edges);
	}
	
	// TODO configuraiton with null checks + identify mvp for set of fields
	private void mapScehmaNodes(JsonNode nodes) {
		Iterator<JsonNode> itrNodes = nodes.iterator();
		while (itrNodes.hasNext()) {
			JsonNode node = itrNodes.next();
			JsonNode id = node.get("id");
			JsonNode displayValue = node.get("label");
			JsonNode uniqueness = node.get("uuid");
			JsonNode label = node.get("group");
			JsonNode qualifier = node.get("qualifier");
			
			String[] extraFields = new String[] {};
			if (null != node.get("extractFields")) {
				extraFields = StringUtils.split(node.get("extractFields").textValue(), ",");
			}
			NodeSchema schNode = new NodeSchema(id.textValue(), displayValue.textValue(), uniqueness.textValue(), label.textValue(), qualifier.textValue(), extraFields);
			graph.addVertex(schNode.getId());
			cache.put(schNode.getId(), schNode);
		}
	}
	
	private void mapSchemaEdges(JsonNode edges) {
		Iterator<JsonNode> itrEdges = edges.iterator();
		while (itrEdges.hasNext()) {
			JsonNode edge = itrEdges.next();
			JsonNode id = edge.get("id");
			JsonNode from = edge.get("from");
			JsonNode to = edge.get("to");
			JsonNode label = edge.get("label");
			String[] extraFields = StringUtils.split(edge.get("extractFields").textValue(), ",");
			EdgeSchema schEdge = new EdgeSchema(id.textValue(), from.textValue(), to.textValue(), label.textValue(), extraFields);
			graph.addEdge(from.textValue(), to.textValue());
			cache.put(schEdge.getId(), schEdge);
		}
	}
	
	public String[] getLables() {
		return cfg.get(SchemaGraph.NODE_LABELS_KEY);
	}

	public String[] getProperties() {
		return cfg.get(SchemaGraph.NODE_PROPERTIES_KEY);
	}

}
