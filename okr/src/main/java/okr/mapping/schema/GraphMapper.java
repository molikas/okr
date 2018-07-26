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

	@Deprecated
	public <S extends BaseNode> Collection<S> mapNodes(Iterable<?> sourceList, SchemaGraph gSchema);
	
	public void mapDocumentUsingSchema(DocumentGraph document, SchemaGraph schema); 
	
}
