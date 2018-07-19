package okr.neo4j.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*
 * Base domain crud sanity checks
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BaseDomainTest extends Neo4jBaseTest {

	@Autowired
	private SessionFactory sessionFactory;
	private Session session;
	
	/**
	 * Test out how to map a generic node.
	 */
	@Before
	public void prepareAndWire() {
		super.clearAllGraphRepositories();
		session = sessionFactory.openSession();
	}

	/*
	 * BaseNode write/read without OGM
	 */
    @Test
    public void baseNodePeristanceTest() {
		BaseNode baseNode = new BaseNode();
		baseNode.getLabels().add("My Label");
		baseNode.getProperties().put("Prop 1", "Value 1");
		session.save(baseNode);
		
		Iterable<BaseNode> res = session.query(BaseNode.class, "MATCH (n) RETURN n", new HashMap<>());
		assertEquals("Base node was not saved in DB", true, res.iterator().hasNext());
		
		BaseNode resultNode = res.iterator().next();
		assertEquals("Stored label does not match", "My Label", resultNode.getLabels().iterator().next());
		assertEquals("Stored property value does not match", "Value 1", resultNode.getProperties().get("Prop 1"));
    }
    
	@Autowired
    ObjectiveRepository oRepo;

	/*
	 * Sanity check for create/read operation for a custom entity
	 */
    @Test
    public void customNodePeristanceTest() {
    	Objective obj = new Objective();
    	oRepo.save(obj);
    	Iterable<Objective> all = oRepo.findAll();
    	
    	assertTrue("Data was not saved", all.iterator().hasNext());
    	
    	Objective result = all.iterator().next();
    	assertTrue("UUID: is missing", !StringUtils.isEmpty(result.getUuid()));
    }    

}
