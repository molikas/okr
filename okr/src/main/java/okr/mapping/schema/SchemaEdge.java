package okr.mapping.schema;

/**
 * DTO for holding schema related data
 * 
 * @author isidenica
 */
public class SchemaEdge extends SchemaElement {

	private String from;
	private String to;

	public SchemaEdge(String id, String from, String to, String label, String[] extractFields) {
		super();
		super.id = id;
		this.from = from;
		this.to = to;
		super.label = label;
		super.extractFields = extractFields;
	}
	
	@Override
	public boolean isQualified(GraphElementDTO element) {
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

}
