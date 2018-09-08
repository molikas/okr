package okr.mapping.schema;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import okr.neo4j.repository.BaseNode;

/**
 * Utility class to work with a given graph
 * @author isidenica
 *
 */
public class GraphUtils {
	
	
	private GraphUtils() {}
	
	/**
	 * Groups a nodes by the schema id those were initialized
	 * @param graph - document graph containing base nodes initialized according to schemas.
	 * @return map of schemaId->list of nodes initialised by that scehma.
	 */
	public static final Map<String, List<BaseNode>> groupBySchemaId(Graph<BaseNode, DefaultWeightedEdge> graph) {
		return graph.vertexSet().stream().filter(v -> !StringUtils.isEmpty(v.getSchemaId()))
		.collect(Collectors.groupingBy(BaseNode::getSchemaId));
	}
	
}
