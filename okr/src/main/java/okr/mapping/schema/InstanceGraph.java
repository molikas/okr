package okr.mapping.schema;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import com.fasterxml.jackson.databind.JsonNode;

import okr.neo4j.repository.BaseNode;

public class InstanceGraph extends GraphHolder<BaseNode>{

	public final Graph<String, DefaultEdge> graph = new DirectedMultigraph<>(DefaultEdge.class);
	
	@Override
	protected void init(JsonNode rootObj) {
	}
	
	
}
