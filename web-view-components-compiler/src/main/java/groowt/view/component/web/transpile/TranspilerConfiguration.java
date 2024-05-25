package groowt.view.component.web.transpile;

import groovy.lang.Tuple3;
import org.codehaus.groovy.ast.ClassNode;

import java.util.Map;
import java.util.Set;

public interface TranspilerConfiguration {
    PositionSetter getPositionSetter();
    BodyTranspiler getBodyTranspiler();
    AppendOrAddStatementFactory getAppendOrAddStatementFactory();
    Map<String, ClassNode> getImports();
    Set<String> getStarImports();
    Set<Tuple3<ClassNode, String, String>> getStaticImports();
    Map<String, ClassNode> getStaticStarImports();
    ClassNode getRenderContextImplementation();
}
