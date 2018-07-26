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
	public SchemaInstance retrieveSchema(String schemaName) {
        SchemaInstance schema = new SchemaInstance(readLocalJson(schemaName));
        return (schema);
	}

	@Override
	public DocumentInstance retrieveDocument(String documentName) {
		DocumentInstance dGraph = new DocumentInstance(readLocalJson(documentName));
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
