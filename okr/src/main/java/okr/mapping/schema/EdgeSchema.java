package okr.mapping.schema;

import okr.neo4j.repository.BaseNode;

/**
 * DTO for holding schema related data
 * 
 * @author isidenica
 */
public class EdgeSchema extends SchemaElement {

	private String from;
	private String to;
	private String relationshipType;

	public EdgeSchema(String id, String from, String to, String relationshipType, String[] extractFields) {
		super();
		super.id = id;
		this.from = from;
		this.to = to;
		super.displayValue = relationshipType;
		this.relationshipType = relationshipType;
		super.extractFields = extractFields;
	}
	
	@Override
	public boolean isQualified(BaseNode element) {
		return true;
	}	
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

}
