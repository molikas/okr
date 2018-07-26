package okr.etl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;

import okr.mapping.schema.ExpressionBasedMapper;
import okr.mapping.schema.LocalJsonRepository;
import okr.mapping.schema.SchemaGraph;
import okr.neo4j.repository.Objective;
import okr.neo4j.repository.ObjectiveRepository;

/**
 * Provider that takes all the data from Jira. It is possible to extend the
 * implementation to perform custom mapping and more complex data processing.
 * TODO: Deployment configuration and orchestration
 * @author isidenica
 */
@Component
public class JiraDataProvider extends JiraInvoker implements DataProvider {

	private static final Logger log = Logger.getLogger(JiraDataProvider.class.getName());
	
	@Value(value="${etl.import.jql}")
	private String importJql;
	
	@Value(value="${etl.import.schemaName}")
	private String schemaName;	
	
	@Autowired
	private ObjectiveRepository oRepo;
	
	@Autowired
	private LocalJsonRepository jsonRepo;
	
	/**
	 * Load data once on application startup
	 * TODO: Move to scheduled task
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void oneBigImportJob() {
		if (StringUtils.isEmpty(importJql)) return; //TODO: Proper configuration loading
		
		Set<String> fields = new HashSet<>(CollectionUtils.arrayToList(new String[]{"*navigable"}));

		int i, total;
		i = total = 0;
		List<Objective> objectiveList = new ArrayList<>();
		do {
			// get initial data
			Promise<SearchResult> searchJqlPromise = client().getSearchClient().searchJql(importJql, 100, 0, fields );
			SearchResult srez = searchJqlPromise.claim();
			total = srez.getTotal();
			
			SchemaGraph gSchema = jsonRepo.retrieveSchema(schemaName);
			objectiveList.addAll(new ExpressionBasedMapper(Objective.class).mapNodes(srez.getIssues(), gSchema));

			i += 100;
		}while (i < total);
		// just to make sure there is no magic
		if (objectiveList.size() != total) {
			log.info("Retrieved issues sizes do not match total= "+total+ "; actual size="+objectiveList.size());
		}
		oRepo.save(objectiveList, 1);
		oRepo.findAll();
	}

	// Getters - Setters
	public String getImportJql() {
		return importJql;
	}

	public void setImportJql(String importJql) {
		this.importJql = importJql;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

}
