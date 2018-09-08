package okr.neo4j.repository;

import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Objective extends BaseNode {

	public Objective() {
		super();
	}
		
	public Objective(String trackingId) {
		super();
	}

}