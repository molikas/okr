package okr.mapping.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Holds configuration of available graph schema mappers
 * 
 * @author isidenica
 */
public class GraphSchema extends ContextHolder<SchemaElement>{

	public static final String NODE_LABELS_KEY = "node-labels";
	public static final String NODE_PROPERTIES_KEY = "node-properties";
	public static final String RELATIONSHIPS_KEY = "graph-relationships";
	
	protected Map<String, String[]> cfg = new HashMap<>();
	
	//Configuration + default mapping if there is no external configuration
	private String[] labels = new String[] {"issueType.name"};
	private String[] properties = new String[] {"key", "summary", "resolution.name", "status.name"};
	//	--------------
	
	public GraphSchema() {
		super();
		cfg.put(NODE_LABELS_KEY, labels);
		cfg.put(NODE_PROPERTIES_KEY, properties);
	}
	
	public GraphSchema(JsonNode rootObj) {
		super(rootObj);
	}
	
	/**
	 * Naive schema mapping with direct knowledge of schema representation
	 * format 
	 * 
	 * @param schema
	 */
	@Override
	protected void init(JsonNode schema) {
		JsonNode nodes = schema.get("nodes");
		JsonNode edges = schema.get("edges");
		
		if (!nodes.isArray() || !edges.isArray()){
			throw new IllegalArgumentException("Illlegal schema expecting array of nodes and edges");
		}
		
		mapNodes(nodes);
		mapEdges(edges);
	}
	private void mapNodes(JsonNode nodes) {
		Iterator<JsonNode> itrNodes = nodes.iterator();
		while (itrNodes.hasNext()) {
			JsonNode node = itrNodes.next();
			JsonNode id = node.get("id");
			JsonNode label = node.get("label");
			JsonNode uniqueness = node.get("uniqueness");
			JsonNode group = node.get("group");
			String[] extraFields = StringUtils.split(node.get("extractFields").textValue(), ",");
			SchemaNode schNode = new SchemaNode(id.textValue(), label.textValue(), uniqueness.textValue(), group.textValue(), extraFields);
			graph.addVertex(schNode.getId());
			cache.put(schNode.getId(), schNode);
		}
	}
	
	private void mapEdges(JsonNode edges) {
		Iterator<JsonNode> itrEdges = edges.iterator();
		while (itrEdges.hasNext()) {
			JsonNode edge = itrEdges.next();
			JsonNode id = edge.get("id");
			JsonNode from = edge.get("from");
			JsonNode to = edge.get("to");
			JsonNode label = edge.get("label");
			String[] extraFields = StringUtils.split(edge.get("extractFields").textValue(), ",");
			SchemaNode schEdge = new SchemaNode(id.textValue(), from.textValue(), to.textValue(), label.textValue(), extraFields);
			graph.addEdge(from.textValue(), to.textValue());
			cache.put(schEdge.getId(), schEdge);
		}
	}
	
	public String[] getLables() {
		return cfg.get(GraphSchema.NODE_LABELS_KEY);
	}

	public String[] getProperties() {
		return cfg.get(GraphSchema.NODE_PROPERTIES_KEY);
	}

}
