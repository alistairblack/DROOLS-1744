package my.drools.templates.engine;

import my.drools.templates.exception.RuleRunnerException;
import my.drools.templates.session.RuleSession;
import org.jboss.logging.Logger;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RuleRunner {
    private static final Logger logger = Logger.getLogger(RuleRunner.class);

    private static RuleRunner instance;
    private static final String CUSTOM_SETTINGS_PROPERTY = "kie.maven.settings.custom";

    private KieContainer kContainer;

    private RuleRunner() {
    }

    public static RuleRunner getInstance() {
        if (instance == null) {
            instance = new RuleRunner();
        }

        return instance;
    }

    private boolean verifyContainer() {
        Results results = kContainer.verify();

        if (logger.isDebugEnabled()) {
            results.getMessages()
                    .forEach(message ->
                            logger.debug(">> Verify Message ( " + message.getLevel() + " ): "
                                    + message.getText()));
        }
        return !results.hasMessages(Message.Level.ERROR);
    }

    public RuleRunner buildKnowledgeBase() throws RuleRunnerException {
        if (kContainer == null) {
            KieServices ks = KieServices.Factory.get();

            String oldSettingsXmlPath = System.getProperty(CUSTOM_SETTINGS_PROPERTY);
            try {
                System.setProperty(CUSTOM_SETTINGS_PROPERTY, "src/main/resources/kieSettings.xml");
                ReleaseId releaseId = ks.newReleaseId("my.drools.templates", "rules", "1.0-SNAPSHOT");

                kContainer = ks.newKieContainer(releaseId);

                KieScanner kieScanner = ks.newKieScanner(kContainer);
                // Start the KieScanner polling the Maven repository every 5 minutes
                kieScanner.start(300000L);

                boolean verified = verifyContainer();
                if (!verified) {
                    throw new RuleRunnerException("Unable to initialise the rule runner.");
                }

            } finally {
                if (oldSettingsXmlPath == null) {
                    System.clearProperty(CUSTOM_SETTINGS_PROPERTY);
                } else {
                    System.setProperty(CUSTOM_SETTINGS_PROPERTY, oldSettingsXmlPath);
                }
            }
        }
        return this;
    }

    private StatelessKieSession getRuleSession(String ruleset) {
        StatelessKieSession kSession = kContainer.newStatelessKieSession(ruleset);
        if (logger.isDebugEnabled()) {
            logger.debug("Returning kSession: " + kSession);
        }
        return kSession;
    }

    public Object executeRules(String ruleSetArgument, final RuleSession ruleSession) {
        StatelessKieSession kSession;

        if (logger.isDebugEnabled()) {
            logger.debug("IN EXECUTE RULES");
            logger.debug("ruleset: " + ruleSetArgument);
        }

        if (ruleSetArgument == null) {
            return null;
        }

        final String ruleSet = ruleSetArgument.toLowerCase();
        if (logger.isDebugEnabled()) {
            logger.debug("ruleset: " + ruleSet);
        }

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("kContainer: " + kContainer);
            }

            kSession = getRuleSession(ruleSet);

            if (logger.isDebugEnabled()) {
                logger.debug("KieSession: " + kSession);
            }

            insertGlobalsIntoKnowledgeSession(kSession, ruleSession);

            List<Command> commands = prepareBatchExecutionCommands(ruleSession);

            kSession.execute(CommandFactory.newBatchExecution(commands));
        } catch (Exception ex) {
            logger.warn(ex);
        }

        return ruleSession.getGlobal("response");
    }

    private List<Command> prepareBatchExecutionCommands(RuleSession ruleSession) {
        List<Command> commands = new ArrayList<>();
        for (Object fact : ruleSession.getFacts()) {
            commands.add(CommandFactory.newInsert(fact));
            if (logger.isDebugEnabled()) {
                logger.debug("Inserting Fact: " + fact);
            }
        }
        return commands;
    }

    private void insertGlobalsIntoKnowledgeSession(StatelessKieSession kSession, RuleSession ruleSession) {
        Map<String, Object> globals = ruleSession.getGlobals();
        for (String globalName : globals.keySet()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Setting Global: " + globalName + " - " + globals.get(globalName));
            }
            kSession.setGlobal(globalName, globals.get(globalName));
        }
    }

}
