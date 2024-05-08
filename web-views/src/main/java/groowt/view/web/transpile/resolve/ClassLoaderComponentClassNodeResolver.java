package groowt.view.web.transpile.resolve;

import groowt.view.web.compiler.WebViewComponentTemplateCompileUnit;
import groowt.view.web.util.Either;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassLoaderComponentClassNodeResolver extends ModuleNodeComponentClassNodeResolver {

    private static final Logger logger = LoggerFactory.getLogger(ModuleNodeComponentClassNodeResolver.class);

    protected final ClassLoader classLoader;

    public ClassLoaderComponentClassNodeResolver(
            WebViewComponentTemplateCompileUnit compileUnit,
            ModuleNode moduleNode,
            ClassLoader classLoader
    ) {
        super(compileUnit, moduleNode);
        this.classLoader = classLoader;
    }

    protected final Either<ClassNodeResolveException, ClassNode> resolveWithClassLoader(String fqn) {
        logger.debug("Trying to resolve {}", fqn);
        try {
            Class<?> clazz = this.classLoader.loadClass(ResolveUtil.convertCanonicalNameToBinaryName(fqn));
            final var classNode = ResolveUtil.getClassNode(clazz);
            return Either.right(classNode);
        } catch (ClassNotFoundException classNotFoundException) {
            return Either.left(
                    new ClassNodeResolveException(
                            this.compileUnit,
                            fqn,
                            "Could not find class " + fqn + " with classLoader " +
                                    this.classLoader,
                            classNotFoundException
                    )
            );
        }
    }

    @Override
    public Either<ClassNodeResolveException, ClassNode> getClassForFqn(String fqn) {
        return super.getClassForFqn(fqn).flatMapLeft(ignored -> {
            final var classLoaderResult = this.resolveWithClassLoader(fqn);
            if (classLoaderResult.isRight()) {
                this.addClassNode(classLoaderResult.getRight());
            }
            return classLoaderResult;
        });
    }

}
