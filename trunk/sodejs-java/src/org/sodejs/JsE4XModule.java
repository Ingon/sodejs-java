package org.sodejs;

import java.io.File;
import java.io.IOException;

import org.mozilla.javascript.Scriptable;

public class JsE4XModule extends JsModule {
    public JsE4XModule(Scriptable rootScope, File moduleFile, boolean precompile) {
        super(rootScope, moduleFile, precompile);
    }

    @Override
    protected String loadModuleSource() throws IOException {
        String source = super.loadModuleSource();
        return "exports.page = " + source;
    }
}
