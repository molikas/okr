package okr.neo4j.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Labels;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Properties;
import org.neo4j.ogm.annotation.Transient;

/**
 * Common persistent node class
 * @author isidenica
 */
@NodeEntity
public class BaseNode {
	
	@Id @GeneratedValue private Long id;

	private String uuid;
	
	@Labels
	private List<String> labels = new ArrayList<>();
	
	@Properties
	private Map<String, String> properties = new HashMap<>();
	
	@Transient
	private String schemaRef;

	public BaseNode() { 
		this.uuid = UUID.randomUUID().toString();
	}
	
	public <T extends BaseNode> T createInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	// Getters - Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}	
	
	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getSchemaRef() {
		return schemaRef;
	}

	public void setSchemaRef(String schemaRef) {
		this.schemaRef = schemaRef;
	}

}
