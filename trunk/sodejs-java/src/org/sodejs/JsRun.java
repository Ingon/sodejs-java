package org.sodejs;

import java.util.ArrayList;
import java.util.List;

public class JsRun {
    public static void main(final String[] args) {
        JsConfig config = new JsConfig();
        
        config.mainModule = "main.js";
        config.mainFunction = "main";
        
        List<String> params = new ArrayList<String>();
        for(int i = 0; i < args.length; i++) { // XXX a bit ugly, try and fix
        	if("--main-module".equals(args[i])) {
        		config.mainModule = args[i + 1];
        		i++;
        		continue;
        	}
        	if("--main-function".equals(args[i])) {
        		config.mainFunction = args[i + 1];
        		i++;
        		continue;
        	}
        	if("--lib".equals(args[i])) {
        		config.libs.add(args[i + 1]);
        		i++;
        		continue;
        	}
        	
        	params.add(args[i]);
        }
        
        JsEngine engine = new JsEngineDev(config);
        engine.init(); // TODO something to setup these from command line?
        engine.execute(params.toArray());
    }
}
