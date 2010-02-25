package org.sodejs;

import java.io.FileReader;
import java.util.Properties;

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
	
	private static Configuration parseConfig(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileReader(args[0]));
		
		Configuration config = new Configuration();
		
		String portStr = props.getProperty("port");
		config.port = portStr != null ? Integer.parseInt(portStr) : 8080;

		config.dconfig = new DynamicConfiguration();
		config.dconfig.pathSpec = props.getProperty("d.pathSpec");
		config.dconfig.main = props.getProperty("d.main");
		for(Object o : props.keySet()) {
			String prop = (String) o;
			if(prop.startsWith("d.libLocation.")) {
				config.dconfig.libLocations.add(props.getProperty(prop));
			}
		}
		
		config.sconfig = new StaticConfiguration();
		config.sconfig.base = props.getProperty("s.base");
		config.sconfig.welcome = props.getProperty("s.welcome");
		
		return config;
	}
}
