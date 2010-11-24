package org.sodejs;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JsEngineDev implements JsEngine {
    private final JsConfig config;
    private Map<String, Object> constants;
    private JsEngineProd engine;
    private Map<File, Long> scanCache;

    public JsEngineDev(JsConfig config) {
        this.config = config;
        this.scanCache = new HashMap<File, Long>();
    }

    public void init() {
        init(Collections.<String, Object>emptyMap());
    }

    public void init(Map<String, Object> constants) {
        this.constants = constants;

        scanModules();
        createEngine();
    }

    private void createEngine() {
        engine = new JsEngineProd(config);
        engine.init(this.constants);
    }

    public Object execute(Object... parameters) {
        if(scanModules()) {
            createEngine();
        }

        return engine.execute(parameters);
    }

    private boolean scanModules() {
        boolean needsReload = false;
        needsReload = scanFile(new File(config.mainModule));
        for (String folder : config.libs) {
            needsReload = needsReload || scanModule(folder);
        }
        return needsReload;
    }

    private boolean scanModule(String folderPath) {
        File moduleFolder = new File(folderPath);
        File[] jsFiles = moduleFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".js") || name.endsWith(".xml");
            }
        });

        boolean needsReload = false;
        for (File jsFile : jsFiles) {
            needsReload = needsReload || scanFile(jsFile);
        }
        return needsReload;
    }

    private boolean scanFile(File f) {
        boolean needsReload = false;

        Long lastModified = scanCache.get(f);
        needsReload = needsReload || lastModified == null;

        Long newLastModified = f.lastModified();
        needsReload = needsReload || (lastModified != null && lastModified.longValue() != newLastModified.longValue());
        scanCache.put(f, newLastModified);

        return needsReload;
    }
}
