package org.sodejs;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;

public class JsRequireFunction extends BaseFunction {
    private static final long serialVersionUID = 1L;

    private final JsEngineProd js;

    public JsRequireFunction(JsEngineProd js) {
        this.js = js;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        String moduleName = (String) FunctionObject.convertArg(cx, scope, args[0], FunctionObject.JAVA_STRING_TYPE);
        return js.require(moduleName);
    }

    @Override
    public String getFunctionName() {
        return "require";
    }
}
