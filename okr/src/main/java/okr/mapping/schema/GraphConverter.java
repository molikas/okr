package okr.mapping.schema;

import java.util.Iterator;
import java.util.Map.Entry;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import com.fasterxml.jackson.databind.JsonNode;

import okr.neo4j.repository.BaseNode;

/**
 * Holds document instance that should be used for mapping
 * using a concrete {@link GraphSchema}
 * 
 * @author isidenica
 */
public class GraphConverter {

	public Graph<BaseNode, DefaultWeightedEdge> fromJson(JsonNode jsonTree) {
		Graph<BaseNode, DefaultWeightedEdge> documentGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
		parseJsonToGraph(null, "root", jsonTree, documentGraph);
		
		return documentGraph;
	}

	/*
	 * Recursive document parsing to a graph representation
	 */
	private void parseJsonToGraph(BaseNode parentNode, String currentField, JsonNode currentNode, Graph<BaseNode, DefaultWeightedEdge> documentGraph) {
		// Store value nodes as properties on parent
		if (currentNode.isValueNode()) {
			parseValueNodes(parentNode, currentField, currentNode);
			return;
		}

		// Array or Object get a node, and linked with parent
		if (currentNode.isContainerNode()) {
			BaseNode currentBaseNode = new BaseNode(currentField);
			documentGraph.addVertex(currentBaseNode);

			if (parentNode != null && !parentNode.equals(currentBaseNode) && !documentGraph.vertexSet().isEmpty()) {
				documentGraph.addEdge(parentNode, currentBaseNode);
			}	
			// Objects are nodes
			if (currentNode.isObject()) { 
				parseObjects(currentBaseNode, currentNode, documentGraph);
			}
			// Objects in arrays are nodes too
			if (currentNode.isArray()) {
				parseArray(currentBaseNode, currentField, currentNode, documentGraph);
			}
			
			documentGraph.addVertex(currentBaseNode);		
		}
	}

	private void parseValueNodes(BaseNode parentNode, String currentField, JsonNode currentNode) {
		//TODO normal value conversion
		if (currentNode.isTextual()) parentNode.getProperties().put(currentField, currentNode.asText());
		else parentNode.getProperties().put(currentField, currentNode.toString());
	}

	private void parseObjects(BaseNode currentBaseNode, JsonNode currentNode, Graph<BaseNode, DefaultWeightedEdge> documentGraph) {
		Iterator<Entry<String, JsonNode>> fieldsItr = currentNode.fields();
		while(fieldsItr.hasNext()) {
			Entry<String, JsonNode> childNode = fieldsItr.next();
			parseJsonToGraph(currentBaseNode, childNode.getKey(), childNode.getValue(), documentGraph);
		}
	}

	private void parseArray(BaseNode currentBaseNode, String currentField, JsonNode currentNode, Graph<BaseNode, DefaultWeightedEdge> documentGraph) {
		Iterator<JsonNode> elemItr = currentNode.elements();
		int i = 0;
		while (elemItr.hasNext()) {
			JsonNode childNode = elemItr.next();
			parseJsonToGraph(currentBaseNode, currentField+"["+i+"]", childNode, documentGraph);
			i = i+1;
		}
	}

}
