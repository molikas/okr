package okr.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import okr.domain.Objective;

@Service
public class ObjectivesManagement {

	@Autowired
	public ObjectiveRepository oRepo;
	
    @EventListener({ContextRefreshedEvent.class})
    void contextRefreshedEvent() {
    	Objective obj = new Objective();
    	obj.setName("Hello");
    	obj.setDescription("world");
		oRepo.save(obj);
    }
	
	
	
}
