package okr.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import okr.domain.Objective;

/**
 * Access for objectives
 * @author isidenica
 */
public interface ObjectiveRepository extends Neo4jRepository<Objective, Long> {

}
