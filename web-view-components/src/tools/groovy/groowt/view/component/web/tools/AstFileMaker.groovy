package groowt.view.component.web.tools

import groovy.transform.InheritConstructors
import groovy.transform.MapConstructor
import groowt.view.component.web.antlr.AntlrUtil
import groowt.view.component.web.antlr.TokenList
import groowt.view.component.web.ast.DefaultAstBuilder
import groowt.view.component.web.ast.DefaultNodeFactory
import groowt.view.component.web.ast.NodeUtil
import groowt.view.component.web.ast.node.Node
import org.jetbrains.annotations.Nullable

import static groowt.view.web.antlr.WebViewComponentsParser.CompilationUnitContext

@InheritConstructors
final class AstFileMaker extends AbstractTreeFileMaker {

    protected sealed interface BuildResult permits BuildSuccess, BuildFailure {}

    @MapConstructor
    protected static class BuildSuccess implements BuildResult {
        Node node
        TokenList tokenList
    }

    @MapConstructor
    protected static class BuildFailure implements BuildResult {
        @Nullable RuntimeException exception
        @Nullable CompilationUnitContext compilationUnitContext
        @Nullable String message
    }

    private void writeFormatted(String name, String formatted) {
        this.outputDirectory.mkdirs()
        def outFile = new File(this.outputDirectory, name + this.suffix + this.extension)
        if (outFile.exists()) {
            if (this.getYesNoInput("$outFile already exists. Write over? (y/n)")) {
                println "Writing to $outFile..."
                outFile.write(formatted)
            } else {
                println "Skipping writing to $outFile."
            }
        } else {
            println "Writing to $outFile..."
            outFile.write(formatted)
        }
    }

    private boolean onSuccess(String name, BuildSuccess buildSuccess) {
        def formatted = NodeUtil.formatAst(buildSuccess.node, buildSuccess.tokenList)
        if (!this.autoYes) {
            println "Please review the following AST:"
            println formatted
        }
        if (this.getYesNoInput('Would you like to write to disk? (y/n)')) {
            this.writeFormatted(name, formatted)
            return true
        } else {
            return !this.getYesNoInput('Do you wish to redo this file? (y/n)')
        }
    }

    private boolean onFailure(String name, BuildFailure buildFailure) {
        if (buildFailure.exception != null) {
            println 'There was an exception during parsing/ast-building:'
            buildFailure.exception.printStackTrace()
        } else if (buildFailure.message != null) {
            println buildFailure.message
        }
        if (this.getYesNoInput('Would you like to try again? (y/n)', true)) {
            println "Re-processing $name..."
            return false
        } else {
            println "Skipping $name after failure."
            return true
        }
    }

    private BuildResult build(File sourceFile) {
        try {
            def parseResult = groowt.view.component.web.antlr.ParserUtil.parseCompilationUnit(sourceFile)

            def cuContext = parseResult.compilationUnitContext

            def errorCollector = AntlrUtil.findErrorNodes(cuContext)
            if (!errorCollector.isEmpty()) {
                def message = 'There were parsing/lexing errors: \n'
                        + errorCollector.all.collect {
                            groowt.view.component.web.antlr.ParserUtil.formatTree(parseResult.parser, it, true) + '\n'
                        }
                return new BuildFailure(
                        compilationUnitContext: cuContext,
                        message: message
                )
            }

            def mismatchedTypeErrors = groowt.view.component.web.analysis.MismatchedComponentTypeAnalysis.check(cuContext)

            if (!mismatchedTypeErrors.isEmpty()) {
                def message = 'There were mismatched type errors: \n' + mismatchedTypeErrors.collect {
                    it.getMessage()
                }.join('\n')
                return new BuildFailure(
                        compilationUnitContext: cuContext,
                        message: message
                )
            }

            def tokenList = new TokenList(parseResult.tokenStream)
            def astBuilder = new DefaultAstBuilder(new DefaultNodeFactory(tokenList))
            return new BuildSuccess(node: astBuilder.build(cuContext), tokenList: tokenList)
        } catch (RuntimeException exception) {
            return new BuildFailure(exception: exception)
        }
    }

    @Override
    void process(File sourceFile) {
        def name = this.getNameWithoutExtension(sourceFile)
        println "Processing $name"
        boolean doneYet = false
        while (!doneYet) {
            def buildResult = this.build(sourceFile)
            if (buildResult instanceof BuildSuccess) {
                doneYet = this.onSuccess(name, buildResult)
            } else {
                doneYet = this.onFailure(name, buildResult as BuildFailure)
            }
        }
    }

}
