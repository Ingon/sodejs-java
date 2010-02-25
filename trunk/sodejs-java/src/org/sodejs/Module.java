package org.sodejs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Module {
	private final Scriptable rootScope;
	private final File moduleFile;
	
	public Module(Scriptable rootScope, File moduleFile) {
		this.rootScope = rootScope;
		this.moduleFile = moduleFile;
	}

	public String getName() {
		return moduleFile.getName().split("\\.")[0];
	}

	public ScriptableObject load() {
		Context ctx = Context.getCurrentContext();
		
		Scriptable obj = ctx.newObject(rootScope);
		obj.setPrototype(rootScope);
		obj.setParentScope(null);
		
		ScriptableObject.putProperty(obj, "exports", ctx.newObject(obj));
		try {
			ctx.evaluateReader(obj, new FileReader(moduleFile), getName(), 0, null);
		} catch (IOException e) {
			throw new RuntimeException("Problems loading module", e);
		}
		
		return (ScriptableObject) ScriptableObject.getProperty(obj, "exports");
	}
}
