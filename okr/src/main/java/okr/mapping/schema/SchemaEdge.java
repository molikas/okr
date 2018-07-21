package okr.mapping.schema;

/**
 * DTO for holding schema related data
 * 
 * @author isidenica
 */
public class SchemaEdge implements SchemaElement {
	private String id;
	private String from;
	private String to;
	private String label;
	private String[] extractFields;
	
	public SchemaEdge(String id, String from, String to, String label, String[] extractFields) {
		super();
		this.id = id;
		this.from = from;
		this.to = to;
		this.label = label;
		this.extractFields = extractFields;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String[] getExtractFields() {
		return extractFields;
	}

	public void setExtractFields(String[] extractFields) {
		this.extractFields = extractFields;
	}

	
	
}
