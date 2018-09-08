package okr.mapping.repositories;

import okr.mapping.schema.GraphSchema;

/**
 * Implementing classes should be able to retrieve
 * the schemas by name.
 * 
 * @author isidenica
 *
 */
public interface SchemaRepository {

	public GraphSchema retrieveSchema(String schemaName);
}
