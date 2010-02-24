package org.sodejs;

import org.eclipse.jetty.server.Handler;
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
		
		ServletContextHandler dynamicHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		dynamicHandler.addServlet(new ServletHolder(new MainServlet(config)), "/dyn/*");
		
		ResourceHandler staticHandler = new ResourceHandler();
		staticHandler.setResourceBase(config.staticBase);
		staticHandler.setWelcomeFiles(new String[] {"index.html"});
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {dynamicHandler, staticHandler, new DefaultHandler()});
		server.setHandler(handlers);
		
		server.start();
		server.join();
	}
	
	private static Configuration parseConfig(String[] args) {
		Configuration config = new Configuration();
		config.port = 8081;
		config.staticBase = "../fail/static";
		return config;
	}
}
