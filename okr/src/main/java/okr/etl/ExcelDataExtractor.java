package okr.etl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okr.mapping.schema.DocumentInstance;
import okr.mapping.schema.DocumentRepository;
import okr.mapping.schema.LocalRepository;

public class ExcelDataExtractor extends LocalRepository implements DocumentRepository {

	@Override
	public DocumentInstance retrieveDocument(String documentName) {
		return new DocumentInstance(retrieveJsonDocument(documentName));
	}

	public JsonNode retrieveJsonDocument(String documentName) {

		Workbook workbook;
		try {
			workbook = new XSSFWorkbook(getInputStream(DOCUMENTS_PATH + documentName));

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode documentNode = mapper.createObjectNode();

		Iterator<Sheet> sheetItr = workbook.sheetIterator();
		while (sheetItr.hasNext()) {
			Sheet sheet = sheetItr.next();
			ObjectNode sheetArrayNode = mapper.createObjectNode();

			Iterator<Row> rowItr = sheet.iterator();
			ArrayNode rowsArrayNode = mapper.createArrayNode();

			int i = 1;
			Map<Integer, String> headerRow = new HashMap<>();
			while (rowItr.hasNext()) {

				Row row = rowItr.next();
				if (i == 1) {
					i++;
					headerRow = readHeaderRow(row);
					continue;
				}

				Iterator<Cell> cellItr = row.iterator();
				ObjectNode rowObject = mapper.createObjectNode();
				boolean rowNotEmpty = false;
				while (cellItr.hasNext()) {

					Cell currentCell = cellItr.next();
					int address = currentCell.getColumnIndex();

					switch (currentCell.getCellTypeEnum()) {

					case STRING:
						rowObject.put(headerRow.get(address), currentCell.getStringCellValue());
						rowNotEmpty = true;
						break;

					case NUMERIC:
						rowObject.put(headerRow.get(address), currentCell.getNumericCellValue());
						rowNotEmpty = true;
						break;

					case BOOLEAN:
						rowObject.put(headerRow.get(address), currentCell.getBooleanCellValue());
						rowNotEmpty = true;
						break;

					default:
						break;
					}
				} // end cell iteration
				if (rowNotEmpty) rowsArrayNode.add(rowObject);
			} // end rows iteration
			sheetArrayNode.set("row", rowsArrayNode);
			documentNode.set(sheet.getSheetName(), sheetArrayNode);
		}

		return documentNode;
	}

	private Map<Integer, String> readHeaderRow(Row row) {
		Map<Integer, String> result = new HashMap<>();
		Iterator<Cell> cellItr = row.iterator();
		while (cellItr.hasNext()) {
			Cell currentCell = cellItr.next();
			int address = currentCell.getColumnIndex();
			result.put(address, currentCell.getStringCellValue());
		}

		return result;
	}

}
