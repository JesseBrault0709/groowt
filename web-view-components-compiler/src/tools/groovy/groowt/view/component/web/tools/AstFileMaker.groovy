package groowt.view.component.web.tools

import groovy.transform.InheritConstructors
import groovy.transform.MapConstructor
import groowt.view.component.web.analysis.MismatchedComponentTypeAnalysis
import groowt.view.component.web.antlr.AntlrUtil
import groowt.view.component.web.antlr.ParserUtil
import groowt.view.component.web.antlr.TokenList
import groowt.view.component.web.antlr.WebViewComponentsParser
import groowt.view.component.web.ast.DefaultAstBuilder
import groowt.view.component.web.ast.DefaultNodeFactory
import groowt.view.component.web.ast.NodeUtil
import groowt.view.component.web.ast.node.Node
import groowt.view.component.web.util.ExtensionUtil
import org.jetbrains.annotations.Nullable

@InheritConstructors
final class AstFileMaker extends AbstractOutputFileMaker {

    protected sealed interface BuildResult permits BuildSuccess, BuildFailure {}

    @MapConstructor
    protected static class BuildSuccess implements BuildResult {
        Node node
        TokenList tokenList
    }

    @MapConstructor
    protected static class BuildFailure implements BuildResult {
        @Nullable RuntimeException exception
        @Nullable WebViewComponentsParser.CompilationUnitContext compilationUnitContext
        @Nullable String message
    }

    private boolean onSuccess(String name, BuildSuccess buildSuccess) {
        def formatted = NodeUtil.formatAst(buildSuccess.node, buildSuccess.tokenList)
        if (!this.autoYes) {
            println "Please review the following AST:"
            println formatted
        }
        if (this.getYesNoInput('Would you like to write to disk? (y/n)')) {
            this.writeToDisk(name, formatted)
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
            def parseResult = ParserUtil.parseCompilationUnit(sourceFile)

            def cuContext = parseResult.compilationUnitContext

            def errorCollector = AntlrUtil.findErrorNodes(cuContext)
            if (!errorCollector.isEmpty()) {
                def message = 'There were parsing/lexing errors: \n'
                        + errorCollector.all.collect {
                            ParserUtil.formatTree(parseResult.parser, it, true) + '\n'
                        }
                return new BuildFailure(
                        compilationUnitContext: cuContext,
                        message: message
                )
            }

            def mismatchedTypeErrors = MismatchedComponentTypeAnalysis.checkForMismatchedComponentTypeErrors(cuContext)

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
        def name = ExtensionUtil.getNameWithoutExtension(sourceFile)
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
