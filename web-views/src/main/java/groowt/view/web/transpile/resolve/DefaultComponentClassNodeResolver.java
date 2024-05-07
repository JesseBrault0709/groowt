package groowt.view.web.transpile.resolve;

import groowt.view.web.util.Either;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DefaultComponentClassNodeResolver implements ComponentClassNodeResolver {

    private static final Pattern aliasPattern =
            Pattern.compile("^(\\P{Lu}+\\.)*(?<alias>\\p{Lu}.+(\\.\\p{Lu}.+)*)$");

    protected static ClassNode getClassNode(Class<?> clazz) {
        return ClassHelper.makeCached(clazz);
    }

    protected static String getAlias(String name) {
        final var matcher = aliasPattern.matcher(name);
        if (matcher.matches()) {
            return matcher.group("alias");
        } else {
            throw new IllegalArgumentException("Cannot determine alias from " + name);
        }
    }

    private final ModuleNode moduleNode;
    protected final ClassLoader classLoader;
    private final ClassInfoList classInfoList;
    private final Map<ClassIdentifier, @Nullable ClassNode> cache = new HashMap<>();

    public DefaultComponentClassNodeResolver(
            ModuleNode moduleNode,
            ClassLoader classLoader,
            ClassInfoList webViewComponentClassInfoList
    ) {
        this.moduleNode = moduleNode;
        this.classLoader = classLoader;
        this.classInfoList = webViewComponentClassInfoList;
    }

    protected final void addToCache(ClassIdentifier identifier, ClassNode clazz) {
        this.cache.put(identifier, clazz);
    }

    protected final Either<ClassNodeResolveError, ClassNode> resolveWithClassLoader(ClassIdentifierWithFqn identifier) {
        try {
            Class<?> clazz = this.classLoader.loadClass(identifier.getFqn());
            final var classNode = getClassNode(clazz);
            return Either.right(classNode);
        } catch (ClassNotFoundException classNotFoundException) {
            return Either.left(
                    new ClassNodeResolveError(
                            identifier,
                            "Could not find class " + identifier.getFqn() + " with classLoader " +
                                    this.classLoader,
                            classNotFoundException
                    )
            );
        }
    }

    protected final Either<ClassNodeResolveError, ClassNode> resolveWithClassGraph(ClassIdentifier identifier) {
        final List<ClassInfo> potential = this.classInfoList.stream()
                .filter(classInfo -> classInfo.getSimpleName().equals(identifier.getAlias()))
                .toList();
        if (potential.size() > 1) {
            final var error = new ClassNodeResolveError(
                    identifier,
                    "There is more than one class on the classpath implementing WebViewComponent that has " +
                            "the simple name " + identifier.getAlias() + ". Please explicitly import the desired " +
                            "component class in the preamble, or use the fully qualified name of the component " +
                            "you wish to use.",
                    null
            );
            return Either.left(error);
        } else if (potential.size() == 1) {
            final var classInfo = potential.getFirst();
            final ClassNode result = getClassNode(classInfo.loadClass());
            return Either.right(result);
        } else {
            final var error = new ClassNodeResolveError(
                    identifier,
                    "Could not resolve a class implementing WebViewComponent for " + identifier.getAlias(),
                    null
            );
            return Either.left(error);
        }
    }

    protected final @Nullable ClassNode findInCacheFqn(String fqn) {
        for (final var entry : this.cache.entrySet()) {
            final var identifier = entry.getKey();
            final var classNode = entry.getValue();
            if (classNode != null
                    && identifier instanceof ClassIdentifierWithFqn withFqn
                    && withFqn.getFqn().equals(fqn)
            ) {
                return classNode;
            }
        }
        return null;
    }

    protected final @Nullable ClassNode findInCacheSimpleName(String simpleName) {
        for (final var entry : this.cache.entrySet()) {
            final var identifier = entry.getKey();
            final var classNode = entry.getValue();
            if (classNode != null && identifier.getAlias().equals(simpleName)) {
                return classNode;
            }
        }
        return null;
    }

    @Override
    public Either<ClassNodeResolveError, ClassNode> getClassForFqn(String fqn) {
        // try cache
        final var identifier = new ClassIdentifierWithFqn(getAlias(fqn), fqn);
        final var fromCache = this.findInCacheFqn(fqn);
        if (fromCache != null) {
            return Either.right(fromCache);
        }

        // do not try preamble, because it is a fully qualified name; i.e., it needs no import

        // try classLoader
        final var classLoaderResolved = this.resolveWithClassLoader(identifier);
        if (classLoaderResolved.isRight()) {
            this.addToCache(identifier, classLoaderResolved.asRight().get());
        }
        return classLoaderResolved;
    }

    @Override
    public Either<ClassNodeResolveError, ClassNode> getClassForNameWithoutPackage(String nameWithoutPackage) {
        // try cache
        final var identifier = new ClassIdentifier(nameWithoutPackage);
        final var fromCache = this.findInCacheSimpleName(nameWithoutPackage);
        if (fromCache != null) {
            return Either.right(fromCache);
        }

        // try imports
        final var importedClassNode = this.moduleNode.getImportType(nameWithoutPackage);
        if (importedClassNode != null) {
            this.addToCache(
                    new ClassIdentifierWithFqn(
                            importedClassNode.getName(),
                            importedClassNode.getNameWithoutPackage()
                    ),
                    importedClassNode
            );
            return Either.right(importedClassNode);
        }

        // try classgraph
        final var classGraphResolved = this.resolveWithClassGraph(identifier);
        if (classGraphResolved.isRight()) {
            final ClassNode resolvedClassNode = classGraphResolved.asRight().get();
            final var withFqn = new ClassIdentifierWithFqn(nameWithoutPackage, resolvedClassNode.getName());
            this.addToCache(withFqn, resolvedClassNode);
        }
        return classGraphResolved;
    }

}
