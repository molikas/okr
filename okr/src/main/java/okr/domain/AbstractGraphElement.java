package okr.domain;

import java.util.Map;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Properties;

/**
 * Common node class
 * @author isidenica
*/
@NodeEntity
public class AbstractGraphElement {
	
	@Id @GeneratedValue private Long id;
	
	@Properties
	private Map<String, String> properties;

	// Getter - Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
}
