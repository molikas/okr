package okr.mapping.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import okr.neo4j.repository.BaseNode;

/**
 * Document to schema mapping and initialization class.
 * Mapping is done using SpEL expressions as a default format.
 * 
 * @author isidenica
 *
 */
public class ExpressionBasedMapper implements GraphMapper {
	
	private static final Logger log = Logger.getLogger(ExpressionBasedMapper.class.getName());

	private Class<? extends BaseNode> nodeClazz;
	
	public ExpressionBasedMapper(Class<? extends BaseNode> nodeClazz) {
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
	public <S extends BaseNode> Collection<S> map(Iterable<?> sourceList, SchemaGraph gSchema) {
		Collection<S> res = new ArrayList<>();
		for (Object source : sourceList) {
			S node = (S) new BaseNode().createInstance(nodeClazz);
			
			Collection<String> labels = SpelUtils.extractValues(gSchema.getLables(), source, source.toString()).values();
			Map<String, String> properties = SpelUtils.extractValues(gSchema.getProperties(), source, source.toString());
			
			node.getLabels().addAll(labels);
			node.getProperties().putAll(properties);
			
			res.add(node);
		}
		
		return res;
	}

	@Override
	public <S extends BaseNode> Collection<S> map(DocumentGraph document, SchemaGraph schema) {
		Collection<S> res = new ArrayList<>();
		
		BreadthFirstIterator<String, DefaultEdge> sItr = new BreadthFirstIterator<>(schema.graph);
		// TODO investigate whether traversal listener is a good option here for init case
		ArrayList<String> edgeList = new ArrayList<>();
		while (sItr.hasNext()) {
			String elemId = sItr.next();
			if (schema.cache.get(elemId) instanceof SchemaNode) {
				matchObjectsThatMatchCriteria(document, (SchemaNode) schema.cache.get(elemId));	
			}else {
				edgeList.add(elemId);
			}
		}
		
		for (String edgeId : edgeList) {
			log.info("Init edge: "+ edgeId);
		}
		
		return res;
	}
	
	private List<String> matchObjectsThatMatchCriteria(DocumentGraph document, SchemaNode schemaNode) {
		List<String> results = new ArrayList<>();
		DepthFirstIterator<String, DefaultEdge> dItr = new DepthFirstIterator<>(document.graph);
		while (dItr.hasNext()) {
			GraphElementDTO nodeDTO = document.cache.get(dItr.next());
			schemaNode.init(nodeDTO);
		}
		
		log.info("Mapping node: "+ schemaNode.getId());
		return results;
	}

}
