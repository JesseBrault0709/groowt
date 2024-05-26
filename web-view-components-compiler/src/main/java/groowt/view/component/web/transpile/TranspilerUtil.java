package groowt.view.component.web.transpile;

import groovy.lang.Tuple2;
import groowt.view.component.ComponentTemplate;
import groowt.view.component.context.ComponentContext;
import groowt.view.component.runtime.ComponentWriter;
import groowt.view.component.web.runtime.DefaultWebViewRenderContext;
import groowt.view.component.web.runtime.WebViewComponentRenderContext;
import groowt.view.component.web.util.SourcePosition;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

import static org.apache.groovy.parser.antlr4.util.StringUtils.*;

public final class TranspilerUtil {

    public static final ClassNode COMPONENT_TEMPLATE = ClassHelper.make(ComponentTemplate.class);
    public static final ClassNode COMPONENT_CONTEXT_TYPE = ClassHelper.make(ComponentContext.class);
    public static final ClassNode COMPONENT_WRITER_TYPE = ClassHelper.make(ComponentWriter.class);
    public static final ClassNode WEB_VIEW_COMPONENT_RENDER_CONTEXT_TYPE =
            ClassHelper.make(WebViewComponentRenderContext.class);
    public static final ClassNode DEFAULT_RENDER_CONTEXT_IMPLEMENTATION =
            ClassHelper.make(DefaultWebViewRenderContext.class);

    public static final String GROOWT_VIEW_COMPONENT_WEB = "groowt.view.component.web";
    public static final String COMPONENT_CONTEXT_NAME = "componentContext";
    public static final String COMPONENT_WRITER_NAME = "out";
    public static final String RENDER_CONTEXT_NAME = "renderContext";
    public static final String GET_RENDERER = "getRenderer";
    public static final String APPEND = "append";
    public static final String ADD = "add";

    public static Tuple2<ConstantExpression, ConstantExpression> lineAndColumn(SourcePosition sourcePosition) {
        return new Tuple2<>(
                new ConstantExpression(sourcePosition.line(), true),
                new ConstantExpression(sourcePosition.column(), true)
        );
    }

    public static ConstantExpression getStringLiteral(String content) {
        final var withoutCR = removeCR(content);
        final var escaped = replaceEscapes(withoutCR, NONE_SLASHY);
        final var expr = new ConstantExpression(escaped);
        expr.setNodeMetaData("_IS_STRING", true);
        return expr;
    }

    public static Token getAssignToken() {
        return new Token(Types.ASSIGN, "=", -1, -1);
    }

    public static Token getLeftShiftToken() {
        return new Token(Types.LEFT_SHIFT, "<<", -1, -1);
    }

    private TranspilerUtil() {}

}
