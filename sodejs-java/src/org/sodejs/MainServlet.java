package org.sodejs;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

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
			
			ScriptableObject.putProperty(obj, "_req", req);
			ScriptableObject.putProperty(obj, "_res", resp);
			
			for(String folder : config.dconfig.loadLocations) {
				evalJsFolder(ctx, obj, folder);
			}
			
			ctx.evaluateString(obj, "service();", "service", 0, null);
		} finally {
			ctx.exit();
		}
	}

	private void evalJsFolder(Context ctx, ScriptableObject root, String folderPath) throws IOException {
		File mainFolder = new File(folderPath);
		File[] jsFiles = mainFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".js");
			}});
		
		for(File jsFile : jsFiles) { // TODO modules?
			ctx.evaluateReader(root, new FileReader(jsFile), jsFile.getName(), 0, null);
		}
	}
}
