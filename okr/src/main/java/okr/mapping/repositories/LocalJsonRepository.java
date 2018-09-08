package okr.mapping.repositories;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okr.mapping.schema.GraphSchema;
import okr.mapping.schema.SchemaConverter;

/**
 * Local repository implementation taking files from
 * "schema/"+name path see {@link importPattern}
 * 
 * @author isidenica
 *
 */
@Component
public class LocalJsonRepository extends LocalRepository implements SchemaRepository, DocumentRepository {

	private static final Logger log = Logger.getLogger(LocalJsonRepository.class.getName());
	
	@Override
	public GraphSchema retrieveSchema(String schemaName) {
		JsonNode jsonNode = parseJson(readLocalFile(SCHEMAS_PATH+schemaName));
        GraphSchema schema = SchemaConverter.fromJson(jsonNode);
        return (schema);
	}

	@Override
	public JsonNode retrieveDocument(String documentName) {
		return parseJson(readLocalFile(DOCUMENTS_PATH+documentName));
	}
	
	private JsonNode parseJson(String json) {
		try {
			return new ObjectMapper().readTree(json);
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage());
		}
		return null;
	}

	
}
