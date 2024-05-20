package groowt.view.component.web.transpile;

import groowt.view.component.web.util.Range;
import org.codehaus.groovy.control.HasCleanup;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.io.ReaderSource;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Deprecated
public class WebViewComponentReaderSource implements ReaderSource {

    public record LineAndColumnRange(LineAndColumn start, LineAndColumn end) {}

    public record LineAndColumn(int line, int column) {}

    public static final LineAndColumn minusOneLineAndColumn = new LineAndColumn(-1, -1);
    public static final LineAndColumnRange minusOneLineAndColumnRange = new LineAndColumnRange(
            minusOneLineAndColumn,
            minusOneLineAndColumn
    );

    protected interface Source {
        Reader toReader() throws IOException;
        @Nullable String getLine(int lineNumber);
        @Nullable URI toUri();
        LineAndColumnRange convertToLineAndColumnRange(Range<Integer> range);
        LineAndColumn convertToLineAndColumn(int index);
    }

    protected static abstract class AbstractSource implements Source {

        @Override
        public LineAndColumnRange convertToLineAndColumnRange(Range<Integer> range) {
            try (final Reader reader = new BufferedReader(this.toReader())) {
                int read = reader.read();
                boolean enteredRangeYet = false;
                int startLine = -1;
                int startColumn = -1;
                int index = 0;
                int curLine = 0;
                int curColumn = 1;
                while (read > -1) {
                    if ((char) read == '\n') {
                        reader.mark(1);
                        final int next = reader.read();
                        if ((char) next != '\r') {
                            reader.reset();
                        }
                        curLine++;
                        curColumn = 1;
                    } else if ((char) read == '\r') {
                        curLine++;
                        curColumn = 1;
                    }
                    if (!enteredRangeYet && range.isInRange(index)) {
                        enteredRangeYet = true;
                        startLine = curLine;
                        startColumn = curColumn;
                    } else if (enteredRangeYet && !range.isInRange(index)) {
                        break;
                    }
                    read = reader.read();
                    if (read > -1) {
                        index++;
                        curColumn++;
                    }
                }
                return new LineAndColumnRange(
                        new LineAndColumn(startLine, startColumn),
                        new LineAndColumn(curLine, curColumn)
                );
            } catch (IOException ignored) {
                return minusOneLineAndColumnRange;
            } finally {
                if (this instanceof HasCleanup hasCleanup) {
                    hasCleanup.cleanup();
                }
            }
        }

        @Override
        public LineAndColumn convertToLineAndColumn(final int index) {
            try (final Reader reader = new BufferedReader(this.toReader())) {
                int read = reader.read();
                int currentIndex = 0;
                int currentLine = 0;
                int currentColumn = 1;
                while (read > -1 && currentIndex <= index) {
                    if ((char) read == '\n') {
                        reader.mark(1);
                        final int next = reader.read();
                        if ((char) next != '\r') {
                            reader.reset();
                        }
                        currentLine++;
                        currentColumn = 1;
                    } else if ((char) read == '\r') {
                        currentLine++;
                        currentColumn = 1;
                    }
                    read = reader.read();
                    if (read > -1) {
                        currentIndex++;
                        currentColumn++;
                    }
                }
                return new LineAndColumn(currentLine, currentColumn);
            } catch (IOException ignored) {
                return minusOneLineAndColumn;
            } finally {
                if (this instanceof HasCleanup hasCleanup) {
                    hasCleanup.cleanup();
                }
            }
        }

    }

    protected static abstract class AbstractSourceWithHasCleanup extends AbstractSource implements HasCleanup {

        private final Collection<Reader> createdReaders = new ArrayList<>();

        protected void addReader(Reader reader) {
            this.createdReaders.add(reader);
        }

        @Override
        public void cleanup() {
            this.createdReaders.forEach(reader -> {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            });
        }

    }

    protected static final class FileSource extends AbstractSourceWithHasCleanup {

        private final File source;
        @Nullable
        private List<String> cachedLines;
        private boolean triedFetchingLines;

        public FileSource(File source) {
            this.source = source;
        }

        @Override
        public Reader toReader() throws IOException {
            final Reader r = new FileReader(this.source);
            this.addReader(r);
            return r;
        }

        private void initCachedLines() {
            try (final FileInputStream inputStream = new FileInputStream(this.source)) {
                final byte[] allBytes = inputStream.readAllBytes();
                final String allSource = new String(allBytes);
                this.cachedLines = allSource.lines().toList();
            } catch (IOException ignored) {
                // ignored
            } finally {
                this.triedFetchingLines = true;
            }
        }

        @Override
        public @Nullable String getLine(int lineNumber) {
            if (!this.triedFetchingLines) {
                this.initCachedLines();
            }
            return this.cachedLines != null ? this.cachedLines.get(lineNumber) : null;
        }

        @Override
        public URI toUri() {
            return this.source.toURI();
        }

    }

    protected static final class StringSource extends AbstractSourceWithHasCleanup {

        private final String source;
        private List<String> cachedLines;

        public StringSource(String source) {
            this.source = source;
        }

        @Override
        public Reader toReader() throws IOException {
            final Reader r = new StringReader(this.source);
            this.addReader(r);
            return r;
        }

        @Override
        public String getLine(int lineNumber) {
            if (this.cachedLines == null) {
                this.cachedLines = this.source.lines().toList();
            }
            return this.cachedLines.get(lineNumber);
        }

        @Override
        public @Nullable URI toUri() {
            return null;
        }

    }

    private final Source source;

    public WebViewComponentReaderSource(File sourceFile) {
        this.source = new FileSource(sourceFile);
    }

    public WebViewComponentReaderSource(String sourceString) {
        this.source = new StringSource(sourceString);
    }

    @Override
    public Reader getReader() throws IOException {
        return this.source.toReader();
    }

    @Override
    public boolean canReopenSource() {
        return true;
    }

    @Override
    public String getLine(int lineNumber, Janitor janitor) {
        if (lineNumber < 0) {
            return null;
        }
        final var line = this.source.getLine(lineNumber);
        if (this.source instanceof HasCleanup hasCleanup) {
            janitor.register(hasCleanup);
        }
        return line;
    }

    @Override
    public void cleanup() {
        if (this.source instanceof HasCleanup hasCleanup) {
            hasCleanup.cleanup();
        }
    }

    @Override
    public URI getURI() {
        return this.source.toUri();
    }

}
