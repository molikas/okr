package okr.mapping.schema;

/**
 * Retrieves documents for a given unique name
 * @author isidenica
 *
 */
public interface DocumentRepository {

	public DocumentGraph retrieveDocument(String documentName);
	
}
