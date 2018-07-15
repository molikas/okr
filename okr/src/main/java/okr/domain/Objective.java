package okr.domain;

import org.neo4j.ogm.annotation.NodeEntity;;

@NodeEntity
public class Objective extends AbstractGraphElement {

	private String trackingId;
	
	private String name;
	
	private String description;
	
	public Objective() {
		super();
	}
		
	public Objective(String trackingId, String name, String description) {
		super();
		this.trackingId = trackingId;
		this.name = name;
		this.description = description;
	}

	// Getters- Setters
	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}