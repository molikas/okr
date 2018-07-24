package okr.mapping.schema;

/**
 * Marker interface to aggregate objects
 * used in defining schema elements
 * @author isidenica
 */
public abstract class SchemaElement {

	protected String id;
	protected String label;
	protected String qualifier;
	protected String[] extractFields;
	
	public abstract boolean isQualified(GraphElementDTO element);

	// Getters - Setters
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

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public String[] getExtractFields() {
		return extractFields;
	}

	public void setExtractFields(String[] extractFields) {
		this.extractFields = extractFields;
	}

}
