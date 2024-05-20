package groowt.view.web.tools

import groovy.transform.InheritConstructors
import groowt.view.web.antlr.*
import groowt.view.web.antlr.AntlrUtil.ParseErrorCollector
import groowt.view.web.antlr.WebViewComponentsParser.CompilationUnitContext
import org.antlr.v4.runtime.CharStreams

@InheritConstructors
final class ParseTreeFileMaker extends AbstractTreeFileMaker {

    private void writeFormatted(String name, WebViewComponentsParser parser, CompilationUnitContext cu) {
        this.outputDirectory.mkdirs()
        def formatted = ParserUtil.formatTree(parser, cu, false)
        def out = new File(this.outputDirectory, name + this.suffix + this.extension)
        if (out.exists()) {
            if (this.getYesNoInput("${out} already exists. Write over? (y/n)")) {
                println "Writing to $out..."
                out.write(formatted)
            } else {
                println "Skipping writing to $out."
            }
        } else {
            println "Writing to $out..."
            out.write(formatted)
        }
    }

    /**
     * @return true if done now, false if not done yet
     */
    private boolean onSuccess(String name, WebViewComponentsParser parser, CompilationUnitContext cu) {
        if (!this.autoYes) {
            println 'Please preview the formatted tree:'
            println ParserUtil.formatTree(parser, cu, true)
        }
        if (this.getYesNoInput('Write to disk? (y/n)')) {
            this.writeFormatted(name, parser, cu)
            return true
        } else {
            return !this.getYesNoInput('Do you wish to redo this file? (y/n)')
        }
    }

    /**
     * @return true if done now, false if not done yet
     */
    private boolean onErrors(String name, WebViewComponentsParser parser, CompilationUnitContext cu, ParseErrorCollector errors) {
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
        def tokenStream = new WebViewComponentsTokenStream(lexer)
        def parser = new WebViewComponentsParser(tokenStream)
        def cu = parser.compilationUnit()
        def errors = AntlrUtil.findErrorNodes(cu)
        new Tuple3<>(parser, cu, errors)
    }

    @Override
    void process(File sourceFile) {
        def name = this.getNameWithoutExtension(sourceFile)
        println "processing: $name"
        boolean doneYet = false
        while (!doneYet) {
            def (parser, cu, errors) = this.parse(sourceFile)
            if (errors.isEmpty()) {
                doneYet = this.onSuccess(name, parser, cu)
            } else {
                doneYet = this.onErrors(name, parser, cu, errors)
            }
        }
    }

}
