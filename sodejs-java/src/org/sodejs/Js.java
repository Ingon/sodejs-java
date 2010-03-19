package org.sodejs;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

public class Js {
	private final JsConfiguration config;
	private final Map<String, Module> modules = new HashMap<String, Module>();
	
	private ScriptableObject scope;
	private Script script;
	private Function main;

	public Js(JsConfiguration config) {
		this.config = config;
		init();
	}
	
	private void init() {
		Context ctx = ContextFactory.getGlobal().enterContext();
		ctx.setOptimizationLevel(9);
		
		try {
			scope = ctx.initStandardObjects();
			
			ScriptableObject.putProperty(scope, "require", new RequireFunction(this));
			preInitializeModules(scope);
			
			script = ctx.compileReader(new FileReader(config.main), config.main, 0, null);
			script.exec(ctx, scope);
			
			main = (Function) ScriptableObject.getProperty(scope, "mainExecute");
		} catch(Exception exc) {
			throw new RuntimeException(exc);
		} finally {
			Context.exit();
		}
	}

	public void execute(Map<String, Object> dynamicVariables) {
		Context ctx = ContextFactory.getGlobal().enterContext();
		
		try {
//			ScriptableObject obj = ctx.initStandardObjects();
//			
//			ScriptableObject.putProperty(obj, "require", new RequireFunction(this));
//			preInitializeModules(obj);
//			
//			for(Map.Entry<String, Object> var : dynamicVariables.entrySet()) {
//				ScriptableObject.putProperty(obj, var.getKey(), var.getValue());
//			}
//			
//			ctx.evaluateReader(obj, new FileReader(config.main), config.main, 0, null);
			
			for(Map.Entry<String, Object> var : dynamicVariables.entrySet()) {
				ScriptableObject.putProperty(scope, var.getKey(), var.getValue());
			}
			
			main.call(ctx, scope, null, new Object[0]);
		} catch(Exception exc) {
			exc.printStackTrace();
		} finally {
			Context.exit();
		}
	}

	private void preInitializeModules(ScriptableObject root) {
		for(String folder : config.libLocations) {
			preInitializeModule(root, folder);
		}
	}
	
	private void preInitializeModule(ScriptableObject root, String folderPath) {
		File mainFolder = new File(folderPath);
		File[] jsFiles = mainFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".js");
			}});
		
		for(File jsFile : jsFiles) {
			Module m = new Module(root, jsFile);
			if(modules.containsKey(m.getName())) {
				throw new UnsupportedOperationException("Module with same name already exists, implement hierarchical modules");
			}
			modules.put(m.getName(), m);
		}
	}

	protected ScriptableObject require(String moduleName) {
		Module module = modules.get(moduleName);
		if(module == null) {
			throw new RuntimeException("Module " + moduleName + " does not exists");
		}
		
		return module.load();
	}
}
