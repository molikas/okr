package okr.neo4j.repository;

import java.util.Map;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import okr.configuration.JunitPersistenceContext;

/**
 * Abstract test to initialize common configuration for Neo4j tests
 * @author isidenica
 */
@TestPropertySource("classpath:application-test.properties")
@ContextConfiguration(classes = {JunitPersistenceContext.class})
public class Neo4jBaseTest{

    @Autowired
    private ApplicationContext ctx;

    @Before // clean DB
    public void clearAllGraphRepositories() {
        Map<String, Neo4jRepository> graphRepositories = ctx.getBeansOfType(Neo4jRepository.class);
        for (Neo4jRepository graphRepository : graphRepositories.values()) {
            graphRepository.deleteAll();
        }
    }
    
}
