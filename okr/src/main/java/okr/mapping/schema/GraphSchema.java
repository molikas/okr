package okr.mapping.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.WeightedMultigraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;

import okr.neo4j.repository.BaseNode;

/**
 * Holds configuration of available graph schema mappers
 * 
 * @author isidenica
 */
public class GraphSchema {

	protected Map<String, NodeSchema> nodes;
	protected Map<String, EdgeSchema> edges;
	
	public final ContextResolutionStrategy ctxResolutionStr;

	public GraphSchema(Map<String, NodeSchema> mapSchemaNodes, Map<String, EdgeSchema> mapSchemaEdges,
			ContextResolutionStrategy defaultStrategy) {
		this.nodes = mapSchemaNodes;
		this.edges = mapSchemaEdges;
		this.ctxResolutionStr = defaultStrategy;
	}

	public Graph<BaseNode, DefaultWeightedEdge> buildGraph(JsonNode document) {
		Graph<BaseNode, DefaultWeightedEdge> docGraph = applySchema(new GraphConverter().fromJson(document));

		// Add nodes to graph
		Graph<BaseNode, DefaultWeightedEdge> resultGraph = new DirectedMultigraph<>(DefaultWeightedEdge.class);
		GraphUtils.groupBySchemaId(docGraph).values().stream().flatMap(Collection::stream).collect(Collectors.toList())
				.forEach(resultGraph::addVertex);

		// Connect nodes
		for (Pair<BaseNode, BaseNode> pair : connectNodes(docGraph)) {
			resultGraph.addEdge(pair.getFirst(), pair.getSecond());
		}

		return resultGraph;
	}

	public Graph<BaseNode, DefaultWeightedEdge> applySchema(Graph<BaseNode, DefaultWeightedEdge> documentGraph) {

		Map<BaseNode, BaseNode> fromTo = new HashMap<>();

		Iterator<NodeSchema> sItr = nodes.values().iterator();
		List<BaseNode> nodeList = new ArrayList<>();
		while (sItr.hasNext()) {
			NodeSchema nSchema = sItr.next();
			DepthFirstIterator<BaseNode, DefaultWeightedEdge> dItr = new DepthFirstIterator<>(documentGraph);

			while (dItr.hasNext()) {
				BaseNode originalNode = dItr.next();

				BaseNode node = nSchema.apply(originalNode);
				if (null == node)
					continue;

				/// add to results
				nodeList.add(node);
				fromTo.put(originalNode, node);
			}
		}

		// 1. add all qualified nodes
		Graph<BaseNode, DefaultWeightedEdge> resultGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
		List<BaseNode> qNodes = documentGraph.vertexSet().stream().filter(node -> !fromTo.containsKey(node))
				.collect(Collectors.toCollection(() -> nodeList));
		qNodes.forEach(resultGraph::addVertex);

		// 2. add edges and remap the old nodes.
		documentGraph.edgeSet().forEach(edge -> {
			BaseNode from = documentGraph.getEdgeSource(edge);
			BaseNode to = documentGraph.getEdgeTarget(edge);
			if (fromTo.containsKey(from)) from = fromTo.get(from);
			if (fromTo.containsKey(to)) to = fromTo.get(to);
			resultGraph.addEdge(from, to);
		});

		return resultGraph;
	}

	/*
	 * Use edge schema to link together two nodes, that were initialized according
	 * to the fromScehmaNode->toScehamNode.
	 */
	public List<Pair<BaseNode, BaseNode>> connectNodes(Graph<BaseNode, DefaultWeightedEdge> docGraph) {

		List<Pair<BaseNode, BaseNode>> result = new ArrayList<>();

		Map<String, List<BaseNode>> qualifiedNodes = GraphUtils.groupBySchemaId(docGraph);

		for (Entry<String, EdgeSchema> edgeSchemaEntry : edges.entrySet()) {
			EdgeSchema edgeSchema = edgeSchemaEntry.getValue();

			List<BaseNode> fromNodes = qualifiedNodes.get(edgeSchema.getFrom());
			List<BaseNode> toNodes = qualifiedNodes.get(edgeSchema.getTo());

			// if FROM or TO node instances are missing -> skip
			if (CollectionUtils.isEmpty(fromNodes) || CollectionUtils.isEmpty(toNodes))
				continue;

			result.addAll(ctxResolutionStr.connectNodes(fromNodes, toNodes, docGraph));
		}
		return result;
	}

}
