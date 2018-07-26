package okr.mapping.schema;

import java.util.Iterator;

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
	
	public SchemaGraph(JsonNode jsonTree) {
		super();
		super.jsonTree = jsonTree;
	}
	
	/**
	 * Naive schema mapping with direct knowledge of schema representation
	 * format 
	 * 
	 * @param schema
	 */
	public void init() {
		JsonNode nodes = jsonTree.get("nodes");
		JsonNode edges = jsonTree.get("edges");
		
		if (!nodes.isArray() || !edges.isArray()){
			throw new IllegalArgumentException("Illlegal schema expecting array of nodes and edges");
		}
		
		mapScehmaNodes(nodes);
		mapSchemaEdges(edges);
	}
	
	// TODO configuration with null checks + identify mvp for set of fields
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

}
