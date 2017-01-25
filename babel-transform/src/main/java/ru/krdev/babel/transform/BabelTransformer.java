package ru.krdev.babel.transform;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

/**
 *
 * @author sergekos
 */
public class BabelTransformer {
    private final String[] presets;
    private boolean minified = false;

    private final SimpleBindings bindings = new SimpleBindings();
    private ScriptEngine jse;

    public BabelTransformer() {
        presets = new String[] {"react", "es2015"};
    }

    public BabelTransformer(boolean minified) {
        this();
        this.minified = minified;
    }

    public BabelTransformer(boolean minified, String... presets) {
        this.presets = presets;
        this.minified = minified;
    }   
    
    public String transform(String code) throws ScriptException {
        if (jse == null) {
            initEngine();
        }

        bindings.put("input", code);
        bindings.put("presets", presets);
        bindings.put("minified", minified);

        Object output = jse.eval(
                "Babel.transform(input, {presets: Java.from(presets), minified: minified}).code",
                bindings);

        return (String) output;
    }

    private void initEngine() throws ScriptException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        jse = engineManager.getEngineByName("nashorn");

        InputStream babel = getClass().getResourceAsStream("/babel/babel-6.19.0.js");

        jse.eval(new BufferedReader(new InputStreamReader(babel)), bindings);
    }

}
