package okr.etl.providers;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.api.domain.Issue;

@Service
public class SpelUtils {
	
	private static final Logger log = Logger.getLogger(SpelUtils.class.getName());
	
	/* ------TODO Move out--------- */
	// move to dynamic configuration and custom mapping-orchestration support
	public static final String[] fieldsToExtract = new String[] {"key", "summary"};
	public static final String[] fieldsForLinking = new String[] {"reporter.displayName"};
	public static final String[] fieldsForProperties = new String[] {"resolution.name", "issueType.name"};
	
	/**
	 * Extracts jira fields from an issue using SpEL.
	 * @param issue
	 * @return
	 */
	public static Map<String, String> extractFieldValue(Issue issue){
		
		Map<String, String> result = new HashMap<>();
		StandardEvaluationContext eCtx = new StandardEvaluationContext(issue);
		
		result.putAll(applySpel(fieldsToExtract, eCtx, issue.getKey()));
		result.putAll(applySpel(fieldsForLinking, eCtx, issue.getKey()));
		result.putAll(applySpel(fieldsForProperties, eCtx, issue.getKey()));
		
		return result;
	}
	/* ------end--------- */
	
	/**
	 * Apply a list of SpEL to a given context. Missing fields will be silently skipped.
	 * @param spelSet
	 * @param eCtx
	 * @return
	 */
	private static Map<String, String> applySpel(String[] spelSet, StandardEvaluationContext eCtx, String identifier) {
		SpelExpressionParser spel = new SpelExpressionParser();
		Map<String, String> result = new HashMap<>();
		
		for (String fName: spelSet) {
			try {
				Expression exp = spel.parseExpression(fName);
				String value = exp.getValue(eCtx).toString();
				result.put(fName, value);
			} catch (SpelEvaluationException e) {
				// TODO abstract to support any source object
				log.info("Failed to map field: "+fName+" of object: "+identifier); 
			}
		}
		
		return result;
	}
	
}
