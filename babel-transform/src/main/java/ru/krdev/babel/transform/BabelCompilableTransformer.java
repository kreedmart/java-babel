/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.krdev.babel.transform;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author sergekos
 */
public class BabelCompilableTransformer {
    private ScriptEngine jse;
    private CompiledScript compiledScript;

    public String transform(String code) throws ScriptException, NoSuchMethodException {
        if (compiledScript == null) {
            initEngine();
        }

//        SimpleBindings bindings = new SimpleBindings();
//        bindings.put("input", code);
//        bindings.put("lib", compiledScript);
//        Object result = jse.eval("lib.Babel.transform(input, { presets: ['react'] }).code;", bindings);
//        compiledScript.eval(bindings)
        Object result = ((Invocable) compiledScript).invokeFunction("babelTransform", code);

        return (String) result;
    }

    private void initEngine() throws ScriptException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        jse = engineManager.getEngineByName("nashorn");

        InputStream babel = getClass().getResourceAsStream("/babel-6.19.0.js");

        String transformCommand = "function babelTransform(input) { return Babel.transform(input, { presets: ['react'] }).code; }";

        SequenceInputStream full = new java.io.SequenceInputStream(babel, new ByteArrayInputStream(transformCommand.getBytes()));

        compiledScript = ((Compilable) jse).compile(new InputStreamReader(full));
    }
}
