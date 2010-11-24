package org.sodejs;

import java.io.File;
import java.io.IOException;

import org.mozilla.javascript.Scriptable;

public class JsE4XModule extends JsModule {
    public JsE4XModule(Scriptable rootScope, File moduleFile) {
        super(rootScope, moduleFile);
    }

    @Override
    protected String loadModuleSource() throws IOException {
        String source = super.loadModuleSource();
        return "exports.page = " + source;
    }
}
