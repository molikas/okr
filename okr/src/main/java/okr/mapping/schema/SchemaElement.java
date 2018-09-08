package okr.mapping.schema;

import okr.neo4j.repository.BaseNode;

/**
 * Marker interface to aggregate objects
 * used in defining schema elements
 * @author isidenica
 */
public abstract class SchemaElement {

	protected String id;
	protected String displayValue;
	protected String qualifier;
	protected String[] extractFields;
	
	public abstract boolean isQualified(BaseNode element);

	// Getters - Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

}
