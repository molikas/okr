package okr.configuration;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Neo4j session configuration class
 * @author isidenica
 *
 */
@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories ("okr.neo4j.repository")
@ComponentScan ({"okr.neo4j.repository", "okr.etl", "okr.mapping"})
public class JunitPersistenceContext {

	@Bean
	public org.neo4j.ogm.config.Configuration configuration() {
		org.neo4j.ogm.config.Configuration.Builder builder = new org.neo4j.ogm.config.Configuration.Builder();
		return builder.build();
	}

	@Bean
	public SessionFactory sessionFactory() {
		return new SessionFactory(configuration(), "okr.neo4j.repository");
	}

	@Bean
	public Neo4jTransactionManager transactionManager() {
		return new Neo4jTransactionManager(sessionFactory());
	}
}

