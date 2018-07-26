package okr.mapping.schema;

import java.util.Collection;

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
	 * @param sourceList - list of objects to be used for convesion to node
	 * @param gSchema - schema to use for mapping
	 * @return list of persistent nodes
	 */
	public <S extends BaseNode> Collection<S> mapNodes(Iterable<?> sourceList, SchemaInstance gSchema);
	
	/**
	 * Maps a document graph to to a given schema.
	 * @param document 
	 * @param schema
	 */
	public void mapDocumentUsingSchema(DocumentInstance document, SchemaInstance schema); 
	
}
