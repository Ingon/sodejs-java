package org.sodejs;

import java.util.Map;

public interface JsEngine {

    public abstract void init();

    public abstract void init(Map<String, Object> constants);

    public abstract Object execute(Object... parameters);

}
