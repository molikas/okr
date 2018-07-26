package okr.mapping.schema;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * DTO to transfer generic graph element data
 * 
 * @author isidenica
 *
 */
public class GraphElementDTO {
	
	private String id;
	
	private String name;
	
	private String parentId;
	
	private String schemaRef;
	
	private Map<String, String> properties = new HashMap<>();
	
	public GraphElementDTO (String id, String name, String parentId){
		this.id = id;
		this.name = name;
		this.parentId = parentId;
	}
	
	public boolean isNodeName(String nodeName) {
		return StringUtils.equals(name, nodeName);
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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getSchemaRef() {
		return schemaRef;
	}

	public void setSchemaRef(String schemaRef) {
		this.schemaRef = schemaRef;
	}
	
	
}
