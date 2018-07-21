package okr.mapping.schema;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO to transfer generic node data
 * 
 * @author isidenica
 *
 */
public class NodeDTO {
	
	private String id;
	
	private String name;
	
	private Map<String, String> properties = new HashMap<>();
	
	public NodeDTO (String id, String name){
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
