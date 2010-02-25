package org.sodejs;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;

public class RequireFunction extends BaseFunction {
	private static final long serialVersionUID = 1L;

	private final MainServlet servlet;
	
	public RequireFunction(MainServlet servlet) {
		this.servlet = servlet;
	}
	
	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		String moduleName = (String) FunctionObject.convertArg(cx, scope, args[0], FunctionObject.JAVA_STRING_TYPE);
		return servlet.require(moduleName);
	}

	@Override
	public String getFunctionName() {
		return "require";
	}
}
