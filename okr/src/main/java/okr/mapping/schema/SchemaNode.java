package okr.mapping.schema;

import java.util.logging.Logger;

/**
 * DTO for holding schema related data
 * 
 * @author isidenica
 */
public class SchemaNode extends SchemaElement {

	private static final Logger log = Logger.getLogger(SchemaNode.class.getName());
	
	private String uniqueness;
	private String group;
	
	public SchemaNode(String id, String label, String uniqueness, String group, String qualifier, String[] extractFields) {
		super();
		super.id = id;
		super.label = label;
		this.uniqueness = uniqueness;
		this.group = group;
		super.extractFields = extractFields;
	}	

	public void init(GraphElementDTO nodeDTO) {
		if (!isQualified(nodeDTO)) {
			return;
		}
		
		log.info("Initializing node "+nodeDTO.getName());
	}	
	
	@Override
	public boolean isQualified(GraphElementDTO nodeDTO) {
		Boolean qualified = SpelUtils.evaluate(qualifier, nodeDTO, nodeDTO.getId());
		
		// Assumes single qualifier expression is provided
		if (qualified != null) {
			return qualified;
		}

		log.info("Node \""+nodeDTO.getName()+"\" not qualified fo schema qualifier: "+ qualifier);
		
		return false;
	} 	
	
	// Getters - Setters
	public String getUniqueness() {
		return uniqueness;
	}
	public void setUniqueness(String uniqueness) {
		this.uniqueness = uniqueness;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}

}
