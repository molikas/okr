package okr.mapping.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.springframework.data.neo4j.repository.support.GraphEntityInformation;

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
	
	public InstanceGraph iGraph = new InstanceGraph();
	
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
	public Collection<BaseNode> map(DocumentGraph document, SchemaGraph schema) {
		Collection<BaseNode> res = new ArrayList<>();
		
		BreadthFirstIterator<String, DefaultEdge> sItr = new BreadthFirstIterator<>(schema.graph);
		// TODO investigate whether traversal listener is a good option here for init case
		ArrayList<String> edgeList = new ArrayList<>();
		while (sItr.hasNext()) {
			String sElemId = sItr.next();
			if (schema.cache.get(sElemId) instanceof NodeSchema) {
				Map<String, BaseNode> matchedNodes = initNodesUsingSchema(document, (NodeSchema) schema.cache.get(sElemId));
				res.addAll(matchedNodes.values());
			}else {
				edgeList.add(sElemId);
			}
		}
		
		structuralEdgeMapping(document, schema);
		
		return res;
	}
	
	// breadth first mapping
	private void structuralEdgeMapping(DocumentGraph document, SchemaGraph schema) {
		for (Entry<String, SchemaElement> entry : schema.cache.entrySet()) {
			if (entry.getValue() instanceof NodeSchema) continue;
			EdgeSchema edge = (EdgeSchema) entry.getValue();
			
			bindNodes(edge.getFrom(), edge.getTo());
			iGraph.graph.containsVertex(entry.getKey()); // iGraph i graph of instances has no track back to schema object
		}
		
	}

	// TODO Make more efficient, by doing propeer caching during node parsing.
	private void bindNodes(String from, String to) {
		Collection<BaseNode> vals = iGraph.cache.values();
		List<BaseNode> fromNode = new ArrayList<>(), toNode = new ArrayList<>();
		
		for (BaseNode baseNode : vals) {
			if (baseNode.getSchemaRef().equals(from)) {
				fromNode.add(baseNode);
			}
			if (baseNode.getSchemaRef().equals(to)) {
				toNode.add(baseNode); 
			}
		}
		if (fromNode == null || toNode == null) {
			return;
		}
		
		for (BaseNode fNode : fromNode) {
			for (BaseNode tNode : toNode) {
				iGraph.graph.addEdge(fNode.getUuid(), tNode.getUuid());
				log.info("Edge created for nodes: "+fNode.getUuid() + " to node "+ tNode.getUuid());
			}
		}

		
		// Find all nodes initialised by qa_node_schema
		// Find all node initialised by wso_node_schema
		// ignore same context SPI == true
		// get instance UUID and connect from ->to
	}

	private Map<String, BaseNode> initNodesUsingSchema(DocumentGraph document, NodeSchema schemaNode) {
		Map<String, BaseNode> results = new HashMap<String, BaseNode>();
		DepthFirstIterator<String, DefaultEdge> dItr = new DepthFirstIterator<>(document.graph);
		while (dItr.hasNext()) {
			GraphElementDTO nodeDTO = document.cache.get(dItr.next());
			BaseNode node = schemaNode.init(nodeDTO);
			if (null != node) {
				results.put(node.getUuid(), node);
				iGraph.graph.addVertex(node.getUuid());
				iGraph.cache.put(node.getUuid(), node);
			}
		}
		
		log.info("Mapping node: "+ schemaNode.getId());
		return results;
	}

}
