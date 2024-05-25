package groowt.view.component.web.transpile.resolve;

import groowt.util.fp.either.Either;
import groowt.view.component.web.compiler.WebViewComponentTemplateCompileUnit;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;

import java.util.Objects;

public class ModuleNodeComponentClassNodeResolver extends CachingComponentClassNodeResolver {

    private ModuleNode moduleNode;

    public ModuleNodeComponentClassNodeResolver(WebViewComponentTemplateCompileUnit compileUnit) {
        super(compileUnit);
    }

    public ModuleNode getModuleNode() {
        return Objects.requireNonNull(this.moduleNode);
    }

    public void setModuleNode(ModuleNode moduleNode) {
        this.moduleNode = Objects.requireNonNull(moduleNode);
    }

    @Override
    public Either<ClassNodeResolveException, ClassNode> getClassForNameWithoutPackage(String nameWithoutPackage) {
        return super.getClassForNameWithoutPackage(nameWithoutPackage).flatMapLeft(ignored -> {
            // try regular imports first
            final var importedClassNode = this.getModuleNode().getImportType(nameWithoutPackage);
            if (importedClassNode != null) {
                this.addClassNode(importedClassNode);
                return Either.right(importedClassNode);
            }

            // try star imports
            final var starImports = this.getModuleNode().getStarImports();
            for (final var starImport : starImports) {
                final var packageName = starImport.getPackageName();
                final String fqn;
                if (!packageName.equals(".") && packageName.endsWith(".")) {
                    fqn = packageName + nameWithoutPackage;
                } else {
                    fqn = packageName + "." + nameWithoutPackage;
                }
                final var withPackage = this.getClassForFqn(fqn);
                if (withPackage.isRight()) {
                    return withPackage;
                }
            }

            // try pre-pending package and asking for fqn
            final String moduleNodePackageName = this.getModuleNode().getPackageName();
            final String packageName;
            if (moduleNodePackageName != null) {
                packageName = moduleNodePackageName;
            } else {
                packageName = "";
            }

            final String fqn;
            if (packageName.equals(".") || packageName.isEmpty()) {
                fqn = nameWithoutPackage;
            } else if (packageName.endsWith(".")) {
                fqn = packageName + nameWithoutPackage;
            } else {
                fqn = packageName + "." + nameWithoutPackage;
            }

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
