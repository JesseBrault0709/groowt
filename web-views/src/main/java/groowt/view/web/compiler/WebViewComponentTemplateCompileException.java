package groowt.view.web.compiler;

import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.compiler.ComponentTemplateCompileUnit;
import groowt.view.web.antlr.TokenUtil;
import groowt.view.web.ast.node.Node;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.Nullable;

public class WebViewComponentTemplateCompileException extends ComponentTemplateCompileException {

    private @Nullable TerminalNode terminalNode;
    private @Nullable ParserRuleContext parserRuleContext;
    private @Nullable Node node;

    public WebViewComponentTemplateCompileException(
            ComponentTemplateCompileUnit compileUnit,
            String message
    ) {
        super(compileUnit, message);
    }

    public WebViewComponentTemplateCompileException(
            ComponentTemplateCompileUnit compileUnit,
            String message,
            Throwable cause
    ) {
        super(compileUnit, message, cause);
    }

    public WebViewComponentTemplateCompileException(
            ComponentTemplateCompileUnit compileUnit,
            Throwable cause
    ) {
        super(compileUnit, "There was an error during compilation.", cause);
    }

    public @Nullable TerminalNode getTerminalNode() {
        return this.terminalNode;
    }

    public void setTerminalNode(@Nullable TerminalNode terminalNode) {
        this.terminalNode = terminalNode;
    }

    public @Nullable ParserRuleContext getParserRuleContext() {
        return this.parserRuleContext;
    }

    public void setParserRuleContext(@Nullable ParserRuleContext parserRuleContext) {
        this.parserRuleContext = parserRuleContext;
    }

    public @Nullable Node getNode() {
        return this.node;
    }

    public void setNode(@Nullable Node node) {
        this.node = node;
    }

    @Override
    protected @Nullable String getPosition() {
        if (this.node != null) {
            return this.node.getTokenRange().getStartPosition().toStringLong();
        } else if (this.parserRuleContext != null) {
            return TokenUtil.formatTokenPosition(this.parserRuleContext.start);
        } else if (this.terminalNode != null) {
            return TokenUtil.formatTokenPosition(terminalNode.getSymbol());
        } else {
            return null;
        }
    }

}
