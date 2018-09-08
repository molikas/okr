package okr.mapping.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.fasterxml.jackson.databind.JsonNode;

import okr.neo4j.repository.BaseNode;

/**
 * Document to schema mapping and initialization class.
 * Mapping is done using SpEL expressions as a default format.
 * 
 * @author isidenica
 *
 */
public class BaseGraphMapper implements GraphMapper {
	
	private Class<? extends BaseNode> nodeClazz;
	
	/// ---------
	public BaseGraphMapper(Class<? extends BaseNode> nodeClazz) {
		super();
		this.nodeClazz = nodeClazz;
	}
	
	/*
	 * Mapping done by simple extraction of properties from a given
	 * object using SpEL. 
	 * 
	 * TODO SPI for rule based initialization and property mapping
	 * TODO move out label and property mapping to a base class, object properties is decoration
	 */
	@Override
	public <S extends BaseNode> Collection<S> map(Iterable<?> sourceList, GraphSchema schema) {
		Collection<S> res = new ArrayList<>();
		
		Iterator<NodeSchema> sItr = schema.nodes.values().iterator();
		while (sItr.hasNext()) {
			NodeSchema schemaNode = sItr.next();
			for (Object source : sourceList) {
				
				Collection<String> labels = SpelUtils.extractValues(new String[] {schemaNode.getLabel()}, source, source.toString()).values();
				Map<String, String> properties = SpelUtils.extractValues(schemaNode.getExtractFields(), source, source.toString());
				
				S node = (S) new BaseNode().createInstance(nodeClazz);
				node.getLabels().addAll(labels);
				node.getProperties().putAll(properties);
				res.add(node);
			}
		}
		
		return res;
	}

	public Graph<BaseNode, DefaultWeightedEdge> map(JsonNode document, GraphSchema schema) {
		return schema.buildGraph(document);
	}


}
