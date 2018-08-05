package okr.mapping.schema;

import java.util.Iterator;
import java.util.Map.Entry;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Holds document instance that should be used for mapping
 * using a concrete {@link SchemaInstance}
 * 
 * @author isidenica
 */
public class DocumentInstance {

	public final Graph<GraphElementDTO, DefaultWeightedEdge> documentGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
	
	public DocumentInstance (JsonNode jsonTree) {
		super();
		init(jsonTree);
	}
	
	private void init(JsonNode jsonTree) {
		GraphElementDTO rootDto = new GraphElementDTO(jsonTree.hashCode(), "root");
		parseJsonToGraph(rootDto, rootDto.getName(), jsonTree);
	}
	
	/*
	 * Recursive document parsing to a graph representation
	 */
	private void parseJsonToGraph(GraphElementDTO parentNode, String currentField, JsonNode currentNode) {	
		// Store value nodes as properties on parent
		parseValueNodes(parentNode, currentField, currentNode);
		
		// Array or Object get a node, and linked with parent
		GraphElementDTO currentDTO = createNodeForContainer(parentNode, currentField, currentNode);
		if (currentDTO == null) return;
		
		// Objects are nodes
		parseObjects(currentDTO, currentNode);
		
		// Objects in arrays are nodes too
		parseArray(currentDTO, currentField, currentNode); 
	}

	private void parseValueNodes(GraphElementDTO parentNode, String currentField, JsonNode currentNode) {
		if (currentNode.isValueNode()) {
			//TODO normal value conversion
			if (currentNode.isTextual()) parentNode.getProperties().put(currentField, currentNode.asText());
			else parentNode.getProperties().put(currentField, currentNode.toString());
		}
	}

	private GraphElementDTO createNodeForContainer(GraphElementDTO parentNode, String currentField, JsonNode currentNode) {
		if (currentNode.isContainerNode()) {
			GraphElementDTO currentDTO = new GraphElementDTO(currentNode.hashCode(), currentField);
			documentGraph.addVertex(currentDTO);
			
			if (!parentNode.equals(currentDTO)) {
				documentGraph.addEdge(parentNode, currentDTO);
			}		
			return currentDTO;
		}
		return null;
	}

	private void parseObjects(GraphElementDTO currentDTO, JsonNode currentNode) {
		if (currentNode.isObject()) { 
			Iterator<Entry<String, JsonNode>> fieldsItr = currentNode.fields();
			while(fieldsItr.hasNext()) {
				Entry<String, JsonNode> childNode = fieldsItr.next();
				parseJsonToGraph(currentDTO, childNode.getKey(), childNode.getValue());
			}
		}
	}

	private void parseArray(GraphElementDTO currentDTO, String currentField, JsonNode currentNode) {
		if (currentNode.isArray()) {
			Iterator<JsonNode> elemItr = currentNode.elements();
			int i = 0;
			while (elemItr.hasNext()) {
				JsonNode childNode = elemItr.next();
				parseJsonToGraph(currentDTO, currentField+"["+i+"]", childNode);
				i = i+1;
			}
		}
	}

}
