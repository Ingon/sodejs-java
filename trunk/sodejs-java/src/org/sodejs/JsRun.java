package org.sodejs;

import java.util.HashMap;

public class JsRun {
    public static void main(final String[] args) {
        JsConfig config = new JsConfig();
        
        config.mainModule = args[0];
        config.mainFunction = "main";
        
        JsEngine engine = new JsEngineDev(config);
        engine.init(new HashMap<String, Object>(){{
            put("args", args);
        }});
        engine.execute();
    }
}
