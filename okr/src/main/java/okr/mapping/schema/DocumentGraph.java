package okr.mapping.schema;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Holds document instance that should be used for mapping
 * using a concrete {@link SchemaGraph}
 * 
 * @author isidenica
 */
public class DocumentGraph extends GraphHolder<GraphElementDTO> {

	private static final Logger log = Logger.getLogger(DocumentGraph.class.getName());

	public final Graph<String, DefaultWeightedEdge> documentGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
	
	@Override
	public void init(JsonNode rootObj) {
		parseJsonToGraph(rootObj.hashCode(), "root", rootObj);
	}
	
	/*
	 * Recursive document parsing to a graph representation
	 */
	private void parseJsonToGraph(Integer parentHash, String currentField, JsonNode currentNode) {	
		// Store value nodes as properties on parent
		parseValueNodes(parentHash, currentField, currentNode);
		
		// Array or Object get a node, and linked with parent
		parseContainers(parentHash, currentField, currentNode);
		
		// Objects are nodes
		parseObjects(currentNode);
		
		// Objects in arrays are nodes too
		parseArray(currentField, currentNode); 
	}

	private void parseValueNodes(Integer parentHash, String currentField, JsonNode currentNode) {
		if (currentNode.isValueNode()) {
			//TODO normal value conversion
			if (currentNode.isTextual())
				appendParentProperties(parentHash, currentField, currentNode.asText());
			else appendParentProperties(parentHash, currentField, currentNode.toString());
		}
	}

	private void parseContainers(Integer parentHash, String currentField, JsonNode currentNode) {
		if (currentNode.isContainerNode()) {
			documentGraph.addVertex(Integer.toString(currentNode.hashCode()));
			if (parentHash != currentNode.hashCode()) {
				documentGraph.addEdge(Integer.toString(parentHash), Integer.toString(currentNode.hashCode()));
			}
			GraphElementDTO graphElementDTO = new GraphElementDTO(Integer.toString(currentNode.hashCode()), currentField, Integer.toString(parentHash));
			cache.put(Integer.toString(currentNode.hashCode()), graphElementDTO);
		}
	}

	private void parseObjects(JsonNode currentNode) {
		if (currentNode.isObject()) { 
			Iterator<Entry<String, JsonNode>> fieldsItr = currentNode.fields();
			while(fieldsItr.hasNext()) {
				Entry<String, JsonNode> childNode = fieldsItr.next();
				parseJsonToGraph(currentNode.hashCode(), childNode.getKey(), childNode.getValue());
			}
		}
	}

	private void parseArray(String currentField, JsonNode currentNode) {
		if (currentNode.isArray()) {
			Iterator<JsonNode> elemItr = currentNode.elements();
			int i = 0;
			while (elemItr.hasNext()) {
				JsonNode childNode = elemItr.next();
				parseJsonToGraph(currentNode.hashCode(), currentField+"["+i+"]", childNode);
				i = i+1;
			}
		}
	}

	private void appendParentProperties(Integer parentHash, String pName, String pValue) {
		GraphElementDTO parentNode = cache.get(parentHash.toString());

		if (parentNode == null) log.info("Parent node not registered, paarentHash: "+ parentHash);
		else parentNode.getProperties().put(pName, pValue);
	}

}
