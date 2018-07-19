package okr.mapping.schema;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

@Service
public class SpelUtils {
	
	private static final Logger log = Logger.getLogger(SpelUtils.class.getName());
	
	/**
	 * Apply a list of SpEL to a given context. Missing fields will be silently skipped.
	 * @param spelSet
	 * @param eCtx
	 * @return
	 */
	public static Map<String, String> applySpel(String[] spelSet, StandardEvaluationContext eCtx, String objId) {
		SpelExpressionParser spel = new SpelExpressionParser();
		Map<String, String> result = new HashMap<>();
		
		for (String fName: spelSet) {
			try {
				Expression exp = spel.parseExpression(fName);
				String value = exp.getValue(eCtx).toString();
				result.put(fName, value);
			} catch (SpelEvaluationException e) {
				// TODO abstract to support any source object
				log.info("Failed to map field: "+fName+" of object: "+objId); 
			}
		}
		
		return result;
	}
	
}
