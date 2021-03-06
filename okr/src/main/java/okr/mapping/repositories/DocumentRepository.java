package okr.mapping.repositories;

import com.fasterxml.jackson.databind.JsonNode;

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
	public JsonNode retrieveDocument(String documentName);
	
}
