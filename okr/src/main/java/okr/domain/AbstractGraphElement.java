package okr.domain;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Common node class
 * @author isidenica
*/
@NodeEntity
public class AbstractGraphElement {
	
	@Id @GeneratedValue private Long id;

	// Getter - Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
