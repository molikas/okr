package okr.etl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import okr.mapping.schema.GraphConverter;
import okr.neo4j.repository.BaseNode;

/**
 * Test whether the excel format is being imported and represented properly
 * @author isidenica
 *
 */
public class ExcelDataImporterTest {
	
	ExcelDataExtractor docRepo = new ExcelDataExtractor();
	
	/**
	 * Test that excel is being imported properly into JSON representation.
	 */
	@Test
	public void excelToJsonParserTest() {
		JsonNode nodeTree = docRepo.retrieveJsonDocument("two-sheet-investment.xlsx");
		
		assertTrue("Expecting document object with sheets", nodeTree.isObject());
		
		Iterator<JsonNode> sheetItr = nodeTree.elements();
		JsonNode sheet1 = sheetItr.next();
		JsonNode sheet2 = sheetItr.next();
		
		assertNotNull("Sheet 1 is missing from object list", sheet1);
		assertNotNull("Sheet 2 is missing from object list", sheet2);

		
		assertTrue("Expecting sheet 1 as object", sheet1.isObject());
		assertTrue("Expecting sheet 2 as object", sheet2.isObject());
		
		// Sheet 1
		JsonNode sh1RowArray = sheet1.elements().next();
		Iterator<JsonNode> sh1RowArrayIterator = sh1RowArray.iterator();
		JsonNode sh1Row2 = sh1RowArrayIterator.next();
		JsonNode sh1Row3 = sh1RowArrayIterator.next();
		
		// Sheet 2
		JsonNode sh2RowArray = sheet2.elements().next();
		Iterator<JsonNode> sh2RowArrayIterator = sh2RowArray.iterator();
		JsonNode sh2Row2 = sh2RowArrayIterator.next();
		
		assertFalse("Expecting only two rows and ignoring header row", sh1RowArrayIterator.hasNext());
		
		
		assertTrue("Expecting row mapped as object", sh1Row2.isObject());
		assertTrue("Expecting row mapped as object", sh1Row3.isObject());
		
		
		assertEquals("Sheet 1 row 2 cell not mapped", "text_1.1",  sh1Row2.findValues("header_1.1").iterator().next().asText());
		assertEquals("Sheet 1 row 2 cell not mapped", 1.1,  sh1Row2.findValues("header_1.2").iterator().next().asDouble(), 0);
		assertEquals("Sheet 1 row 2 cell not mapped", true,  sh1Row2.findValues("header_1.3").iterator().next().asBoolean());
		
		assertEquals("Sheet 1 row 3 cell not mapped", "text_1.2",  sh1Row3.findValues("header_1.1").iterator().next().asText());
		assertEquals("Sheet 1 row 3 cell not mapped", 1.2,  sh1Row3.findValues("header_1.2").iterator().next().asDouble(), 0);
		assertEquals("Sheet 1 row 3 cell not mapped", false,  sh1Row3.findValues("header_1.3").iterator().next().asBoolean());		
		
		assertEquals("Sheet 2 row 2 cell not mapped", "text_2.1",  sh2Row2.findValues("header_2.1").iterator().next().asText());
		assertEquals("Sheet 2 row 2 cell not mapped", 3,  sh2Row2.findValues("header_2.2").iterator().next().asDouble(), 0);
		assertEquals("Sheet 2 row 2 cell not mapped", false,  sh2Row2.findValues("header_2.3").iterator().next().asBoolean());			
	}
	
	/**
	 * Test graph representation
	 */
	@Test
	public void excelJsonToGraph() {
		JsonNode document = docRepo.retrieveDocument("two-sheet-investment.xlsx");
		Set<BaseNode> verts = new GraphConverter().fromJson(document).vertexSet();
		
		assertEquals("Wrong amount of vertexes initialized from fiven json", 8, verts.size());
	}	
	
}
