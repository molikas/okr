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

//    @Autowired
//    private ResourceLoader resourceLoader;
	
    private String importPattern = "/schemas/";
    
	@Override
	public SchemaGraph retrieveSchema(String schemaName) {
        SchemaGraph schema = new SchemaGraph();
        schema.init(readLocalJson(schemaName));
        return (schema);
	}

	@Override
	public DocumentGraph retrieveDocument(String documentName) {
		DocumentGraph dGraph = new DocumentGraph();
		dGraph.init(readLocalJson(documentName));
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
//	    Resource[] schResources;
//		try {
//			schResources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(importPattern+name);
//		      ObjectMapper mapper = new ObjectMapper();
//		       
//		      for (Resource schResource: schResources) {
//		          File file = schResource.getFile();
//		          String jsonString = IOUtils.toString(new FileInputStream(file));
//		          
//		          return mapper.readTree(jsonString);
//		      }			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return null;
	}
	
}
