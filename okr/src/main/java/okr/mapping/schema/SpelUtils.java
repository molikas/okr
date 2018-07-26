package okr.mapping.schema;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Utility class to remove some boilerplate code used to evaluate SpEL expressions.
 * 
 * @author isidenica
 */
public class SpelUtils {

	private static final Logger log = Logger.getLogger(SpelUtils.class.getName());

	/**
	 * Apply a list of SpEL to a given context. Missing fields will be silently
	 * skipped.
	 * 
	 * @param expressionSet - array of SpEL expressions for value extraction
	 * @param rootObject - object used to extract data from
	 * @param objId - id used for tracking
	 * @return - map of extracted values, empty map in case no value were extracted
	 */
	public static Map<String, String> extractValues(String[] expressionSet, Object rootObject, String objId) {
		StandardEvaluationContext eCtx = new StandardEvaluationContext(rootObject);
		SpelExpressionParser spelParser = new SpelExpressionParser();
		Map<String, String> result = new HashMap<>();

		for (String fName : expressionSet) {
			try {
				Expression exp = spelParser.parseExpression(fName);
				Object value = exp.getValue(eCtx);
				if (value != null) {
					result.put(fName, value.toString());
				} else {
					log.info("Failed to extract field: " + fName + " from object: " + objId);
				}
			} catch (SpelEvaluationException e) {
				log.info("Failed to extract field: " + fName + " from object: " + objId);
			}
		}

		return result;
	}

	/**
	 * Helper method to evaluate logical expressions
	 * 
	 * @param expression - SpEL expression which results in a boolean value
	 * @param rootObject - object used for evaluation
	 * @param objId - id used for tracking
	 * @return true/false based on evaluation results, null if unable to parse
	 */
	public static Boolean evaluate(String expression, Object rootObject, String objId) {
		StandardEvaluationContext eCtx = new StandardEvaluationContext(rootObject);
		SpelExpressionParser spel = new SpelExpressionParser();

		try {
			Expression exp = spel.parseExpression(expression);
			return exp.getValue(eCtx, Boolean.class);
		} catch (SpelEvaluationException e) {
			log.info("Failed to evaluate logical expression: " + expression + " using object: " + objId);
		}

		return null;
	}

	public static String extractValue(String expression, GraphElementDTO rootObject, String objId) {
		Map<String, String> vMap = extractValues(new String[] {expression}, rootObject, rootObject.getUuidString());
		if (!vMap.isEmpty() && vMap.size() == 1) {
			return vMap.values().iterator().next();
		}else {
			log.info("Multiple evaluation results when extracting single value from object: "+objId);
		}
		
		return "";
	}

}
