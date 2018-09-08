package okr.mapping.schema;

import java.util.Collection;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.fasterxml.jackson.databind.JsonNode;

import okr.neo4j.repository.BaseNode;

/**
 * Provides generic mapping capabilities,
 * by allowing to map input to a neo4j schema.
 *
 * TODO: Maybe model mapper can be used for more generic mapping of properties. Or map directly from JSON
 * @author isidenica
 * @return 
 *
 */
public interface GraphMapper {

	/**	
	 * Maps a set of objects using a given schema.  
	 * 
	 * @param sourceList - list of objects to be used for conversion to node
	 * @param gSchema - schema to use for mapping
	 * @return list of persistent nodes
	 */
	public <S extends BaseNode> Collection<S> map(Iterable<?> sourceList, GraphSchema gSchema);
	
	/**
	 * Maps a document graph to to a given schema.
	 * @param document 
	 * @param schema
	 * @return 
	 */
	public Graph<BaseNode, DefaultWeightedEdge> map(JsonNode document, GraphSchema schema); 
	
}
