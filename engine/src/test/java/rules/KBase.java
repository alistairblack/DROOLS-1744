package rules;

import my.drools.templates.engine.RuleRunner;
import my.drools.templates.exception.RuleRunnerException;
import org.jboss.logging.Logger;

class KBase
{
    private final static Logger logger = Logger.getLogger(RuleRunner.class);

    private static RuleRunner ruleRunner;

    static RuleRunner getRuleRunner() throws RuleRunnerException {
        if(ruleRunner == null)
        {
            logger.debug("Building KnowledgeBase");
            ruleRunner = RuleRunner.getInstance().buildKnowledgeBase();
            logger.debug("Built");
        }

        return ruleRunner;
    }
}
