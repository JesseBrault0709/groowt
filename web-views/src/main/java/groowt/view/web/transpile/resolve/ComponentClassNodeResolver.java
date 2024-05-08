package groowt.view.web.transpile.resolve;

import groowt.view.web.compiler.WebViewComponentTemplateCompileException;
import groowt.view.web.compiler.WebViewComponentTemplateCompileUnit;
import groowt.view.web.util.Either;
import org.codehaus.groovy.ast.ClassNode;

public interface ComponentClassNodeResolver {

    final class ClassNodeResolveException extends WebViewComponentTemplateCompileException {

        private final String identifier;

        public ClassNodeResolveException(
                WebViewComponentTemplateCompileUnit compileUnit,
                String identifier,
                String message
        ) {
            super(compileUnit, message);
            this.identifier = identifier;
        }

        public ClassNodeResolveException(
                WebViewComponentTemplateCompileUnit compileUnit,
                String identifier,
                String message,
                Throwable cause
        ) {
            super(compileUnit, message, cause);
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return this.identifier;
        }

    }

    Either<ClassNodeResolveException, ClassNode> getClassForFqn(String fqn);
    Either<ClassNodeResolveException, ClassNode> getClassForNameWithoutPackage(String nameWithoutPackage);

}
