package groowt.view.component.web.ast;

import groowt.view.component.web.WebViewComponentBugError;
import groowt.view.component.web.antlr.MergedGroovyCodeToken;
import groowt.view.component.web.antlr.TokenUtil;
import groowt.view.component.web.antlr.WebViewComponentsParser;
import groowt.view.component.web.antlr.WebViewComponentsParser.BodyTextContext;
import groowt.view.component.web.antlr.WebViewComponentsParserBaseVisitor;
import groowt.view.component.web.ast.node.*;
import groowt.view.component.web.util.TokenRange;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultAstBuilderVisitor extends WebViewComponentsParserBaseVisitor<Node> {

    protected static boolean isNotBlankNotEmpty(@NotNull String subject) {
        return !subject.isEmpty() && !subject.isBlank();
    }

    private final NodeFactory nodeFactory;

    public DefaultAstBuilderVisitor(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    @Override
    public Node visitChildren(RuleNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Node defaultResult() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Node aggregateResult(Node aggregate, Node nextResult) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean shouldVisitNextChild(RuleNode node, Node currentResult) {
        throw new UnsupportedOperationException();
    }

    protected @Nullable Node getSingle(@Nullable ParserRuleContext ctx) {
        return ctx != null ? ctx.accept(this) : null;
    }

    protected <R extends Node> @Nullable R getSingleAs(@Nullable ParserRuleContext ctx, @NotNull Class<R> type) {
        final Node result = this.getSingle(ctx);
        return result != null ? type.cast(result) : null;
    }

    protected @NotNull Node getSingleNonNull(@NotNull ParserRuleContext ctx) {
        return Objects.requireNonNull(Objects.requireNonNull(ctx).accept(this));
    }

    protected <R extends Node> @NotNull R getSingleAsNonNull(@NotNull ParserRuleContext ctx, @NotNull Class<R> type) {
        return type.cast(this.getSingleNonNull(ctx));
    }

    protected Node getSingleChild(ParserRuleContext ctx) {
        return ctx.getChild(0).accept(this);
    }

    protected TokenRange getTokenRange(ParserRuleContext ctx) {
        return TokenRange.of(ctx.start, ctx.stop);
    }

    @Override
    public Node visitCompilationUnit(WebViewComponentsParser.CompilationUnitContext ctx) {
        final PreambleNode preamble = this.getSingleAs(ctx.preamble(), PreambleNode.class);
        final BodyNode body = this.getSingleAs(ctx.body(), BodyNode.class);
        return this.nodeFactory.compilationUnitNode(this.getTokenRange(ctx), preamble, body);
    }

    @Override
    public @Nullable Node visitPreamble(WebViewComponentsParser.PreambleContext ctx) {
        final @Nullable TerminalNode groovyCode = ctx.GroovyCode();
        if (groovyCode != null && isNotBlankNotEmpty(groovyCode.getText())) {
            return this.nodeFactory.preambleNode(this.getTokenRange(ctx), groovyCode.getSymbol().getTokenIndex());
        } else {
            return null;
        }
    }

    @Override
    public @Nullable Node visitBody(WebViewComponentsParser.BodyContext ctx) {
        final List<BodyChildNode> children = new ArrayList<>();
        for (final var child : ctx.children) {
            final BodyChildNode bodyChildNode = (BodyChildNode) child.accept(this);
            if (bodyChildNode != null) {
                children.add(bodyChildNode);
            }
        }
        if (children.isEmpty()) {
            return null;
        } else {
            return this.nodeFactory.bodyNode(this.getTokenRange(ctx), children);
        }
    }

    @Override
    public @Nullable Node visitBodyText(BodyTextContext ctx) {
        final List<BodyTextChild> children = new ArrayList<>();
        for (final var child : ctx.children) {
            final @Nullable Node childResult = child.accept(this);
            if (childResult != null) {
                children.add((BodyTextChild) childResult);
            }
        }
        if (children.isEmpty()) {
            return null;
        } else {
            return this.nodeFactory.bodyTextNode(this.getTokenRange(ctx), children);
        }
    }

    @Override
    public @Nullable Node visitQuestionTag(WebViewComponentsParser.QuestionTagContext ctx) {
        final List<QuestionTagChild> children = new ArrayList<>();
        for (final var child : ctx.children) {
            final @Nullable Node childResult = child.accept(this);
            if (childResult != null) {
                children.add((QuestionTagChild) childResult);
            }
        }
        return this.nodeFactory.questionTagNode(
                this.getTokenRange(ctx),
                ctx.QuestionTagOpen().getSymbol(),
                ctx.QuestionTagClose().getSymbol(),
                children
        );
    }

    @Override
    public Node visitHtmlComment(WebViewComponentsParser.HtmlCommentContext ctx) {
        final List<HtmlCommentChild> children = new ArrayList<>();
        for (final var child : ctx.children) {
            final @Nullable Node childResult = child.accept(this);
            if (childResult != null) {
                children.add((HtmlCommentChild) childResult);
            }
        }
        return this.nodeFactory.htmlCommentNode(
                this.getTokenRange(ctx),
                ctx.HtmlCommentOpen().getSymbol(),
                ctx.HtmlCommentClose().getSymbol(),
                children
        );
    }

    @Override
    public @Nullable Node visitText(WebViewComponentsParser.TextContext ctx) {
        final String content = ctx.getText();
        if (isNotBlankNotEmpty(content)) {
            return this.nodeFactory.textNode(this.getTokenRange(ctx), content);
        } else {
            return null;
        }
    }

    @Override
    public Node visitBodyTextGroovyElement(WebViewComponentsParser.BodyTextGroovyElementContext ctx) {
        return this.getSingleChild(ctx);
    }

    @Override
    public Node visitComponent(WebViewComponentsParser.ComponentContext ctx) {
        return this.getSingleChild(ctx);
    }

    @Override
    public Node visitSelfClosingComponent(WebViewComponentsParser.SelfClosingComponentContext ctx) {
        return this.nodeFactory.typedComponentNode(
                this.getTokenRange(ctx),
                this.getSingleAs(ctx.componentArgs(), ComponentArgsNode.class),
                null
        );
    }

    @Override
    public Node visitComponentWithChildren(WebViewComponentsParser.ComponentWithChildrenContext ctx) {
        return this.nodeFactory.typedComponentNode(
                this.getTokenRange(ctx),
                this.getSingleAs(ctx.openComponent().componentArgs(), ComponentArgsNode.class),
                this.getSingleAs(ctx.body(), BodyNode.class)
        );
    }

    @Override
    public Node visitOpenComponent(WebViewComponentsParser.OpenComponentContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node visitClosingComponent(WebViewComponentsParser.ClosingComponentContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node visitFragmentComponent(WebViewComponentsParser.FragmentComponentContext ctx) {
        return this.nodeFactory.fragmentComponentNode(
                this.getTokenRange(ctx),
                this.getSingleAsNonNull(ctx.body(), BodyNode.class)
        );
    }

    @Override
    public Node visitComponentArgs(WebViewComponentsParser.ComponentArgsContext ctx) {
        final ComponentTypeNode typeNode = this.getSingleAs(ctx.componentType(), ComponentTypeNode.class);
        final @Nullable ComponentConstructorNode constructorNode = this.getSingleAs(
                ctx.componentConstructor(),
                ComponentConstructorNode.class
        );
        final List<AttrNode> attrNodes = new ArrayList<>();
        for (final var attrCtx : ctx.attr()) {
            attrNodes.add(this.getSingleAsNonNull(attrCtx, AttrNode.class));
        }
        return this.nodeFactory.componentArgsNode(this.getTokenRange(ctx), typeNode, constructorNode, attrNodes);
    }

    @Override
    public Node visitComponentType(WebViewComponentsParser.ComponentTypeContext ctx) {
        final var typedIdentifier = ctx.TypedIdentifier();
        if (typedIdentifier != null) {
            return this.nodeFactory.classComponentTypeNode(this.getTokenRange(ctx));
        }
        final var stringIdentifier = ctx.StringIdentifier();
        if (stringIdentifier != null) {
            return this.nodeFactory.stringComponentTypeNode(
                    this.getTokenRange(ctx),
                    stringIdentifier.getSymbol().getTokenIndex()
            );
        }
        throw new WebViewComponentBugError("Could not determine type of " + ctx);
    }

    @Override
    public @Nullable Node visitComponentConstructor(WebViewComponentsParser.ComponentConstructorContext ctx) {
        final TerminalNode groovyCode = ctx.GroovyCode();
        if (groovyCode != null) {
            final String rawCode = groovyCode.getText();
            if (isNotBlankNotEmpty(rawCode)) {
                return this.nodeFactory.componentConstructorNode(
                        this.getTokenRange(ctx),
                        ctx.GroovyCode().getSymbol().getTokenIndex()
                );
            }
        }
        return null;
    }

    @Override
    public Node visitAttr(WebViewComponentsParser.AttrContext ctx) {
        return this.getSingleChild(ctx);
    }

    @Override
    public Node visitKeyValueAttr(WebViewComponentsParser.KeyValueAttrContext ctx) {
        final TerminalNode identifier = ctx.AttributeIdentifier();
        final KeyNode keyNode = this.nodeFactory.keyNode(TokenRange.of(
                identifier.getSymbol(),
                ctx.Equals().getSymbol()
        ), identifier.getSymbol().getTokenIndex());
        final ValueNode valueNode = (ValueNode) ctx.value().accept(this);
        return this.nodeFactory.keyValueAttrNode(this.getTokenRange(ctx), keyNode, valueNode);
    }

    @Override
    public Node visitBooleanAttr(WebViewComponentsParser.BooleanAttrContext ctx) {
        final Token identifierToken = ctx.AttributeIdentifier().getSymbol();
        final KeyNode keyNode = this.nodeFactory.keyNode(
                TokenRange.of(identifierToken),
                identifierToken.getTokenIndex()
        );
        return this.nodeFactory.booleanValueAttrNode(this.getTokenRange(ctx), keyNode);
    }

    @Override
    public Node visitValue(WebViewComponentsParser.ValueContext ctx) {
        return this.getSingleChild(ctx);
    }

    protected static boolean canBeGString(List<? extends Token> originalGroovyTokens) {
        return originalGroovyTokens.stream().anyMatch(TokenUtil::isGStringPart);
    }

    @Override
    public Node visitGStringAttrValue(WebViewComponentsParser.GStringAttrValueContext ctx) {
        final TerminalNode groovyCode = ctx.GroovyCode();
        final TokenRange ctxTokenRange = this.getTokenRange(ctx);
        if (groovyCode != null) {
            final MergedGroovyCodeToken groovyCodeToken = (MergedGroovyCodeToken) groovyCode.getSymbol();
            if (canBeGString(groovyCodeToken.getOriginals())) {
                return this.nodeFactory.gStringValueNode(ctxTokenRange, groovyCodeToken.getTokenIndex());
            } else {
                return this.nodeFactory.jStringValueNode(ctxTokenRange, groovyCode.getText());
            }
        } else {
            return this.nodeFactory.jStringValueNode(ctxTokenRange, "");
        }
    }

    @Override
    public Node visitJStringAttrValue(WebViewComponentsParser.JStringAttrValueContext ctx) {
        final TerminalNode groovyCode = ctx.GroovyCode();
        final TokenRange ctxTokenRange = this.getTokenRange(ctx);
        if (groovyCode != null) {
            return this.nodeFactory.jStringValueNode(ctxTokenRange, groovyCode.getText());
        } else {
            return this.nodeFactory.jStringValueNode(ctxTokenRange, "");
        }
    }

    @Override
    public Node visitClosureAttrValue(WebViewComponentsParser.ClosureAttrValueContext ctx) {
        final TerminalNode groovyCode = ctx.GroovyCode();
        final TokenRange ctxTokenRange = this.getTokenRange(ctx);
        if (groovyCode != null) {
            final String rawCode = groovyCode.getText();
            if (!(rawCode.isEmpty() || rawCode.isBlank())) {
                return this.nodeFactory.closureValueNode(ctxTokenRange, groovyCode.getSymbol().getTokenIndex());
            }
        }
        return this.nodeFactory.emptyClosureValueNode(ctxTokenRange);
    }

    @Override
    public Node visitComponentAttrValue(WebViewComponentsParser.ComponentAttrValueContext ctx) {
        return this.nodeFactory.componentValueNode(
                this.getTokenRange(ctx),
                (ComponentNode) ctx.component().accept(this)
        );
    }

    @Override
    public @Nullable Node visitEqualsScriptlet(WebViewComponentsParser.EqualsScriptletContext ctx) {
        final TerminalNode groovyCode = ctx.GroovyCode();
        if (groovyCode != null) {
            return this.nodeFactory.equalsScriptletNode(
                    this.getTokenRange(ctx),
                    ctx.GroovyCode().getSymbol().getText()
            );
        } else {
            return null;
        }
    }

    @Override
    public @Nullable Node visitPlainScriptlet(WebViewComponentsParser.PlainScriptletContext ctx) {
        final TerminalNode groovyCode = ctx.GroovyCode();
        if (groovyCode != null) {
            return this.nodeFactory.plainScriptletNode(this.getTokenRange(ctx), groovyCode.getSymbol().getText());
        } else {
            return null;
        }
    }

    @Override
    public @Nullable Node visitDollarScriptlet(WebViewComponentsParser.DollarScriptletContext ctx) {
        final TerminalNode groovyCode = ctx.GroovyCode();
        if (groovyCode != null) {
            return this.nodeFactory.dollarScriptletNode(
                    this.getTokenRange(ctx),
                    groovyCode.getSymbol().getText()
            );
        } else {
            return null;
        }
    }

    @Override
    public Node visitDollarReference(WebViewComponentsParser.DollarReferenceContext ctx) {
        final String groovyCode = ctx.GroovyCode().getText();
        final List<String> parts = new ArrayList<>();
        if (groovyCode.contains(".")) {
            parts.addAll(List.of(groovyCode.split("\\.")));
        } else {
            parts.add(groovyCode);
        }
        return this.nodeFactory.dollarReferenceNode(this.getTokenRange(ctx), parts);
    }

    @Override
    public Node visitTerminal(TerminalNode node) {
        throw new WebViewComponentBugError("Should not be visiting terminal nodes.");
    }

    @Override
    public Node visitErrorNode(ErrorNode node) {
        throw new WebViewComponentBugError("Should not have found an ErrorNode by this point: " + node);
    }

}
