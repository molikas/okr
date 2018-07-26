package okr.mapping.schema;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Designated to hold runtime data, to be used in mapping
 * 
 * @author isidenica
 * @param <T> - cached data object
 */
public abstract class GraphHolder<T> {

	protected JsonNode jsonTree;
	
	public final Map<String, T> cache = new LinkedHashMap<>();
	
	/**
	 * Called during {@link GraphHolder} instantiation.
	 * Hook to initialize the context for a given document.
	 */
	public abstract void init();
	
}
