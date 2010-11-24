package org.sodejs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class JsModule {
    private final Scriptable rootScope;
    private final File moduleFile;

    private Scriptable module;

    public JsModule(Scriptable rootScope, File moduleFile) {
        this.rootScope = rootScope;
        this.moduleFile = moduleFile;
    }

    public String getName() {
        return moduleFile.getName().split("\\.")[0];
    }

    public Scriptable load(Scriptable parent) {
        if(module != null) {
            return module;
        }

        Context ctx = Context.getCurrentContext();
        ctx.setOptimizationLevel(-1);

        Scriptable obj = ctx.newObject(rootScope);
        obj.setPrototype(rootScope);
        obj.setParentScope(parent);

        ScriptableObject.putProperty(obj, "exports", ctx.newObject(obj));
        try {
            ctx.evaluateString(obj, loadModuleSource(), getName(), 0, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        module = (Scriptable) ScriptableObject.getProperty(obj, "exports");
        return module;
    }

    protected String loadModuleSource() throws IOException {
        FileReader reader = new FileReader(moduleFile);
        try {
            BufferedReader br = new BufferedReader(reader);
            StringBuffer sb = new StringBuffer();
    
            for(String line = br.readLine(); line != null; line = br.readLine()) {
                sb.append(line + "\r\n");
            }
    
            return sb.toString();
        } finally {
            reader.close();
        }
    }
}
