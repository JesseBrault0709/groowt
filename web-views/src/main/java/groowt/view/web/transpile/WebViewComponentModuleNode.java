package groowt.view.web.transpile;

import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.SourceUnit;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WebViewComponentModuleNode extends ModuleNode {

    public static void copyTo(ModuleNode from, WebViewComponentModuleNode to) {
        from.getImports().forEach(to::addImport);
        from.getStarImports().forEach(to::addStarImport);
        from.getStaticImports().forEach(to::addStaticImport);
        from.getStaticStarImports().forEach(to::addStaticStarImport);
    }

    protected final List<ImportNode> imports = new ArrayList<>();
    protected final List<ImportNode> starImports = new ArrayList<>();
    protected final Map<String, ImportNode> staticImports = new LinkedHashMap<>();
    protected final Map<String, ImportNode> staticStarImports = new LinkedHashMap<>();

    protected final Map<String, ImportNode> allImports = new LinkedHashMap<>();

    public WebViewComponentModuleNode(SourceUnit context) {
        super(context);
    }

    @Override
    public List<ImportNode> getImports() {
        return new ArrayList<>(this.imports);
    }

    @Override
    public List<ImportNode> getStarImports() {
        return new ArrayList<>(this.starImports);
    }

    @Override
    public Map<String, ImportNode> getStaticImports() {
        return new HashMap<>(this.staticImports);
    }

    @Override
    public Map<String, ImportNode> getStaticStarImports() {
        return new HashMap<>(this.staticStarImports);
    }

    @Override
    public @Nullable ClassNode getImportType(String alias) {
        return Optional.ofNullable(this.getImport(alias))
                .map(ImportNode::getType)
                .orElse(null);
    }

    /**
     * @param alias the name of interest
     * @return a standard (non-static, non-star) import, or {@code null} if there is none
     */
    @Override
    public @Nullable ImportNode getImport(String alias) {
        return this.imports.stream()
                .filter(importNode -> importNode.getAlias().equals(alias))
                .findFirst()
                .orElse(null);
    }

    protected void putToAll(String alias, ImportNode importNode) {
        this.allImports.put(alias, importNode);
    }

    protected final void putToAll(ImportNode importNode) {
        this.putToAll(importNode.getAlias(), importNode);
    }

    public void addImport(ImportNode importNode) {
        this.imports.add(importNode);
        this.putToAll(importNode);
    }

    public void addStarImport(ImportNode importNode) {
        this.starImports.add(importNode);
        this.putToAll(importNode);
    }

    public void addStaticImport(String alias, ImportNode importNode) {
        this.staticImports.put(alias, importNode);
        this.putToAll(alias, importNode);
    }

    public void addStaticStarImport(String alias, ImportNode importNode) {
        this.staticStarImports.put(alias, importNode);
        this.putToAll(alias, importNode);
    }

    @Override
    public void addImport(String alias, ClassNode type) {
        this.addImport(new ImportNode(type, alias));
    }

    @Override
    public void addImport(String alias, ClassNode type, List<AnnotationNode> annotations) {
        final var importNode = new ImportNode(type, alias);
        importNode.addAnnotations(annotations);
        this.addImport(importNode);
    }

    @Override
    public void addStarImport(String packageName) {
        this.addStarImport(new ImportNode(packageName));
    }

    @Override
    public void addStarImport(String packageName, List<AnnotationNode> annotations) {
        final var importNode = new ImportNode(packageName);
        importNode.addAnnotations(annotations);
        this.addStarImport(importNode);
    }

    @Override
    public void addStaticImport(ClassNode type, String fieldName, String alias) {
        this.addStaticImport(alias, new ImportNode(type, fieldName, alias));
    }

    @Override
    public void addStaticImport(ClassNode type, String fieldName, String alias, List<AnnotationNode> annotations) {
        final var importNode = new ImportNode(type, fieldName, alias);
        importNode.addAnnotations(annotations);
        this.addStaticImport(alias, importNode);
    }

    @Override
    public void addStaticStarImport(String name, ClassNode type) {
        this.addStaticStarImport(name, new ImportNode(type, name));
    }

    @Override
    public void addStaticStarImport(String name, ClassNode type, List<AnnotationNode> annotations) {
        final var importNode = new ImportNode(type, name);
        importNode.addAnnotations(annotations);
        this.addStaticStarImport(name, importNode);
    }

}
