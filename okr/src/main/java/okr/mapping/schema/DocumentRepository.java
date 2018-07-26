package okr.mapping.schema;

/**
 * Provides access to the document source.
 * 
 * @author isidenica
 *
 */
public interface DocumentRepository {

	/**
	 * Retrieves documents for a given unique name
	 * @param documentName - name which uniquely identifies the document
	 * @return initialized DocumentGraph ready for use
	 */
	public DocumentGraph retrieveDocument(String documentName);
	
}
