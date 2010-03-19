package org.sodejs;

import java.io.File;
import java.io.FileReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Module {
	private final Scriptable rootScope;
	private final File moduleFile;
	private final Script module;
	
	public Module(Scriptable rootScope, File moduleFile) {
		this.rootScope = rootScope;
		this.moduleFile = moduleFile;
		this.module = compileModule();
	}

	private Script compileModule() {
		Context ctx = Context.getCurrentContext();
		
		try {
			return ctx.compileReader(new FileReader(moduleFile), getName(), 0, null);
		} catch(Exception exc) {
			throw new RuntimeException(exc);
		}
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
//		try {
//			ctx.evaluateReader(obj, new FileReader(moduleFile), getName(), 0, null);
		module.exec(ctx, obj);
//		} catch (IOException e) {
//			throw new RuntimeException("Problems loading module", e);
//		}
		
		return (ScriptableObject) ScriptableObject.getProperty(obj, "exports");
	}
}
