package groowt.view.web.transpile;

import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.node.PreambleNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PreambleTranspiler {

    record PreambleResult(
            @Nullable ModuleNode moduleNode, @Nullable ClassNode scriptClass,
            List<ClassNode> allClasses
    ) {}

    PreambleResult getPreambleResult(@Nullable PreambleNode preambleNode, String templateName, TokenList tokens);

}
