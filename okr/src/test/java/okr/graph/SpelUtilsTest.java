package okr.graph;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import okr.mapping.schema.GraphElementDTO;
import okr.mapping.schema.SpelUtils;

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
		GraphElementDTO nodeDto = new GraphElementDTO(1, "name");
		Map<String, String> properties = new HashMap<>();
		properties.put("name", "John");
		nodeDto.setProperties(properties);

		String goodSpel = "properties[name] == 'John'";
		String badSpel = "properties[name] == 'Betty'";
		String nullSpel = "none_existing_property[name] == 'John'";
		
		Boolean goodResult = SpelUtils.evaluate(goodSpel, nodeDto, Integer.toString(nodeDto.getUuid()));
		Boolean badResult = SpelUtils.evaluate(badSpel, nodeDto, Integer.toString(nodeDto.getUuid()));
		Boolean nullResult = SpelUtils.evaluate(nullSpel, nodeDto, Integer.toString(nodeDto.getUuid()));
		
		assertTrue("String comaprison did not go well: ", goodResult);
		assertFalse("String comaprison did not go well: ", badResult);
		assertNull("Null was not returned given an invalid SpEL", nullResult);
	}

	

}
