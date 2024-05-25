package groowt.view.component.compiler.source;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.util.List;

public class FileSource implements ComponentTemplateSource {

    private final File templateFile;
    private List<String> lines;

    public FileSource(File templateFile) {
        this.templateFile = templateFile;
    }

    @Override
    public Reader toReader() throws Exception {
        return new FileReader(this.templateFile);
    }

    @Override
    public String getDescriptiveName() {
        return this.templateFile.toString();
    }

    @Override
    public boolean canReopen() {
        return true;
    }

    @Override
    public List<String> getLines() {
        if (this.lines == null) {
            try (final var fis = new FileInputStream(this.templateFile)) {
                final var allSource = new String(fis.readAllBytes());
                this.lines = allSource.lines().toList();
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }
        return this.lines;
    }

    public @Nullable URI getURI() {
        return templateFile.toURI();
    }

}
