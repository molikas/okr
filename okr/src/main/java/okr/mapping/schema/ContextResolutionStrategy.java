package okr.mapping.schema;

import java.util.Collection;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;

import okr.neo4j.repository.BaseNode;

public interface ContextResolutionStrategy {

	/**
	 * Implementation returns pairs of nodes that have to be connected. In cases where there are multiple candidates for connection
	 * the user might choose a different context resolution strategy to say which nodes have to be connected.
	 * 
	 * @param fromNodes - nodes that were initialized according to a node schema, the edge originates from these nodes
	 * @param toNodes - nodes that were initialized according to schema, and are targeted for the edge connection 
	 * @param documentGraph - original document that was parsed from json to a graph
	 * @return pairs that indicate which node need to connect to which target.
	 */
	public Collection<Pair<BaseNode, BaseNode>> connectNodes(List<BaseNode> fromNodes, List<BaseNode> toNodes, Graph<BaseNode, DefaultWeightedEdge> documentGraph );

}
