package okr.mapping.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;
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
	
	public final Graph<BaseNode, DefaultEdge> mappedGraph = new DirectedMultigraph<>(DefaultEdge.class);
	
	public Map<GraphElementDTO, BaseNode> dtoToBase = new HashMap<>();
	
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
	public <S extends BaseNode> Collection<S> mapNodes(Iterable<?> sourceList, SchemaInstance schema) {
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

	@Override
	public void mapDocumentUsingSchema(DocumentInstance document, SchemaInstance schema) {
		initAllNodes(document, schema);
		structuralEdgeMapping(document, schema);
	}

	/*
	 * Walks through the provided schema trying to initialize the nodes
	 */
	private void initAllNodes(DocumentInstance document, SchemaInstance schema) {
		Iterator<NodeSchema> sItr = schema.nodes.values().iterator();
		while (sItr.hasNext()) {
			initNodesUsingSchema(document, sItr.next());
		}
	}
	
	/*
	 * Initialize all the node that are qualified by the given schema.
	 * Cache the qualified nodes using schemaId->nodeDTO mapping
	 */
	private void initNodesUsingSchema(DocumentInstance document, NodeSchema schemaNode) {
		DepthFirstIterator<GraphElementDTO, DefaultWeightedEdge> dItr = new DepthFirstIterator<>(document.documentGraph);
		while (dItr.hasNext()) {
			GraphElementDTO nodeDTO = dItr.next();

			BaseNode node = schemaNode.init(nodeDTO);
			if (null == node) continue; 
			
			addToQualifiedNodes(schemaNode.getId(), node, nodeDTO);
			
			mappedGraph.addVertex(node);
		}
		log.info("Mapping node: "+ schemaNode.getId());
	}
	
	private void addToQualifiedNodes(String schemaId, BaseNode node, GraphElementDTO nodeDTO) {
		List<GraphElementDTO> list = qualifiedNodesCache.get(schemaId);
		list = (list == null)? new ArrayList<>(): list;
		list.add(nodeDTO);
		
		qualifiedNodesCache.put(schemaId, list);
		dtoToBase.put(nodeDTO, node);
	}

	/*
	 * Use edge schema to link together two nodes, that were initialized
	 * according to the fromScehmaNode->toScehamNode. 
	 */
	public void structuralEdgeMapping(DocumentInstance document, SchemaInstance schema) {
		FloydWarshallShortestPaths<GraphElementDTO, DefaultWeightedEdge> paths = new FloydWarshallShortestPaths<>(document.documentGraph);
		
		for (Entry<String, EdgeSchema> entry : schema.edges.entrySet()) {
			EdgeSchema edgeSchema = (EdgeSchema) entry.getValue();
			List<GraphElementDTO> fromNodes = qualifiedNodesCache.get(edgeSchema.getFrom());
			List<GraphElementDTO> toNodes = qualifiedNodesCache.get(edgeSchema.getTo());
			
			// if from or to node instances are missing -> skip
			if (CollectionUtils.isEmpty(fromNodes) || CollectionUtils.isEmpty(toNodes)) continue;
			
			//TODO really should use reducers
			for (GraphElementDTO fromNode: fromNodes){
				for (GraphElementDTO toNode : findClosestNodes(fromNode, toNodes, paths)) {
					mappedGraph.addEdge(dtoToBase.get(fromNode), dtoToBase.get(toNode));
				}
				
			}
		}
	}

	// Finds the closest nodes from a given list
	private List<GraphElementDTO> findClosestNodes(GraphElementDTO fromNode, List<GraphElementDTO> toNodes,
			FloydWarshallShortestPaths<GraphElementDTO, DefaultWeightedEdge> paths) {
		
		double shortestPath = 0;
		List<GraphElementDTO> closestNodes = new ArrayList<>();

		for (GraphElementDTO toNode : toNodes) {
			GraphPath<GraphElementDTO, DefaultWeightedEdge> path = paths.getPath(fromNode, toNode);
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
