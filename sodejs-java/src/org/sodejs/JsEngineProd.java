package org.sodejs;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class JsEngineProd implements JsEngine {
    private final JsConfig config;
    private final Map<String, JsModule> modules;

    private Scriptable scope;
    private Script script;
    private Function main;

    public JsEngineProd(JsConfig config) {
        this.config = config;
        this.modules = new HashMap<String, JsModule>();
    }

    public void init() {
        init(Collections.<String, Object>emptyMap());
    }

    public void init(Map<String, Object> constants) {
        Context ctx = ContextFactory.getGlobal().enterContext();
        ctx.setOptimizationLevel(config.precompile ? 9 : -1);

        try {
            scope = ctx.initStandardObjects();

            ScriptableObject.putConstProperty(scope, "require", new JsRequireFunction(this));
            ScriptableObject.putConstProperty(scope, "requirex", new JsRequireE4XFunction(this));

            for(Map.Entry<String, Object> constant : constants.entrySet()) {
                ScriptableObject.putConstProperty(scope, constant.getKey(), constant.getValue());
            }

            preInitializeModules(scope);

            FileReader reader = new FileReader(config.mainModule);
            try {
                script = ctx.compileReader(reader, config.mainModule, 0, null);
                script.exec(ctx, scope);
    
                main = (Function) ScriptableObject.getProperty(scope, config.mainFunction);
            } finally {
                reader.close();
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            Context.exit();
        }
    }

    public Object execute(Object... parameters) {
        Context ctx = ContextFactory.getGlobal().enterContext();

        try {
            return main.call(ctx, scope, null, parameters == null ? new Object[0] : parameters);
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        } finally {
            Context.exit();
        }
    }

    private void preInitializeModules(Scriptable root) {
        for (String folder : config.libs) {
            preInitializeModule(root, folder);
        }
    }

    private void preInitializeModule(Scriptable root, String folderPath) {
        File moduleFolder = new File(folderPath);
        File[] jsFiles = moduleFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".js") || name.endsWith(".xml");
            }
        });

        for (File jsFile : jsFiles) {
            JsModule module = null;
            if (jsFile.getName().endsWith("js")) {
                module = new JsModule(root, jsFile, config.precompile);
            } else if (jsFile.getName().endsWith("xml")) {
                module = new JsE4XModule(root, jsFile, config.precompile);
            } else {
                throw new RuntimeException("Unknown extension");
            }

            if (modules.containsKey(module.getName())) {
                throw new UnsupportedOperationException("Module with same name already exists, implement hierarchical modules");
            }
            modules.put(module.getName(), module);
        }
    }

    protected Scriptable require(String moduleName) {
        JsModule module = modules.get(moduleName);
        if (module == null) {
            throw new RuntimeException("Module " + moduleName + " does not exists");
        }

        return module.load(null);
    }

    protected Scriptable requirex(String moduleName, Scriptable ctx) {
        JsModule module = modules.get(moduleName);
        if (module == null) {
            throw new RuntimeException("Module " + moduleName + " does not exists");
        }

        return module.load(ctx);
    }
}
