package okr.mapping.schema;

import java.io.IOException;

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
public class LocalJsonRepository extends LocalRepository implements SchemaRepository, DocumentRepository {

	@Override
	public SchemaInstance retrieveSchema(String schemaName) {
		JsonNode jsonNode = parseJson(readLocalFile(SCHEMAS_PATH+schemaName));
        SchemaInstance schema = new SchemaInstance(jsonNode);
        return (schema);
	}

	@Override
	public DocumentInstance retrieveDocument(String documentName) {
		JsonNode jsonNode = parseJson(readLocalFile(DOCUMENTS_PATH+documentName));
		DocumentInstance dGraph = new DocumentInstance(jsonNode);
		return dGraph;
	}
	
	private JsonNode parseJson(String json) {
		try {
			return new ObjectMapper().readTree(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
}
