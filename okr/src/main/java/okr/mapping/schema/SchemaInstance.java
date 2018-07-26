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
public class SchemaInstance{
	
	private JsonNode jsonTree;
	
	public final Map<String, NodeSchema> nodes = new HashMap<>();
	public final Map<String, EdgeSchema> edges = new HashMap<>();
	
	public SchemaInstance(JsonNode jsonTree) {
		super();
		this.jsonTree = jsonTree;
		init();
	}
	
	/**
	 * Naive schema mapping with direct knowledge of schema representation
	 * format 
	 * 
	 * @param schema
	 */
	private void init() {
		JsonNode nodes = jsonTree.get("nodes");
		JsonNode edges = jsonTree.get("edges");
		
		if (!nodes.isArray() || !edges.isArray()){
			throw new IllegalArgumentException("Illlegal schema expecting array of nodes and edges");
		}
		
		mapScehmaNodes(nodes);
		mapSchemaEdges(edges);
	}
	
	// TODO configuration with null checks + identify mvp for set of fields
	private void mapScehmaNodes(JsonNode jsonNodes) {
		Iterator<JsonNode> itrNodes = jsonNodes.iterator();
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
			nodes.put(schNode.getId(), schNode);
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
			this.edges.put(schEdge.id, schEdge);
		}
	}

}
