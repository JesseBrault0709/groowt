package groowt.view.web.ast;

import groowt.view.web.antlr.MergedGroovyCodeToken;
import groowt.view.web.antlr.TokenUtil;
import groowt.view.web.antlr.WebViewComponentsParser;
import groowt.view.web.antlr.WebViewComponentsParserBaseVisitor;
import groowt.view.web.ast.node.*;
import groowt.view.web.util.TokenRange;
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
import java.util.regex.Pattern;

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

    protected <R extends Node> R getSingleChildAs(ParserRuleContext ctx, Class<R> type) {
        return type.cast(this.getSingleChild(ctx));
    }

    protected TokenRange getTokenRange(ParserRuleContext ctx) {
        return TokenRange.of(ctx.start, ctx.stop);
    }

    protected TerminalNode getSingleChildTerminalNode(WebViewComponentsParser.JStringBodyTextContext ctx) {
        return ctx.getChild(TerminalNode.class, 0);
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
    public Node visitBodyText(WebViewComponentsParser.BodyTextContext ctx) {
        return this.getSingleChild(ctx);
    }

    @Override
    public Node visitGStringBodyText(WebViewComponentsParser.GStringBodyTextContext ctx) {
        final List<Node> children = new ArrayList<>();
        for (final var child : ctx.children) {
            final @Nullable Node childResult = child.accept(this);
            if (childResult != null) {
                children.add(childResult);
            }
        }
        return this.nodeFactory.gStringBodyTextNode(this.getTokenRange(ctx), children);
    }

    @Override
    public @Nullable Node visitJStringBodyText(WebViewComponentsParser.JStringBodyTextContext ctx) {
        final String text = ctx.getText();
        if (isNotBlankNotEmpty(text)) {
            return this.nodeFactory.jStringBodyTextNode(
                    this.getTokenRange(ctx),
                    text
            );
        } else {
            return null;
        }
    }

    @Override
    public Node visitGStringBodyTextGroovyElement(WebViewComponentsParser.GStringBodyTextGroovyElementContext ctx) {
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
                this.getSingleAs(ctx.body(), BodyNode.class)
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

    private static final Pattern lowercaseLetterPattern = Pattern.compile("\\p{Ll}");

    protected boolean startsWithLowercaseLetter(String subject) {
        if (subject.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot test for starting lowercase letter when the subject length is 0; given: " + subject);
        }
        return lowercaseLetterPattern.matcher(subject.substring(0, 1)).matches();
    }

    @Override
    public Node visitComponentType(WebViewComponentsParser.ComponentTypeContext ctx) {
        final var identifiers = ctx.Identifier();
        if (identifiers.size() == 1) {
            final TerminalNode first = identifiers.getFirst();
            if (startsWithLowercaseLetter(first.getText())) {
                return this.nodeFactory.stringComponentTypeNode(
                        this.getTokenRange(ctx), first.getSymbol().getTokenIndex()
                );
            }
        }
        return this.nodeFactory.classComponentTypeNode(this.getTokenRange(ctx));
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
        final TerminalNode identifier = ctx.Identifier();
        final KeyNode keyNode = this.nodeFactory.keyNode(TokenRange.of(
                identifier.getSymbol(),
                ctx.Equals().getSymbol()
        ), identifier.getSymbol().getTokenIndex());
        final ValueNode valueNode = (ValueNode) ctx.value().accept(this);
        return this.nodeFactory.keyValueAttrNode(this.getTokenRange(ctx), keyNode, valueNode);
    }

    @Override
    public Node visitBooleanAttr(WebViewComponentsParser.BooleanAttrContext ctx) {
        final Token identifierToken = ctx.Identifier().getSymbol();
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
                // TODO: we need to set the appropriate type: slashy, dollar slashy, etc.
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
            return this.nodeFactory.dollarScriptletNode(
                    this.getTokenRange(ctx),
                    ctx.GroovyCode().getSymbol().getTokenIndex()
            );
        } else {
            return null;
        }
    }

    @Override
    public @Nullable Node visitPlainScriptlet(WebViewComponentsParser.PlainScriptletContext ctx) {
        final TerminalNode groovyCode = ctx.GroovyCode();
        if (groovyCode != null) {
            return this.nodeFactory.plainScriptletNode(this.getTokenRange(ctx), groovyCode.getSymbol().getTokenIndex());
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
                    groovyCode.getSymbol().getTokenIndex()
            );
        } else {
            return null;
        }
    }

    @Override
    public @Nullable Node visitDollarReference(WebViewComponentsParser.DollarReferenceContext ctx) {
        final TerminalNode groovyCode = ctx.GroovyCode();
        if (groovyCode != null) {
            return this.nodeFactory.dollarReferenceNode(
                    this.getTokenRange(ctx),
                    groovyCode.getSymbol().getTokenIndex()
            );
        } else {
            return null;
        }
    }

    @Override
    public Node visitTerminal(TerminalNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node visitErrorNode(ErrorNode node) {
        throw new IllegalStateException("Found an ErrorNode: " + node);
    }

}
