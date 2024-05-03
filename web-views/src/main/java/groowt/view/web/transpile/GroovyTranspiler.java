package groowt.view.web.transpile;

import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.node.CompilationUnitNode;
import org.codehaus.groovy.control.io.ReaderSource;

public interface GroovyTranspiler {

    void transpile(
            CompilationUnitNode compilationUnitNode,
            TokenList tokens,
            String ownerComponentName,
            ReaderSource readerSource
    );

}
