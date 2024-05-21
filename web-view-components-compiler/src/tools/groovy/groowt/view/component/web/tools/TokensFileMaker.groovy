package groowt.view.component.web.tools

import groovy.transform.InheritConstructors
import groowt.view.component.web.antlr.*
import groowt.view.component.web.util.ExtensionUtil
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.ConsoleErrorListener
import org.antlr.v4.runtime.Token

@InheritConstructors
class TokensFileMaker extends AbstractOutputFileMaker {

    protected boolean onSuccess(String name, List<Token> allTokens) {
        def formatted = allTokens.collect(TokenUtil.&formatToken).join('\n')
        if (!this.autoYes) {
            println 'Please review the following tokens:'
            println formatted
        }
        if (this.getYesNoInput('Write to disk? (y/n)')) {
            this.writeToDisk(name, formatted)
            return true
        } else {
            return !this.getYesNoInput('Do you wish to redo this file? (y/n)')
        }
    }

    protected boolean onLexerErrors(String name, List<LexerError> errors) {
        println "There were lexer errors in $name."
        errors.each { println LexerError.format(it) }
        if (this.getYesNoInput('Do you wish to try again? (y/n)', true)) {
            println "Trying $name again..."
            return false
        } else {
            println "Skipping $name..."
            return true
        }
    }

    protected boolean onException(String name, Exception e) {
        println "There was an exception while tokenizing $name: $e.message"
        e.printStackTrace()
        if (this.getYesNoInput('Do you wish to try again? (y/n)', true)) {
            println "Trying $name again..."
            return false
        } else {
            println "Skipping $name."
            return true
        }
    }

    @Override
    void process(File sourceFile) {
        def name = ExtensionUtil.getNameWithoutExtension(sourceFile)
        println "Processing: $name"
        boolean doneYet = false
        while (!doneYet) {
            try {
                def input = CharStreams.fromString(sourceFile.getText())
                def lexer = new WebViewComponentsLexer(input)
                lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
                def lexerErrorListener = new LexerErrorListener()
                lexer.addErrorListener(lexerErrorListener)
                def tokenStream = new WebViewComponentsTokenStream(lexer)
                def allTokens = tokenStream.getAllTokensSkipEOF()
                if (!lexerErrorListener.errors.isEmpty()) {

                } else {
                    doneYet = this.onSuccess(name, allTokens)
                }
            } catch (Exception e) {
                doneYet = this.onException(name, e)
            }
        }
    }

}
