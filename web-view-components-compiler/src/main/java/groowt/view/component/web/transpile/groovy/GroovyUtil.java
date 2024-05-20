package groowt.view.component.web.transpile.groovy;

import groovy.lang.GroovyCodeSource;
import groowt.view.component.web.WebViewComponentBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.builder.AstStringCompiler;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public final class GroovyUtil {

    public static String formatGroovy(ASTNode node) {
        return formatGroovy(List.of(node), GroovyPrettyPrinter.SIMPLE);
    }

    public static String formatGroovy(ASTNode node, GroovyPrettyPrinter.OnEachNode onEachNode) {
        return formatGroovy(List.of(node), onEachNode);
    }

    public static String formatGroovy(List<ASTNode> nodes) {
        return formatGroovy(nodes, GroovyPrettyPrinter.SIMPLE);
    }

    public static String formatGroovy(List<ASTNode> nodes, GroovyPrettyPrinter.OnEachNode onEachNode) {
        return nodes.stream().map(node -> {
            final var pp = new GroovyPrettyPrinter(onEachNode);
            if (node instanceof ClassNode classNode) {
                pp.visitClass(classNode);
            } else {
                node.visit(pp);
            }
            return pp.getResult();
        }).collect(Collectors.joining("\n"));
    }

    public record ConvertResult(
            ModuleNode moduleNode,
            @Nullable BlockStatement blockStatement,
            @Nullable ClassNode scriptClass,
            List<ClassNode> classNodes
    ) {}

    private static String makeScriptClassName() {
        return "Script" + System.nanoTime();
    }

    public static ConvertResult convert(String source) {
        return convert(source, makeScriptClassName());
    }

    /**
     * See {@link AstStringCompiler#compile(String, CompilePhase, boolean)}
     */
    public static ConvertResult convert(String source, String scriptClassName) {
        final var groovyCodeSource = new GroovyCodeSource(
                source, scriptClassName + ".groovy", "/groovy/script"
        );
        final var compilationUnit = new CompilationUnit(
                CompilerConfiguration.DEFAULT,
                groovyCodeSource.getCodeSource(),
                null
        );
        try {
            compilationUnit.addSource(groovyCodeSource.getName(), source);
            compilationUnit.compile(CompilePhase.CONVERSION.getPhaseNumber());
        } finally {
            try {
                compilationUnit.getClassLoader().close();
            } catch (IOException ignored) {
            }
        }
        final List<ModuleNode> moduleNodes = compilationUnit.getAST().getModules();
        if (moduleNodes.size() != 1) {
            throw new RuntimeException("moduleNodes.size() != 1; is actually " + moduleNodes.size());
        }
        final ModuleNode moduleNode = moduleNodes.getFirst();
        final BlockStatement blockStatement = moduleNode.getStatementBlock();
        final List<ClassNode> classNodes = moduleNode.getClasses();
        ClassNode scriptClassNode = null;
        for (final ClassNode classNode : classNodes) {
            if (classNode.isScript()) {
                if (scriptClassNode == null) {
                    scriptClassNode = classNode;
                } else {
                    throw new RuntimeException("Found more than one scriptClassNode!");
                }
            }
        }
        return new ConvertResult(moduleNode, blockStatement, scriptClassNode, classNodes);
    }

    /**
     * @param source must be of form {@code def c = { ... }} and the closure must not be empty.
     * @return the block inside the closure
     */
    public static ClosureExpression getClosure(String source) {
        final ConvertResult convertResult = convert(source);
        final BlockStatement blockStatement = convertResult.blockStatement();
        if (blockStatement == null) {
            throw new WebViewComponentBugError(new IllegalArgumentException(
                    "Did not expect the source to produce no BlockStatement."
            ));
        }
        if (blockStatement.isEmpty()) {
            throw new WebViewComponentBugError(new IllegalArgumentException(
                    "Did not expect the BlockStatement to be empty."
            ));
        }
        final ExpressionStatement exprStmt = (ExpressionStatement) blockStatement.getStatements().getFirst();
        final BinaryExpression binaryExpression = (BinaryExpression) exprStmt.getExpression();
        return (ClosureExpression) binaryExpression.getRightExpression();
    }

    private GroovyUtil() {}

}
