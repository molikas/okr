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
	
	public Map<String, List<GraphElementDTO>> qualifiedNodesCache = new HashMap<>();
	
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
	public <S extends BaseNode> Collection<S> mapNodes(Iterable<?> sourceList, SchemaGraph schema) {
		Collection<S> res = new ArrayList<>();
		
		BreadthFirstIterator<String, DefaultWeightedEdge> sItr = new BreadthFirstIterator<>(schema.graph);
		while (sItr.hasNext()) {
			NodeSchema schemaNode = (NodeSchema)schema.cache.get(sItr.next());
			for (Object source : sourceList) {
				S node = (S) new BaseNode().createInstance(nodeClazz);
				Collection<String> labels = SpelUtils.extractValues(new String[] {schemaNode.getLabel()}, source, source.toString()).values();
				Map<String, String> properties = SpelUtils.extractValues(schemaNode.getExtractFields(), source, source.toString());
				node.getLabels().addAll(labels);
				node.getProperties().putAll(properties);
				res.add(node);
			}
		}
		
		return res;
	}

	@Override
	public void mapDocumentUsingSchema(DocumentGraph document, SchemaGraph schema) {
		initAllNodes(document, schema);
		structuralEdgeMapping(document, schema);
	}

	/*
	 * Walks through the provided schema trying to initialize the nodes
	 */
	private void initAllNodes(DocumentGraph document, SchemaGraph schema) {
		BreadthFirstIterator<String, DefaultWeightedEdge> sItr = new BreadthFirstIterator<>(schema.graph);
		while (sItr.hasNext()) {
			String sElemId = sItr.next();
			if (schema.cache.get(sElemId) instanceof NodeSchema) {
				initNodesUsingSchema(document, (NodeSchema) schema.cache.get(sElemId));
			}
		}
	}
	
	/*
	 * Initialize all the node that are qualified by the given schema.
	 * Cache the qualified nodes using schemaId->nodeDTO mapping
	 */
	private void initNodesUsingSchema(DocumentGraph document, NodeSchema schemaNode) {
		DepthFirstIterator<String, DefaultWeightedEdge> dItr = new DepthFirstIterator<>(document.documentGraph);
		while (dItr.hasNext()) {
			GraphElementDTO nodeDTO = document.cache.get(dItr.next());

			BaseNode node = schemaNode.init(nodeDTO);
			if (null == node) continue; 
			
			addToQualifiedNodes(schemaNode.getId(), node, nodeDTO);
			
			iGraph.graph.addVertex(node.getUuid());
			iGraph.cache.put(node.getUuid(), node);
		}
		log.info("Mapping node: "+ schemaNode.getId());
	}
	
	private void addToQualifiedNodes(String schemaId, BaseNode node, GraphElementDTO nodeDTO) {
		List<GraphElementDTO> list = qualifiedNodesCache.get(schemaId);
		list = (list == null)? new ArrayList<>(): list;
		list.add(nodeDTO);
		
		qualifiedNodesCache.put(schemaId, list);
		uuidToHash.put(nodeDTO.getId(), node.getUuid());
	}

	/*
	 * Use edge schema to link together two nodes, that were initialized
	 * according to the fromScehmaNode->toScehamNode. 
	 */
	public void structuralEdgeMapping(DocumentGraph document, SchemaGraph schema) {
		FloydWarshallShortestPaths<String, DefaultWeightedEdge> paths = new FloydWarshallShortestPaths<>(document.documentGraph);
		
		for (Entry<String, SchemaElement> entry : schema.cache.entrySet()) {
			if (entry.getValue() instanceof NodeSchema) continue;
			
			EdgeSchema edgeSchema = (EdgeSchema) entry.getValue();
			List<GraphElementDTO> fromNodes = qualifiedNodesCache.get(edgeSchema.getFrom());
			List<GraphElementDTO> toNodes = qualifiedNodesCache.get(edgeSchema.getTo());
			
			// if from or to node instances are missing -> skip
			if (CollectionUtils.isEmpty(fromNodes) || CollectionUtils.isEmpty(toNodes)) continue;
			
			//TODO really should use reducers
			for (GraphElementDTO fromNode: fromNodes){
				for (GraphElementDTO toNode : findClosestNodes(fromNode, toNodes, paths)) {
					iGraph.graph.addEdge(uuidToHash.get(fromNode.getId()), uuidToHash.get(toNode.getId()));
				}
				
			}
		}
	}

	// Finds the closest nodes from a given list
	private List<GraphElementDTO> findClosestNodes(GraphElementDTO fromNode, List<GraphElementDTO> toNodes,
			FloydWarshallShortestPaths<String, DefaultWeightedEdge> paths) {
		
		double shortestPath = 0;
		List<GraphElementDTO> closestNodes = new ArrayList<>();

		for (GraphElementDTO toNode : toNodes) {
			GraphPath<String, DefaultWeightedEdge> path = paths.getPath(fromNode.getId(), toNode.getId());
			// new closest found
			if ((shortestPath > path.getWeight() && path.getWeight() != 0) || shortestPath == 0) {
				shortestPath = path.getWeight();
				closestNodes.clear();
			}
			// add closest to result list
			if (shortestPath != 0 && shortestPath == path.getWeight()) {
				closestNodes.add(toNode);
			}
		}
		
		return closestNodes;
	}

}
