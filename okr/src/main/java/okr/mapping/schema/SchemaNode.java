package okr.mapping.schema;

/**
 * DTO for holding schema related data
 * 
 * @author isidenica
 */
public class SchemaNode implements SchemaElement {
	private String id;
	private String label;
	private String uniqueness;
	private String group;
	private String[] extractFields;
	
	public SchemaNode(String id, String label, String uniqueness, String group, String[] extractFields) {
		super();
		this.id = id;
		this.label = label;
		this.uniqueness = uniqueness;
		this.group = group;
		this.extractFields = extractFields;
	}	
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
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
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String[] getExtractFields() {
		return extractFields;
	}
	public void setExtractFields(String[] extractFields) {
		this.extractFields = extractFields;
	} 
	
}
