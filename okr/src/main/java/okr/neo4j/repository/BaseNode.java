package okr.neo4j.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Labels;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Properties;
import org.neo4j.ogm.annotation.Transient;

import okr.etl.JiraDataExtractor;

/**
 * Common persistent node class
 * @author isidenica
 */
@NodeEntity
public class BaseNode {
	
	@Transient
	private static final Logger log = Logger.getLogger(JiraDataExtractor.class.getName());
	
	@Id @GeneratedValue private Long id;

	private String uuid;
	
	private String name;
	
	private String schemaId;
	
	@Labels
	private List<String> labels = new ArrayList<>();
	
	@Properties
	private Map<String, String> properties = new HashMap<>();
	
	public BaseNode() { 
		this.uuid = UUID.randomUUID().toString();
	}
	
	public BaseNode(String name) {
		this();
		this.name = name;
	}	
	
	public <T extends BaseNode> T createInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage());
		}
		return null;
	}	
	
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof BaseNode)) {
            return false;
        }
        BaseNode node = (BaseNode) o;
        return this.uuid == node.getUuid();
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
	
	public boolean isNodeName(String nodeName) {
		return StringUtils.equals(name, nodeName) && name !=null;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}
}
