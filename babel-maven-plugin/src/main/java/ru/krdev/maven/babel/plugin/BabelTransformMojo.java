package ru.krdev.maven.babel.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import javax.script.ScriptException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import ru.krdev.babel.transform.BabelTransformer;

/**
 *
 * @author sergekos
 */
@Mojo(name = "transform")
public class BabelTransformMojo extends AbstractMojo {
    @Parameter(required = true, alias = "fileset")
    private FileSet sourceFileSet;

    @Parameter(required = true)
    private String outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        FileSetManager fileSetManager = new FileSetManager();
        String[] includedFiles = fileSetManager.getIncludedFiles(sourceFileSet);

        BabelTransformer babel = new BabelTransformer();

        int transformCount = 0;
        int errorCount = 0;

        for (String f : includedFiles) {
            File srcFile = new File(sourceFileSet.getDirectory(), f);
            File targetFile = new File(outputDirectory, f);

            if (targetFile.exists() && targetFile.lastModified() > srcFile.lastModified()) {
                getLog().debug("Not modified, skipping: " + srcFile.getAbsolutePath());
                continue;
            }

            transformCount++;

            getLog().debug("Transforming: " + srcFile.getAbsolutePath());

            try {
                String content = new String(Files.readAllBytes(srcFile.toPath()));
                String output = babel.transform(content);

                if (!targetFile.getParentFile().isDirectory() || !targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }

                getLog().debug("Writing: " + targetFile.getAbsolutePath());

                Files.write(
                        targetFile.toPath(),
                        output.getBytes(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);

            } catch (IOException | ScriptException ex) {
                getLog().error("Transforming error: " + srcFile.getAbsolutePath() + "\n" + ex.getMessage());
                errorCount++;
            }
        }

        if (transformCount == 0) {
            getLog().info("Nothing to transform");

        } else if (errorCount > 0) {
            throw new MojoFailureException("Error(s) occured while babel transforming");

        } else {
            getLog().info("Transformed " + transformCount + (transformCount > 1 ? " resources" : " resource"));
        }
    }
}
