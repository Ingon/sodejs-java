package org.sodejs.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sodejs.Js;

public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final Configuration config;
	private final Js js;
	
	public MainServlet(Configuration config) {
		this.config = config;
		this.js = new Js(config.dconfig);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> dynamicVariables = new HashMap<String, Object>();
		dynamicVariables.put("_req", req);
		dynamicVariables.put("_res", resp);
		js.execute(dynamicVariables);
	}
}
