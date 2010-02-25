package org.sodejs;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final Configuration config;
	private final Map<String, Module> modules = new HashMap<String, Module>();
	
	public MainServlet(Configuration config) {
		this.config = config;
	}

	@Override
	public void init() throws ServletException {
		for(String driver : config.dconfig.dbDrivers) {
			try {
				Class.forName(driver);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Context ctx = ContextFactory.getGlobal().enterContext();
		
		try {
			ScriptableObject obj = ctx.initStandardObjects();
			
			ScriptableObject.putProperty(obj, "require", new RequireFunction(this));
			preInitializeModules(obj);
			
			ScriptableObject.putProperty(obj, "_req", req);
			ScriptableObject.putProperty(obj, "_res", resp);
			
			ctx.evaluateReader(obj, new FileReader(config.dconfig.main), config.dconfig.main, 0, null);
		} catch(Exception exc) {
			exc.printStackTrace();
		} finally {
			Context.exit();
		}
	}

	private void preInitializeModules(ScriptableObject root) {
		for(String folder : config.dconfig.libLocations) {
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

	public ScriptableObject require(String moduleName) {
		Module module = modules.get(moduleName);
		if(module == null) {
			throw new RuntimeException("Module " + moduleName + " does not exists");
		}
		
		return module.load();
	}
}
