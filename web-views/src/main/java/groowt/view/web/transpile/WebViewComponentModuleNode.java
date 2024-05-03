package groowt.view.web.transpile;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.SourceUnit;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WebViewComponentModuleNode extends ModuleNode {

    public static void copyTo(ModuleNode from, WebViewComponentModuleNode to) {
        to.setDescription(from.getDescription());
        to.setPackage(from.getPackage());
        to.setPackageName(from.getPackageName());
        to.setImportsResolved(from.hasImportsResolved());
        to.setMetaDataMap(from.getMetaDataMap());
        from.getImports().forEach(to::addImport);
        from.getStarImports().forEach(to::addStarImport);
        from.getStaticImports().forEach(to::addStaticImport);
        from.getStaticStarImports().forEach(to::addStaticStarImport);
        from.getClasses().forEach(to::addClass);
    }

    protected final List<ImportNode> imports = new ArrayList<>();
    protected final List<ImportNode> starImports = new ArrayList<>();
    protected final Map<String, ImportNode> staticImports = new LinkedHashMap<>();
    protected final Map<String, ImportNode> staticStarImports = new LinkedHashMap<>();

    public WebViewComponentModuleNode(SourceUnit context) {
        super(context);
    }

    @Override
    public List<ImportNode> getImports() {
        final List<ImportNode> r = new ArrayList<>(super.getImports());
        r.addAll(this.imports);
        return r;
    }

    @Override
    public List<ImportNode> getStarImports() {
        final List<ImportNode> r = new ArrayList<>(super.getStarImports());
        r.addAll(this.starImports);
        return r;
    }

    @Override
    public Map<String, ImportNode> getStaticImports() {
        final Map<String, ImportNode> r = new HashMap<>(super.getStaticImports());
        r.putAll(this.staticImports);
        return r;
    }

    @Override
    public Map<String, ImportNode> getStaticStarImports() {
        final Map<String, ImportNode> r = new HashMap<>(super.getStaticStarImports());
        r.putAll(this.staticStarImports);
        return r;
    }

    @Override
    public ClassNode getImportType(String alias) {
        final ClassNode superResult = super.getImportType(alias);
        if (superResult != null) {
            return superResult;
        } else {
            return Optional.ofNullable(this.getImport(alias)).map(ImportNode::getType).orElse(null);
        }
    }

    @Override
    public @Nullable ImportNode getImport(String alias) {
        final ImportNode superResult = super.getImport(alias);
        if (superResult != null) {
            return superResult;
        } else {
            final Map<String, ImportNode> aliases = this.getNodeMetaData("import.aliases", x -> {
                return this.imports.stream()
                        .collect(Collectors.toMap(ImportNode::getAlias,
                                Function.identity(),
                                (first, second) -> second
                        ));
            });
            return aliases.get(alias);
        }
    }

    // Copied from super
    protected void storeLastAddedImportNode(ImportNode importNode) {
        if (this.getNodeMetaData(ImportNode.class) == ImportNode.class) {
            this.putNodeMetaData(ImportNode.class, importNode);
        }
    }

    public void addImport(ImportNode importNode) {
        this.imports.add(importNode);
        this.removeNodeMetaData("import.aliases");
        this.storeLastAddedImportNode(importNode);
    }

    public void addStarImport(ImportNode importNode) {
        this.starImports.add(importNode);
        this.storeLastAddedImportNode(importNode);
    }

    public void addStaticImport(String alias, ImportNode importNode) {
        final ImportNode prev = this.staticImports.put(alias, importNode);
        if (prev != null) {
            this.staticImports.put(prev.toString(), prev);
            this.staticImports.put(alias, this.staticImports.remove(alias));
        }
        this.storeLastAddedImportNode(importNode);
    }

    public void addStaticStarImport(String alias, ImportNode importNode) {
        this.staticStarImports.put(alias, importNode);
        this.storeLastAddedImportNode(importNode);
    }

}
