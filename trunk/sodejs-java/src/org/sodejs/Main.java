package org.sodejs;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {
	public static void main(String[] args) throws Exception {
		Configuration config = parseConfig(args);
		
		Server server = new Server(config.port);
		HandlerList handlers = new HandlerList();
		
		if(config.dconfig != null) {
			ServletContextHandler dynamicHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
			dynamicHandler.addServlet(new ServletHolder(new MainServlet(config)), config.dconfig.pathSpec);
			handlers.addHandler(dynamicHandler);
		}
		
		if(config.sconfig != null) {
			ResourceHandler staticHandler = new ResourceHandler();
			staticHandler.setResourceBase(config.sconfig.base);
			staticHandler.setWelcomeFiles(new String[] {config.sconfig.welcome});
			handlers.addHandler(staticHandler);
		}
		
		handlers.addHandler(new DefaultHandler());
		server.setHandler(handlers);
		
		server.start();
		server.join();
	}
	
	private static Configuration parseConfig(String[] args) {
		Configuration config = new Configuration();
		config.port = 8081;
		
		config.dconfig = new DynamicConfiguration();
		config.dconfig.pathSpec = "/dyn/*";
		
		config.sconfig = new StaticConfiguration();
		config.sconfig.base = "../fail/static";
		config.sconfig.welcome = "index.html";
		
		return config;
	}
}
