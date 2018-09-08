package okr.mapping.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;

import okr.neo4j.repository.BaseNode;

public class ClosestContextStrategy implements ContextResolutionStrategy {

	public Collection<Pair<BaseNode, BaseNode>> connectNodes(List<BaseNode> fromNodes, List<BaseNode> toNodes, Graph<BaseNode, DefaultWeightedEdge> documentGraph ) {
		FloydWarshallShortestPaths<BaseNode, DefaultWeightedEdge> paths = new FloydWarshallShortestPaths<>(documentGraph);
		
		List<Pair<BaseNode, BaseNode>> result = new ArrayList<>();
		for (BaseNode fromNode: fromNodes){
			for (BaseNode toNode : findClosestNodes(fromNode, toNodes, paths)) {
				Pair<BaseNode, BaseNode> pair = new Pair<>(fromNode, toNode);
				result.add(pair);
			}
		}
		return result;
	}

	// Finds the closest nodes from a given list
	private List<BaseNode> findClosestNodes(BaseNode fromNode, List<BaseNode> toNodes,
			FloydWarshallShortestPaths<BaseNode, DefaultWeightedEdge> paths) {
		
		double shortestPath = 0;
		List<BaseNode> closestNodes = new ArrayList<>();

		for (BaseNode toNode : toNodes) {
			GraphPath<BaseNode, DefaultWeightedEdge> path = paths.getPath(fromNode, toNode);
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
