package rules;

import my.drools.templates.facts.FactOne;
import my.drools.templates.facts.RuleResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


public class RuleRunnerTest extends RuleTest
{

	private RuleResponse ruleResponse;

	@Before
	public void setup() throws Exception
	{
		ruleResponse = new RuleResponse();
		setup(ruleResponse);
	}

	private void executeRules() throws Exception
	{
		Object response = runner.executeRules("inheritedTemplateModules-ruleModule_1_kbase", ruleSession);
		ruleResponse = (RuleResponse) response;
	}

	@Test
	public void testMainNameChange_Pending() throws Exception
	{
		List<Object> facts = new ArrayList<>();
		FactOne factOne = new FactOne();
		factOne.setParameterName("AAA");
		ruleSession.setFacts(facts);

		executeRules();

		assertTrue(ruleResponse.getResult());
	}

}