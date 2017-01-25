package ru.krdev.maven.babel.plugin.test;

import java.io.File;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import ru.krdev.maven.babel.plugin.BabelTransformMojo;

/**
 *
 * @author sergekos
 */
public class BabelTransformMojoTest {
    @Rule
    public MojoRule rule = new MojoRule();

    @Rule
    public TestResources resources = new TestResources();

    @Test
    public void testSomething() throws Exception {        
        File pom = new File("src/test/resources/prj/test-pom.xml");
        assertTrue("Test POM not found", pom.exists());                                
        
        File fileToTransform = new File("src/test/resources/prj/src/test.jsx");        
        assertTrue("Test JSX file not found", fileToTransform.exists());                                
        
        BabelTransformMojo mojo = (BabelTransformMojo) rule.lookupMojo("transform", pom);        
        assertNotNull("Mojo lookup failed", mojo);                
        
        mojo.execute();

        File outputFile = new File("target/test/resources/prj/src/test.jsx");        
        assertTrue("Output file not found", outputFile.exists());
        
        outputFile.delete();
    }
}
