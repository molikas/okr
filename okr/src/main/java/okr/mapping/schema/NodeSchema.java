package okr.mapping.schema;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import okr.neo4j.repository.BaseNode;

/**
 * DTO for holding schema related data
 * 
 * @author isidenica
 */
public class NodeSchema extends SchemaElement {

	private static final Logger log = Logger.getLogger(NodeSchema.class.getName());
	
	protected String label;
	private String uniqueness;
	
	public NodeSchema(String id, String displayValue, String uniqueness, String label, String qualifier, String[] extractFields) {
		super();
		super.id = id;
		super.displayValue = displayValue;
		this.uniqueness = uniqueness;
		this.label = label;
		super.extractFields = extractFields;
		super.qualifier = qualifier;
	}	

	public BaseNode apply(BaseNode nodeDTO) {
		if (!isQualified(nodeDTO)) {
			return null;
		}
		BaseNode node = new BaseNode();
		
		node.getLabels().add(this.label); // need to rewrite the set UUID part
		node.setUuid(SpelUtils.extractValue(uniqueness, nodeDTO, nodeDTO.getUuid()));
		node.setSchemaId(id);
		
		Map<String, String> extraProps = SpelUtils.extractValues(extractFields, nodeDTO, nodeDTO.getUuid());
		node.getProperties().putAll(extraProps);
		
		log.log(Level.INFO, "Initializing node {0}", nodeDTO.getName());
		return node;
	}	
	
	@Override
	public boolean isQualified(BaseNode node) {
		Boolean qualified = SpelUtils.evaluate(qualifier, node, node.getUuid());
		if (qualified != null) {
			return qualified;
		}

		log.log(Level.INFO, "Node {0} not qualified fo schema qualifier: {1} ", new Object[] {node.getName(), qualifier});
		
		return false;
	} 	
	
	// Getters - Setters
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getUniqueness() {
		return uniqueness;
	}
	public void setUniqueness(String uniqueness) {
		this.uniqueness = uniqueness;
	}
}
