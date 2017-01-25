package ru.krdev.babel.transform.test;

import javax.script.ScriptException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.krdev.babel.transform.BabelTransformer;

/**
 *
 * @author sergekos
 */
public class BabelTransformerTest {
    private static BabelTransformer transformer; 
    
    @BeforeClass
    public static void init() {
        transformer = new BabelTransformer(true);
    }

    @Test(expected = ScriptException.class)
    public void errorTest() throws ScriptException {
        transformer.transform("() => <Component name='Тест'>");
    }

    @Test
    public void transformTest() throws ScriptException {
        final String inputJsx = "() => <Component name='Тест'/>";
        final String expectedJs = "'use strict';(function(){return React.createElement(Component,{name:'\\u0422\\u0435\\u0441\\u0442'})});";

        String result = transformer.transform(inputJsx);

        Assert.assertEquals("Unexpected result JS", expectedJs, result);
    }       
}
