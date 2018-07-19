package okr.mapping.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import okr.neo4j.repository.BaseNode;

public class ExpressionBasedMapper implements GraphMapper {

	private Class<? extends BaseNode> nodeClazz;
	
	public ExpressionBasedMapper(Class<? extends BaseNode> nodeClazz) {
		super();
		this.nodeClazz = nodeClazz;
	}
	
	/*
	 * Mapping done by simple extraction of properties from a given
	 * object using SpEL. 
	 * 
	 * TODO SPI for rule based initialization and property mapping
	 * TODO move out label and property mapping to a base class, object properties is decoration
	 */
	@Override
	public <S extends BaseNode> Collection<S> map(Iterable<?> sourceList, GraphSchema gSchema) {
		Collection<S> res = new ArrayList<>();
		for (Object source : sourceList) {
			S node = (S) new BaseNode().createInstance(nodeClazz);
			
			StandardEvaluationContext elCtx = new StandardEvaluationContext(source);
			
			Collection<String> a = SpelUtils.applySpel(gSchema.getLables(), elCtx, source.toString()).values();
			node.getLabels().addAll(a);
			
			Map b = SpelUtils.applySpel(gSchema.getProperties(), elCtx, source.toString());
			node.getProperties().putAll(b);
			
			res.add(node);
		}
		
		return res;
	}

}
