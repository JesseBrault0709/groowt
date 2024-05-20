package groowt.view.component.web.tools

import groovy.transform.InheritConstructors
import groowt.view.component.web.antlr.TokenUtil
import groowt.view.component.web.antlr.WebViewComponentsLexer
import groowt.view.component.web.antlr.WebViewComponentsTokenStream
import groowt.view.component.web.util.ExtensionUtil
import org.antlr.v4.runtime.CharStreams
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
                def tokenStream = new WebViewComponentsTokenStream(lexer)
                doneYet = this.onSuccess(name, tokenStream.getAllTokensSkipEOF())
            } catch (Exception e) {
                doneYet = this.onException(name, e)
            }
        }
    }

}
