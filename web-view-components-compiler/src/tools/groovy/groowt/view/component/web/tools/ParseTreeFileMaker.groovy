package groowt.view.component.web.tools

import groovy.transform.InheritConstructors
import groowt.view.component.web.antlr.*
import groowt.view.component.web.antlr.AntlrUtil.ParseErrorCollector
import groowt.view.component.web.antlr.WebViewComponentsParser.CompilationUnitContext
import groowt.view.component.web.util.ExtensionUtil
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.ConsoleErrorListener

@InheritConstructors
final class ParseTreeFileMaker extends AbstractOutputFileMaker {

    /**
     * @return true if done now, false if not done yet
     */
    private boolean onSuccess(
            String name,
            WebViewComponentsParser parser,
            WebViewComponentsParser.CompilationUnitContext cu
    ) {
        if (!this.autoYes) {
            println 'Please preview the formatted tree:'
            println ParserUtil.formatTree(parser, cu, true)
        }
        if (this.getYesNoInput('Write to disk? (y/n)')) {
            def formatted = ParserUtil.formatTree(parser, cu, false)
            this.writeToDisk(name, formatted)
            return true
        } else {
            return !this.getYesNoInput('Do you wish to redo this file? (y/n)')
        }
    }

    /**
     * @return true if done now, false if not done yet
     */
    private boolean onErrors(
            String name,
            WebViewComponentsParser parser,
            CompilationUnitContext cu,
            ParseErrorCollector errors
    ) {
        def errorCount = errors.errorCount
        def isOne = errorCount == 1
        def formatted = ParserUtil.formatTree(parser, cu, true)
        println "There ${isOne ? 'was' : 'were'} ${errorCount} error${isOne ? '' : 's'} during parsing:"
        println formatted
        if (this.getYesNoInput('Do you wish to try again? (y/n)', true)) {
            println "trying $name again..."
            return false
        } else {
            println "skipping $name..."
            return true
        }
    }

    private Tuple3<WebViewComponentsParser, CompilationUnitContext, ParseErrorCollector> parse(File sourceFile) {
        def input = CharStreams.fromFileName(sourceFile.toString())

        def lexer = new WebViewComponentsLexer(input)
        def lexerErrorListener = new LexerErrorListener()
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
        lexer.addErrorListener(lexerErrorListener)

        def tokenStream = new WebViewComponentsTokenStream(lexer)

        def parser = new WebViewComponentsParser(tokenStream)
        def parserErrorListener = new ParserErrorListener()
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE)
        parser.addErrorListener(parserErrorListener)

        def cu = parser.compilationUnit()

        if (!lexerErrorListener.errors.isEmpty()) {
            println 'There were lexer errors.'
            lexerErrorListener.errors.each { println LexerErrorKt.formatLexerError(it) }
            return null
        }

        if (!parserErrorListener.parserErrors.isEmpty()) {
            println 'There were parser errors.'
            parserErrorListener.parserErrors.each { println ParserErrorKt.formatParserError(it) }
            return null
        }

        def errors = AntlrUtil.findErrorNodes(cu)
        new Tuple3<>(parser, cu, errors)
    }

    @Override
    void process(File sourceFile) {
        def name = ExtensionUtil.getNameWithoutExtension(sourceFile)
        println "Processing: $name"
        boolean doneYet = false
        while (!doneYet) {
            final WebViewComponentsParser parser
            final CompilationUnitContext cu
            final ParseErrorCollector errors
            try {
                def result = this.parse(sourceFile)
                if (result != null) {
                    (parser, cu, errors) = result
                    if (errors.isEmpty()) {
                        doneYet = this.onSuccess(name, parser, cu)
                    } else {
                        doneYet = this.onErrors(name, parser, cu, errors)
                    }
                } else {
                    doneYet = !this.getYesNoInput('Would you like to try again? (y/n)', true)
                }
            } catch (Exception e) {
                println "There was an exception: $e"
                if (this.verbose) {
                    e.printStackTrace()
                }
                doneYet = !this.getYesNoInput('Would you like to try again? (y/n)', true)
            }
        }
    }

}
