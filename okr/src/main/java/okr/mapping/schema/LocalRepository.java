package okr.mapping.schema;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * Provides access to locally stored schemas and documents
 * from paths {@link LocalRepository.SCHEMAS_PATH} and {@link LocalRepository.DOCUMENTS_PATH} 
 * 
 * @author isidenica
 *
 */
public class LocalRepository {

	public static final String SCHEMAS_PATH = "/schemas/";
	public static final String DOCUMENTS_PATH = "/documents/";
	
	public String readLocalFile(String path) {
	    try {
	    	return IOUtils.toString(getInputStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public InputStream getInputStream(String path) {
	    return getClass().getResourceAsStream(path);
	}
	
}
