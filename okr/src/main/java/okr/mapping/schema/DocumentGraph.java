package okr.mapping.schema;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Holds document instance that should be used for mapping
 * using a concrete {@link SchemaGraph}
 * 
 * @author isidenica
 */
public class DocumentGraph extends GraphHolder<GraphElementDTO> {

	private static final Logger log = Logger.getLogger(DocumentGraph.class.getName());
	
	public DocumentGraph(JsonNode rootObj) {
		super(rootObj);
	}

	@Override
	protected void init(JsonNode rootObj) {
		parseDocument(rootObj.hashCode(), "root", rootObj);
	}
	
	public void parseDocument(Integer parentHash, String currentField, JsonNode currentNode) {	
		
		// Store value nodes as properties on parent
		if (currentNode.isValueNode()) {
			//TODO normal value conversion
			if (currentNode.isTextual())
				appendParentProperties(parentHash, currentField, currentNode.asText());
			else appendParentProperties(parentHash, currentField, currentNode.toString());
			
			return;
		}
		
		// Array or Object get a node, and linked with parent
		if (currentNode.isContainerNode()) {
			graph.addVertex(Integer.toString(currentNode.hashCode()));
			if (parentHash != currentNode.hashCode()) {
				graph.addEdge(Integer.toString(parentHash), Integer.toString(currentNode.hashCode()));
			}
			GraphElementDTO nodeDTO = new GraphElementDTO(Integer.toString(currentNode.hashCode()), currentField);
			cache.put(Integer.toString(currentNode.hashCode()), nodeDTO);
		}
		
		// Objects are nodes
		if (currentNode.isObject()) { 
			Iterator<Entry<String, JsonNode>> fieldsItr = currentNode.fields();
			while(fieldsItr.hasNext()) {
				Entry<String, JsonNode> childNode = fieldsItr.next();
				parseDocument(currentNode.hashCode(), childNode.getKey(), childNode.getValue());
			}
			return;
		}
		
		// Objects in arrays are nodes too
		if (currentNode.isArray()) {
			Iterator<JsonNode> elemItr = currentNode.elements();
			int i = 0;
			while (elemItr.hasNext()) {
				JsonNode childNode = elemItr.next();
				parseDocument(currentNode.hashCode(), currentField+"["+i+"]", childNode);
				i = i++;
			}
			return;
		} 
		
		log.info("Unprocessed node type: "+ currentNode.getNodeType().toString());
	}

	private void appendParentProperties(Integer parentHash, String pName, String pValue) {
		GraphElementDTO parentNode = cache.get(parentHash.toString());
		
		if (parentNode == null) {
			log.info("Parent node not registered, paarentHash: "+ parentHash);
		}
		else {
			parentNode.getProperties().put(pName, pValue);
		}
	}
}
