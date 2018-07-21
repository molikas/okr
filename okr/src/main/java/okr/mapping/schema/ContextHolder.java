package okr.mapping.schema;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Designated to hold runtime data, to be used in mapping
 * 
 * @author isidenica
 * @param <T> - cached data object
 */
public abstract class ContextHolder<T> {

	// TODO: Generic for custom typed graph.
	public Graph<String, DefaultEdge> graph = new DirectedMultigraph<>(DefaultEdge.class);
	public Map<String, T> cache = new LinkedHashMap<>();
	
	/* Constructuors */
	protected ContextHolder(){}
	
	public ContextHolder(JsonNode rootObj) {
		super();
		init(rootObj);
	}
	/* --------- */
	
	/**
	 * Called during {@link ContextHolder} instantiation.
	 * Hook to initialize the context for a given document.
	 * @param rootObj - root json object for a document.
	 */
	protected abstract void init(JsonNode rootObj);
	
}
