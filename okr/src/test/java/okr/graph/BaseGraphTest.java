package okr.graph;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BaseGraphTest {

    @Autowired
    private ResourceLoader resourceLoader;
	
    protected String importPattern = "data/*.json";
    
    protected Map<String, JsonNode> jsonTestData = new HashMap<>();
    
	@Before
	public void setUp() throws Exception {
      Resource[] schResources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(importPattern);

      ObjectMapper mapper = new ObjectMapper();
       
      for (Resource schResource: schResources) {
          File file = schResource.getFile();
          String jsonString = IOUtils.toString(new FileInputStream(file));
          
          JsonNode actualObj = mapper.readTree(jsonString);
          jsonTestData.put(file.getName(), actualObj);
      }
	}

}
