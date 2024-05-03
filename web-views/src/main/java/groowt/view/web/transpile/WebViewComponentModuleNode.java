package groowt.view.web.transpile;

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

    @Override
    public @Nullable ImportNode getImport(String alias) {
        return this.allImports.get(alias);
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

}
