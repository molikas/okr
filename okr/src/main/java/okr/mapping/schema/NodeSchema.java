package okr.mapping.schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import okr.neo4j.repository.BaseNode;

/**
 * DTO for holding schema related data
 * 
 * @author isidenica
 */
public class NodeSchema extends SchemaElement {

	private static final Logger log = Logger.getLogger(NodeSchema.class.getName());
	
	protected String label;
	private String uniqueness;
	public List<String> qualifiedUUIDs = new ArrayList<>();
	
	public NodeSchema(String id, String displayValue, String uniqueness, String label, String qualifier, String[] extractFields) {
		super();
		super.id = id;
		super.displayValue = displayValue;
		this.uniqueness = uniqueness;
		this.label = label;
		super.extractFields = extractFields;
		super.qualifier = qualifier;
	}	

	public BaseNode init(GraphElementDTO nodeDTO) {
		if (!isQualified(nodeDTO)) {
			return null;
		}
		BaseNode node = new BaseNode();
		
		node.getLabels().add(this.label);
		node.setUuid(SpelUtils.extractValue(uniqueness, nodeDTO, nodeDTO.getUuidString()));
		
		Map<String, String> extraProps = SpelUtils.extractValues(extractFields, nodeDTO, nodeDTO.getUuidString());
		node.getProperties().putAll(extraProps);
		
		qualifiedUUIDs.add(node.getUuid());
		log.info("Initializing node "+nodeDTO.getName());
		return node;
	}	
	
	@Override
	public boolean isQualified(GraphElementDTO nodeDTO) {
		Boolean qualified = SpelUtils.evaluate(qualifier, nodeDTO, nodeDTO.getUuidString());
		if (qualified != null) {
			return qualified;
		}

		log.info("Node \""+nodeDTO.getName()+"\" not qualified fo schema qualifier: "+ qualifier);
		
		return false;
	} 	
	
	// Getters - Setters
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getUniqueness() {
		return uniqueness;
	}
	public void setUniqueness(String uniqueness) {
		this.uniqueness = uniqueness;
	}
}
