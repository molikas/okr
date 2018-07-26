package okr.mapping.schema;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Local repository implementation taking files from
 * "schema/"+name path see {@link importPattern}
 * 
 * @author isidenica
 *
 */
@Component
public class LocalJsonRepository implements SchemaRepository, DocumentRepository {

    private String importPattern = "/schemas/";
    
	@Override
	public SchemaGraph retrieveSchema(String schemaName) {
        SchemaGraph schema = new SchemaGraph(readLocalJson(schemaName));
        schema.init();
        return (schema);
	}

	@Override
	public DocumentGraph retrieveDocument(String documentName) {
		DocumentGraph dGraph = new DocumentGraph(readLocalJson(documentName));
		dGraph.init();
		return dGraph;
	}

	private JsonNode readLocalJson(String name) {
	    try {
	    	InputStream in = getClass().getResourceAsStream(importPattern+name); 
        	String jsonString = IOUtils.toString(in);
			return new ObjectMapper().readTree(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
