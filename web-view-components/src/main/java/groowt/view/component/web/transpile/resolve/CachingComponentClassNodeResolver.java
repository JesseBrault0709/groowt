package groowt.view.component.web.transpile.resolve;

import groowt.util.fp.either.Either;
import groowt.view.component.web.WebViewComponent;
import groowt.view.component.web.compiler.WebViewComponentTemplateCompileUnit;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;

import java.util.ArrayList;
import java.util.List;

public class CachingComponentClassNodeResolver implements ComponentClassNodeResolver {

    private final List<ClassNode> classNodes = new ArrayList<>();

    protected final WebViewComponentTemplateCompileUnit compileUnit;

    public CachingComponentClassNodeResolver(WebViewComponentTemplateCompileUnit compileUnit) {
        this.compileUnit = compileUnit;
    }

    public void addClass(Class<? extends WebViewComponent> clazz) {
        this.classNodes.add(ClassHelper.make(clazz));
    }

    public void addClassNode(ClassNode classNode) {
        this.classNodes.add(classNode);
    }

    @Override
    public Either<ClassNodeResolveException, ClassNode> getClassForFqn(String fqn) {
        for (final var classNode : this.classNodes) {
            if (classNode.getName().equals(fqn)) {
                return Either.right(classNode);
            }
        }
        return Either.left(new ClassNodeResolveException(
                this.compileUnit,
                fqn,
                "Could not resolve ClassNode for fqn: " + fqn,
                null
        ));
    }

    @Override
    public Either<ClassNodeResolveException, ClassNode> getClassForNameWithoutPackage(String nameWithoutPackage) {
        for (final var classNode : this.classNodes) {
            if (classNode.getNameWithoutPackage().equals(nameWithoutPackage)) {
                return Either.right(classNode);
            }
        }
        return Either.left(new ClassNodeResolveException(
                this.compileUnit,
                nameWithoutPackage,
                "Could not resolve ClassNode for nameWithoutPackage: " + nameWithoutPackage,
                null
        ));
    }

}
