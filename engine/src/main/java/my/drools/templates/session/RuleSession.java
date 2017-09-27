package my.drools.templates.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleSession {

    private Map<String, Object> globals = new HashMap<>();
    private List<Object> facts = new ArrayList<>();

    public Map<String, Object> getGlobals()
    {
        return globals;
    }

    public void setGlobals(Map<String, Object> globals)
    {
        this.globals = globals;
    }

    public void addGlobal(String name, Object global)
    {
        this.globals.put(name, global);
    }

    public Object getGlobal(String name)
    {
        return this.globals.get(name);
    }

    public List<Object> getFacts() {
        return facts;
    }

    public void setFacts(List<Object> facts) {
        this.facts = facts;
    }

    public void reset() {
        facts.clear();
    }
}
