package okr.app;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableNeo4jRepositories ("okr.neo4j.repository")
@ComponentScan ({"okr.neo4j.repository", "okr.etl", "okr.mapping"})
public class Application {
	
	@Value("${neo4j.username}")
	private String username;
	
	@Value("${neo4j.password}")
	private String password;
	
	@Value("${neo4j.url}")
	private String url;	
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
	public SessionFactory sessionFactory() {
		return new SessionFactory(getConfiguration(), "okr.neo4j.repository");
	}

	@Bean
	public Configuration getConfiguration() {
		StringBuilder sb = new StringBuilder();
		sb.append("http://").append(username).append(":").append(password).append("@").append(url);
		return new Configuration.Builder().uri(sb.toString()).build();
	}

	@Bean
	public Neo4jTransactionManager transactionManager() {
		return new Neo4jTransactionManager(sessionFactory());
	}
}