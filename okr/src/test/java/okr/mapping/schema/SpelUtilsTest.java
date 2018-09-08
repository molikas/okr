package okr.mapping.schema;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import okr.neo4j.repository.BaseNode;

/**
 * Class to test SpelUtils in isolation
 * @author isidenica
 *
 */
public class SpelUtilsTest {

	/**
	 * Happy path validation
	 */
	@Test
	public void logicValidationSpels() {
		BaseNode node = new BaseNode("name");
		Map<String, String> properties = new HashMap<>();
		properties.put("name", "John");
		node.setProperties(properties);

		String goodSpel = "properties[name] == 'John'";
		String badSpel = "properties[name] == 'Betty'";
		String nullSpel = "none_existing_property[name] == 'John'";
		
		Boolean goodResult = SpelUtils.evaluate(goodSpel, node, node.getUuid());
		Boolean badResult = SpelUtils.evaluate(badSpel, node, node.getUuid());
		Boolean nullResult = SpelUtils.evaluate(nullSpel, node, node.getUuid());
		
		assertTrue("String comaprison did not go well: ", goodResult);
		assertFalse("String comaprison did not go well: ", badResult);
		assertNull("False was not returned given an invalid SpEL", nullResult);
	}

	

}
