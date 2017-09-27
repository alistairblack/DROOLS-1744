package rules;

import my.drools.templates.engine.RuleRunner;
import my.drools.templates.facts.RuleResponse;
import my.drools.templates.session.RuleSession;

abstract class RuleTest
{

	static RuleRunner runner;
	RuleSession ruleSession;

	void setup(final RuleResponse ruleResponse) throws Exception
	{
		runner = KBase.getRuleRunner();
		ruleSession = getRuleSession(ruleResponse);
	}

	private RuleSession getRuleSession(final RuleResponse ruleResponse) throws Exception {
		RuleSession ruleSession = new RuleSession();
		ruleSession.addGlobal("response",ruleResponse);
		return ruleSession;
	}
}
