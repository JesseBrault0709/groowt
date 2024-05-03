package groowt.view.web.analysis.classes;

import groovy.lang.GroovyClassLoader;
import groowt.view.web.antlr.MergedGroovyCodeToken;
import groowt.view.web.antlr.WebViewComponentsParser.PreambleContext;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.builder.AstStringCompiler;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public non-sealed class PreambleAwareClassLocator extends ClassLoaderClassLocator implements ClassLocator {

    private final List<ClassNode> currentClassNodes = new ArrayList<>();
    private GroovyClassLoader currentGroovyClassLoader;

    public PreambleAwareClassLocator(ClassLoader classLoader) {
        super(classLoader);
    }

    protected class ClassNodeCachedLocatedClass implements CustomCachedLocatedClass {
        private final ClassNode classNode;
        private Class<?> lazyLoadedClass;

        public ClassNodeCachedLocatedClass(ClassNode classNode) {
            this.classNode = classNode;
        }

        @Override
        public Class<?> get() {
            if (this.lazyLoadedClass == null) {
                try {
                    final File tmp = File.createTempFile("preambleContextAwareClassLocator", "_" + System.currentTimeMillis());
                    this.lazyLoadedClass = currentGroovyClassLoader.defineClass(this.classNode, null, tmp.getAbsolutePath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return this.lazyLoadedClass;
        }
    }

    public void setCurrentPreamble(PreambleContext preambleContext) {
        this.currentGroovyClassLoader = new GroovyClassLoader(this.classLoader);
        this.currentClassNodes.clear();
        final MergedGroovyCodeToken groovyCodeToken = (MergedGroovyCodeToken) preambleContext.GroovyCode().getSymbol();
        final String groovyCode = groovyCodeToken.getText();
        final List<ASTNode> astNodes = new AstStringCompiler().compile(groovyCode);
        astNodes.forEach(groovyASTNode -> {
            if (groovyASTNode instanceof ClassNode classNode) {
                this.currentClassNodes.add(classNode);
            }
        });
    }

    private @Nullable ClassNode searchPreambleSimpleName(String simpleName) {
        for (final ClassNode classNode : this.currentClassNodes) {
            if (classNode.getNameWithoutPackage().equals(simpleName)) {
                this.addToCache(classNode.getName(), new ClassNodeCachedLocatedClass(classNode));
                return classNode;
            }
        }
        return null;
    }

    @Override
    public boolean hasClassForFQN(String name) {
        return super.hasClassForFQN(name) || this.searchPreambleSimpleName(name) != null;
    }

    private boolean hasSimpleNameInCache(String simpleName) {
        final Collection<ClassNodeCachedLocatedClass> allCached = this.getFromCacheByType(ClassNodeCachedLocatedClass.class).values();
        for (final ClassNodeCachedLocatedClass cached : allCached) {
            if (cached.classNode.getNameWithoutPackage().equals(simpleName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasClassForSimpleName(String simpleName) {
        return this.hasSimpleNameInCache(simpleName) || this.searchPreambleSimpleName(simpleName) != null;
    }

}