package okr.mapping.schema;

/**
 * Implementing classes should be able to retrieve
 * the schemas by name.
 * 
 * @author isidenica
 *
 */
public interface SchemaRepository {

	public SchemaGraph retrieveSchema(String schemaName);
}
