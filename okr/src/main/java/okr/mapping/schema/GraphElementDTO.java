package okr.mapping.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * DTO to transfer generic graph element data
 * 
 * @author isidenica
 *
 */
public class GraphElementDTO {
	
	private String name;
	
	private Map<String, String> properties = new HashMap<>();

	private int uuid;
	
	public GraphElementDTO (int uuid, String name){
		this.name = name;
		this.uuid = uuid;
	}
	
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof GraphElementDTO)) {
            return false;
        }
        GraphElementDTO dto = (GraphElementDTO) o;
        return this.uuid == dto.getUuid();
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
	
	public boolean isNodeName(String nodeName) {
		return StringUtils.equals(name, nodeName);
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

	public int getUuid() {
		return uuid;
	}
	
	public String getUuidString() {
		return Integer.toString(uuid);
	}

	public void setUuid(int uuid) {
		this.uuid = uuid;
	}

	
}
