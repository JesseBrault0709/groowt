package groowt.view.web.transpile;

import groowt.view.web.antlr.MergedGroovyCodeToken;
import groowt.view.web.antlr.WebViewComponentsLexer;
import groowt.view.web.ast.extension.GStringNodeExtension;
import groowt.view.web.ast.extension.GStringPathExtension;
import groowt.view.web.ast.extension.GStringScriptletExtension;
import groowt.view.web.ast.node.GStringBodyTextNode;
import groowt.view.web.ast.node.JStringBodyTextNode;
import groowt.view.web.ast.node.Node;
import groowt.view.web.transpile.util.GroovyUtil;
import groowt.view.web.util.FilteringIterable;
import groowt.view.web.util.Option;
import groowt.view.web.util.TokenRange;
import org.antlr.v4.runtime.Token;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class DefaultGStringTranspiler implements GStringTranspiler {

    private final PositionSetter positionSetter;
    private final JStringTranspiler jStringTranspiler;

    public DefaultGStringTranspiler(PositionSetter positionSetter, JStringTranspiler jStringTranspiler) {
        this.positionSetter = positionSetter;
        this.jStringTranspiler = jStringTranspiler;
    }

    protected Option<ConstantExpression> checkPrevBeforeDollar(@Nullable Node prev, Node current) {
        if (!(prev instanceof JStringBodyTextNode)) {
            return Option.liftLazy(() -> {
                final ConstantExpression expression = this.jStringTranspiler.createEmptyStringLiteral();
                this.positionSetter.setToStartOf(expression, current);
                return expression;
            });
        } else {
            return Option.empty();
        }
    }

    protected Option<ConstantExpression> checkNextAfterDollar(Node current, @Nullable Node next) {
        if (!(next instanceof JStringBodyTextNode)) {
            return Option.liftLazy(() -> {
                final ConstantExpression expression = this.jStringTranspiler.createEmptyStringLiteral();
                if (next != null) {
                    this.positionSetter.setToStartOf(expression, next);
                } else {
                    this.positionSetter.setToStartOf(expression, current);
                }
                return expression;
            });
        } else {
            return Option.empty();
        }
    }

    protected ConstantExpression handleText(JStringBodyTextNode jStringBodyTextNode, @Nullable Node prev) {
        if (prev instanceof JStringBodyTextNode) {
            throw new IllegalStateException("Cannot have two texts in a row");
        }
        return this.jStringTranspiler.createStringLiteral(jStringBodyTextNode);
    }

    protected record PathResult(
            Expression result,
            Option<ConstantExpression> before,
            Option<ConstantExpression> after
    ) {}

    protected PathResult handlePath(Node current, GStringPathExtension path, @Nullable Node prev, @Nullable Node next) {
        final List<Token> groowtTokens = path.getRawTokens();

        VariableExpression begin = null;
        PropertyExpression propertyExpression = null;

        for (final Token groowtToken : groowtTokens) {
            if (groowtToken instanceof MergedGroovyCodeToken groovyCodeToken) {
                final Iterable<Token> identifierTokenIterable = FilteringIterable.continuingUntilSuccess(
                        groovyCodeToken.getOriginals(),
                        token -> token.getType() == WebViewComponentsLexer.GStringIdentifier
                );
                for (final Token identifierToken : identifierTokenIterable) {
                    final String identifier = identifierToken.getText();
                    final TokenRange identifierTokenRange = TokenRange.of(identifierToken);
                    if (begin == null) {
                        begin = new VariableExpression(identifier);
                        this.positionSetter.setPosition(begin, identifierTokenRange);
                    } else if (propertyExpression == null) {
                        propertyExpression = new PropertyExpression(begin, identifier);
                        this.positionSetter.setPosition(propertyExpression, identifierTokenRange);
                    } else {
                        propertyExpression = new PropertyExpression(propertyExpression, identifier);
                        this.positionSetter.setPosition(propertyExpression, identifierTokenRange);
                    }
                }
            } else {
                throw new IllegalStateException("Received a non-MergedGroovyToken from a GStringExtension");
            }
        }

        if (begin == null) {
            throw new IllegalStateException("begin is null!");
        }

        if (propertyExpression != null) {
            return new PathResult(
                    propertyExpression,
                    this.checkPrevBeforeDollar(prev, current),
                    this.checkNextAfterDollar(current, next)
            );
        } else {
            return new PathResult(
                    begin,
                    this.checkPrevBeforeDollar(prev, current),
                    this.checkNextAfterDollar(current, next)
            );
        }
    }

    protected ClosureExpression handleScriptlet(GStringScriptletExtension gStringScriptletExtension) {
        final GroovyUtil.ConvertResult convertResult = GroovyUtil.convert(
                "def cl = {" + gStringScriptletExtension.getAsValidEmbeddableCode() + "}"
        );
        final BlockStatement convertBlock = convertResult.blockStatement();
        if (convertBlock == null) {
            throw new NullPointerException("Did not except convertBlock to be null");
        }
        final List<Statement> convertStatements = convertBlock.getStatements();
        if (convertStatements.size() != 1) {
            throw new IllegalStateException("Did not expect convertStatements.size() to not equal 1");
        }
        final ExpressionStatement convertExpressionStatement = (ExpressionStatement) convertStatements.getFirst();
        final BinaryExpression assignment = (BinaryExpression) convertExpressionStatement.getExpression();
        return (ClosureExpression) assignment.getRightExpression();
    }

    @Override
    public GStringExpression createGStringExpression(GStringBodyTextNode gStringBodyTextNode) {
        final var children = gStringBodyTextNode.getChildren();
        if (children.isEmpty()) {
            throw new IllegalArgumentException("Cannot make a gStringOutStatement from zero GStringParts");
        }

        final String verbatimText = children.stream().map(node -> {
            if (node instanceof JStringBodyTextNode jStringBodyTextNode) {
                return jStringBodyTextNode.getContent();
            } else if (node.hasExtension(GStringNodeExtension.class)) {
                final var gString = node.getExtension(GStringNodeExtension.class);
                return switch (gString) {
                    case GStringPathExtension ignored -> gString.getAsValidEmbeddableCode();
                    case GStringScriptletExtension ignored -> "${" + gString.getAsValidEmbeddableCode() + "}";
                };
            } else {
                throw new IllegalArgumentException(
                        "Cannot get verbatim text when one of the given parts has "
                                + "neither a JStringNodeExtension nor a GStringNodeExtension"
                );
            }
        }).collect(Collectors.joining());

        final List<ConstantExpression> texts = new ArrayList<>();
        final List<Expression> values = new ArrayList<>();
        final ListIterator<Node> iter = children.listIterator();

        while (iter.hasNext()) {
            final var prev = iter.previousIndex() > -1 ? children.get(iter.previousIndex()) : null;
            final var current = iter.next();
            final var next = iter.nextIndex() < children.size() ? children.get(iter.nextIndex()) : null;
            if (current instanceof JStringBodyTextNode jStringBodyTextNode) {
                texts.add(this.handleText(jStringBodyTextNode, prev));
            } else {
                switch (current.getExtension(GStringNodeExtension.class)) {
                    case GStringPathExtension path -> {
                        final var pathResult = this.handlePath(current, path, prev, next);
                        pathResult.before().ifPresent(texts::add);
                        values.add(pathResult.result());
                        pathResult.after().ifPresent(texts::add);
                    }
                    case GStringScriptletExtension scriptlet -> {
                        checkPrevBeforeDollar(prev, current).ifPresent(texts::add);
                        values.add(this.handleScriptlet(scriptlet));
                        checkNextAfterDollar(current, next).ifPresent(texts::add);
                    }
                }
            }
        }

        if (texts.size() != values.size() + 1) {
            throw new IllegalStateException(
                    "incorrect amount of texts vs. values: " + texts.size() + " " + values.size()
            );
        }
        
        final var gString = new GStringExpression(verbatimText, texts, values);
        this.positionSetter.setPosition(gString, gStringBodyTextNode);
        return gString;
    }

}
