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

	public <S extends BaseNode> Collection<S> map(Iterable<?> sourceList, SchemaGraph gSchema);
	
	public <S extends BaseNode> Collection<S> map(DocumentGraph document, SchemaGraph schema); 
	
}
