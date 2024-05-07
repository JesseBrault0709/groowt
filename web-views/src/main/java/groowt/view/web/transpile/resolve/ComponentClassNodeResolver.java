package groowt.view.web.transpile.resolve;

import groowt.view.web.util.Either;
import org.codehaus.groovy.ast.ClassNode;
import org.jetbrains.annotations.Nullable;

public interface ComponentClassNodeResolver {

    record ClassNodeResolveError(ClassIdentifier identifier, String getMessage, @Nullable Throwable getCause) {}

    Either<ClassNodeResolveError, ClassNode> getClassForFqn(String fqn);
    Either<ClassNodeResolveError, ClassNode> getClassForNameWithoutPackage(String nameWithoutPackage);

}
