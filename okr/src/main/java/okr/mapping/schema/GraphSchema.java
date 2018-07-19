package okr.mapping.schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds configuration of available graph schema mappers
 * 
 * @author isidenica
 *
 */
public class GraphSchema{

	public static final String NODE_LABELS_KEY = "node-labels";
	public static final String NODE_PROPERTIES_KEY = "node-properties";
	public static final String RELATIONSHIPS_KEY = "graph-relationships";
	
	protected Map<String, String[]> cfg = new HashMap<>();
	
	//Configuration + default mapping if there is no external configuration
	private String[] labels = new String[] {"issueType.name"};
	private String[] properties = new String[] {"key", "summary", "resolution.name", "status.name"};
	
	public GraphSchema() {
		cfg.put(NODE_LABELS_KEY, labels);
		cfg.put(NODE_PROPERTIES_KEY, properties);
	}
	
	public GraphSchema(Map<String, String[]> cfg) {
		this();
		if (cfg != null) this.cfg = cfg;
	}	

	public String[] getLables() {
		return cfg.get(GraphSchema.NODE_LABELS_KEY);
	}

	public String[] getProperties() {
		return cfg.get(GraphSchema.NODE_PROPERTIES_KEY);
	}
	
}
