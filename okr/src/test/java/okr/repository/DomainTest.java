package okr.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

import okr.domain.Objective;


/*
 * Sanity check 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DomainTest extends Neo4jTest {

	private static final String OBJ_NAME = "Obj: Name";
    private static final String OBJ_DESCRIPTION = "Obj: Description";
	
	@Autowired
    ObjectiveRepository pRepo;

	/*
	 * Sanity check for create operation
	 */
    @Test
    public void saveObjectiveTest() {
    	Objective obj = new Objective();
    	obj.setName(OBJ_NAME);
    	obj.setDescription(OBJ_DESCRIPTION);
    	pRepo.save(obj);
    	Iterable<Objective> all = pRepo.findAll();
    	
    	assertTrue("Data was not saved", all.iterator().hasNext());
    	
    	Objective result = all.iterator().next();
    	assertEquals("Obj: Name is wrong", OBJ_NAME, result.getName());
    	assertEquals("Obj: Description is wrong", OBJ_DESCRIPTION, result.getDescription());
    	
    }
}
