package okr.mapping.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.springframework.util.CollectionUtils;

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
	
	public Map<String, String> uuidToHash = new HashMap<>();
	
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
	public Collection<BaseNode> mapDocumentUsingSchema(DocumentGraph document, SchemaGraph schema) {
		Collection<BaseNode> res = new ArrayList<>();
		
		BreadthFirstIterator<String, DefaultWeightedEdge> sItr = new BreadthFirstIterator<>(schema.graph);
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
		//good until this point
		structuralEdgeMapping(document, schema);
		return res;
	}
	
	// breadth first mapping
	public void structuralEdgeMapping(DocumentGraph document, SchemaGraph schema) {
		FloydWarshallShortestPaths paths = new FloydWarshallShortestPaths<>(document.documentGraph);
		for (Entry<String, SchemaElement> entry : schema.cache.entrySet()) {
			if (entry.getValue() instanceof NodeSchema) continue;
			EdgeSchema edgeSchema = (EdgeSchema) entry.getValue();
			List<GraphElementDTO> fromNodes = document.qualifiedNodesCache.get(edgeSchema.getFrom());
			List<GraphElementDTO> toNodes = document.qualifiedNodesCache.get(edgeSchema.getTo());
			
			if (CollectionUtils.isEmpty(fromNodes) || CollectionUtils.isEmpty(toNodes)) continue;
			
			for (GraphElementDTO fromNode: fromNodes){
				double shortestPath = 0;
				for (GraphElementDTO toNode : toNodes) {
					GraphPath<String, DefaultWeightedEdge> path = paths.getPath(fromNode.getId(), toNode.getId());
					if ((shortestPath > path.getWeight() && path.getWeight() != 0) || shortestPath == 0) {
						shortestPath = path.getWeight();
					}
				}
				if (shortestPath != 0) {
					for (GraphElementDTO toNode : toNodes) {
						GraphPath<String, DefaultWeightedEdge> path = paths.getPath(fromNode.getId(), toNode.getId());
						if (path.getWeight() == shortestPath) {
							document.qualifiedNodesGraph.addEdge(fromNode.getId(), toNode.getId());
							iGraph.graph.addEdge(uuidToHash.get(fromNode.getId()), uuidToHash.get(toNode.getId()));
						}
					}
				}
			}
		}
		log.info("");
	}

	// TODO Make more efficient, by doing proper caching during node parsing.
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
				log.info("Edge created for nodes: " + fNode.getUuid() + " to node "+ tNode.getUuid());
			}
		}
		
		// Find all nodes initialised by qa_node_schema
		// Find all node initialised by wso_node_schema
		// ignore same context SPI == true
		// get instance UUID and connect from ->to
	}

	private Map<String, BaseNode> initNodesUsingSchema(DocumentGraph document, NodeSchema schemaNode) {
		Map<String, BaseNode> results = new HashMap<String, BaseNode>();
		DepthFirstIterator<String, DefaultWeightedEdge> dItr = new DepthFirstIterator<>(document.documentGraph);
		while (dItr.hasNext()) {
			GraphElementDTO nodeDTO = document.cache.get(dItr.next());

			BaseNode node = schemaNode.init(nodeDTO);
			if (null != node) {
				
				List<GraphElementDTO> list = document.qualifiedNodesCache.get(schemaNode.getId());
				if (list == null) list = new ArrayList<>();
				list.add(nodeDTO);
				document.qualifiedNodesCache.put(schemaNode.getId(), list);
				document.qualifiedNodesGraph.addVertex(nodeDTO.getId());
				uuidToHash.put(nodeDTO.getId(), node.getUuid());
				
				results.put(node.getUuid(), node);
				iGraph.graph.addVertex(node.getUuid());
				iGraph.cache.put(node.getUuid(), node);
			}
		}
		
		log.info("Mapping node: "+ schemaNode.getId());
		return results;
	}

}
