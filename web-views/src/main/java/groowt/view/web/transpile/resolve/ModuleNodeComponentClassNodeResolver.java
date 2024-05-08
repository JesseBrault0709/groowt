package groowt.view.web.transpile.resolve;

import groowt.view.web.WebViewComponentBugError;
import groowt.view.web.compiler.WebViewComponentTemplateCompileUnit;
import groowt.view.web.util.Either;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;

public class ModuleNodeComponentClassNodeResolver extends CachingComponentClassNodeResolver {

    private final ModuleNode moduleNode;

    public ModuleNodeComponentClassNodeResolver(
            WebViewComponentTemplateCompileUnit compileUnit,
            ModuleNode moduleNode
    ) {
        super(compileUnit);
        this.moduleNode = moduleNode;
    }

    @Override
    public Either<ClassNodeResolveException, ClassNode> getClassForNameWithoutPackage(String nameWithoutPackage) {
        return super.getClassForNameWithoutPackage(nameWithoutPackage).flatMapLeft(ignored -> {
            // try imports first
            final var importedClassNode = this.moduleNode.getImportType(nameWithoutPackage);
            if (importedClassNode != null) {
                this.addClassNode(importedClassNode);
                return Either.right(importedClassNode);
            }

            // try pre-pending package and asking for fqn
            final var packageName = this.moduleNode.getPackageName();
            if (packageName.endsWith(".")) {
                throw new WebViewComponentBugError(new IllegalStateException("Package name illegally ends with '.'"));
            }
            final var fqn = this.moduleNode.getPackageName() + "." + nameWithoutPackage;
            final var withPackage = this.getClassForFqn(fqn);
            if (withPackage.isRight()) {
                return withPackage;
            } else {
                return Either.left(new ClassNodeResolveException(
                        this.compileUnit,
                        nameWithoutPackage,
                        "Cannot resolve " + nameWithoutPackage
                                + " from imports, package-local classes, or pre-added classes."
                ));
            }
        });
    }

}
