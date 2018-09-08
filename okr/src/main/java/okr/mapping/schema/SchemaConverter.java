package okr.mapping.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class SchemaConverter {
	
	private static final String SCHEMA_QUALIFIER = "qualifier";
	private static final String SCHEMA_GROUP = "group";
	private static final String SCHEMA_UUID = "uuid";
	private static final String FIELD_SEPARATOR_CHAR = ",";
	private static final String SCHEMA_EXTRACT_FIELDS = "extractFields";
	private static final String SCHEMA_EDGE_TO = "to";
	private static final String SCHEMA_EDGE_FROM = "from";
	private static final String SCHEMA_LABEL = "label";
	private static final String SCHEMA_ID = "id";
	
	
	private SchemaConverter() {}
	
	/**
	 * Naive schema mapping with direct knowledge of schema representation format
	 * 
	 * @param jsonTree
	 * 
	 * @param schema
	 */
	public static GraphSchema fromJson(JsonNode jsonTree) {
		JsonNode nodes = jsonTree.get("nodes");
		JsonNode edges = jsonTree.get("edges");

		if (!nodes.isArray() || !edges.isArray()) {
			throw new IllegalArgumentException("Illlegal schema expecting array of nodes and edges");
		}

		return new GraphSchema(mapSchemaNodes(nodes), mapSchemaEdges(edges), new ClosestContextStrategy());
	}

	// TODO configuration with null checks + identify mvp for set of fields
	private static Map<String, NodeSchema> mapSchemaNodes(JsonNode jsonNodes) {
		Iterator<JsonNode> itrNodes = jsonNodes.iterator();
		Map<String, NodeSchema> nodes = new HashMap<>();
		while (itrNodes.hasNext()) {
			JsonNode node = itrNodes.next();
			JsonNode id = node.get(SCHEMA_ID);
			JsonNode displayValue = node.get(SCHEMA_LABEL);
			JsonNode uniqueness = node.get(SCHEMA_UUID);
			JsonNode label = node.get(SCHEMA_GROUP);
			JsonNode qualifier = node.get(SCHEMA_QUALIFIER);

			String[] extraFields = new String[] {};
			if (null != node.get(SCHEMA_EXTRACT_FIELDS)) {
				extraFields = StringUtils.split(node.get(SCHEMA_EXTRACT_FIELDS).textValue(), FIELD_SEPARATOR_CHAR);
			}
			NodeSchema schNode = new NodeSchema(id.textValue(), displayValue.textValue(), uniqueness.textValue(),
					label.textValue(), qualifier.textValue(), extraFields);
			nodes.put(schNode.getId(), schNode);
		}
		return nodes;
	}

	private static Map<String, EdgeSchema> mapSchemaEdges(JsonNode jsonEdges) {
		Iterator<JsonNode> itrEdges = jsonEdges.iterator();
		Map<String, EdgeSchema> edges = new HashMap<>();
		while (itrEdges.hasNext()) {
			JsonNode edge = itrEdges.next();
			JsonNode id = edge.get(SCHEMA_ID);
			JsonNode from = edge.get(SCHEMA_EDGE_FROM);
			JsonNode to = edge.get(SCHEMA_EDGE_TO);
			JsonNode label = edge.get(SCHEMA_LABEL);
			String[] extraFields = StringUtils.split(edge.get(SCHEMA_EXTRACT_FIELDS).textValue(), FIELD_SEPARATOR_CHAR);
			EdgeSchema schEdge = new EdgeSchema(id.textValue(), from.textValue(), to.textValue(), label.textValue(),
					extraFields);
			edges.put(schEdge.id, schEdge);
		}
		return edges;
	}
}
