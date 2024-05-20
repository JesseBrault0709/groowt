package groowt.view.component.web.ast;

import groowt.view.component.web.antlr.TokenList;
import groowt.view.component.web.ast.node.Node;
import groowt.view.component.web.testutil.FileComparisonTestUtil;
import groowt.view.component.web.util.ExtensionUtil;
import groowt.view.component.web.util.FileUtil;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Path;
import java.util.Collection;

public abstract class AstBuilderTests {

    protected record BuildResult(Node node, TokenList tokenList) {}

    private final Path sourceFileDir;
    private final String sourceFileGlob;
    private final Path astTreeDir;
    private final String astFileSuffixAndExt;

    public AstBuilderTests(Path sourceFileDir, String sourceFileGlob, Path astTreeDir, String astFileSuffixAndExt) {
        this.sourceFileDir = sourceFileDir;
        this.sourceFileGlob = sourceFileGlob;
        this.astTreeDir = astTreeDir;
        this.astFileSuffixAndExt = astFileSuffixAndExt;
    }

    protected abstract BuildResult buildFromSource(String source);

    protected abstract String format(BuildResult buildResult);

    @TestFactory
    public Collection<DynamicTest> getSourceFileTests() {
        return FileComparisonTestUtil.getTestsFor(
                this.sourceFileDir,
                this.sourceFileGlob,
                this.astTreeDir,
                sourcePath -> {
                    final String nameWithoutExtension = ExtensionUtil.getNameWithoutExtension(sourcePath);
                    return Path.of(nameWithoutExtension + this.astFileSuffixAndExt);
                },
                sourceFile -> {
                    final BuildResult buildResult = this.buildFromSource(FileUtil.readFile(sourceFile));
                    return this.format(buildResult);
                }
        );
    }

}
