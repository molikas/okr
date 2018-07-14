package okr.domain;

import org.neo4j.ogm.annotation.NodeEntity;;

@NodeEntity
public class Objective extends AbstractGraphElement {

	private String name;
	
	private String description;
	
	// Getters- Setters
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