package groowt.view.web.ast;

import groowt.util.di.DefaultRegistryObjectFactory;
import groowt.util.di.Registry;
import groowt.util.di.RegistryObjectFactory;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.extension.*;
import groowt.view.web.ast.node.*;
import groowt.view.web.util.TokenRange;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static groowt.util.di.BindingUtil.*;
import static groowt.util.di.filters.FilterHandlers.getAllowsTypesFilterHandler;
import static groowt.util.di.filters.IterableFilterHandlers.getIterableElementTypesFilterHandler;

public class DefaultNodeFactory implements NodeFactory {

    public static final Set<Class<? extends Node>> NODE_TYPES = Set.of(
            CompilationUnitNode.class,
            PreambleNode.class,
            BodyNode.class,
            GStringBodyTextNode.class,
            JStringBodyTextNode.class,
            TypedComponentNode.class,
            FragmentComponentNode.class,
            ComponentArgsNode.class,
            ClassComponentTypeNode.class,
            StringComponentTypeNode.class,
            ComponentConstructorNode.class,
            KeyValueAttrNode.class,
            BooleanValueAttrNode.class,
            KeyNode.class,
            GStringValueNode.class,
            JStringValueNode.class,
            ClosureValueNode.class,
            ComponentValueNode.class,
            PlainScriptletNode.class,
            DollarScriptletNode.class,
            DollarReferenceNode.class
    );

    protected final RegistryObjectFactory objectFactory;

    public DefaultNodeFactory(TokenList tokenList) {
        final var b = this.getRegistryObjectFactoryBuilder();
        b.configureRegistry(r -> {
            this.configureDependencies(r, tokenList);
            this.configureNodeImplementations(r);
            this.configureRegistryExtensions(r);
        });
        this.configureFilters(b);
        this.objectFactory = b.build();
    }

    protected RegistryObjectFactory.Builder<?> getRegistryObjectFactoryBuilder() {
        return DefaultRegistryObjectFactory.Builder.withDefaults();
    }

    protected void configureDependencies(Registry registry, TokenList tokenList) {
        registry.bind(RegistryObjectFactory.class, toProvider(() -> this.objectFactory));
        registry.bind(TokenList.class, toSingleton(tokenList));
        registry.bind(NodeExtensionFactory.class, toClass(SimpleNodeExtensionFactory.class));
        registry.bind(
                NodeExtensionContainer.class,
                toProvider(() -> new SimpleNodeExtensionContainer(this.objectFactory.get(NodeExtensionFactory.class)))
        );
    }

    protected void configureNodeImplementations(Registry registry) {
        NODE_TYPES.forEach(nodeType -> registry.bind(nodeType, toSelf()));
    }

    protected void configureRegistryExtensions(Registry registry) {
        registry.addExtension(new SelfNodeRegistryExtension());
    }

    protected void configureFilters(RegistryObjectFactory.Builder<?> builder) {
        builder.addFilterHandler(getAllowsTypesFilterHandler(Node.class));
        builder.addIterableFilterHandler(getIterableElementTypesFilterHandler());
        builder.addFilterHandler(ExtensionUtil.hasExtensionsFilterHandler);
        builder.addIterableFilterHandler(ExtensionUtil.iterableHasExtensionsFilterHandler);
        builder.addFilterHandler(ExtensionUtil.hasExtensionOneOfNodeFilterHandler);
        builder.addIterableFilterHandler(ExtensionUtil.iterableHasExtensionOneOfFilterHandler);
    }

    @Override
    public CompilationUnitNode compilationUnitNode(
            TokenRange tokenRange,
            @Nullable PreambleNode preambleNode,
            @Nullable BodyNode bodyNode
    ) {
        return this.objectFactory.get(CompilationUnitNode.class, tokenRange, preambleNode, bodyNode);
    }

    @Override
    public PreambleNode preambleNode(TokenRange tokenRange, int groovyIndex) {
        return this.objectFactory.get(PreambleNode.class, tokenRange, groovyIndex);
    }

    @Override
    public BodyNode bodyNode(TokenRange tokenRange, List<? extends BodyChildNode> children) {
        return this.objectFactory.get(BodyNode.class, tokenRange, children);
    }

    @Override
    public GStringBodyTextNode gStringBodyTextNode(TokenRange tokenRange, List<? extends Node> children) {
        return this.objectFactory.get(GStringBodyTextNode.class, tokenRange, children);
    }

    @Override
    public JStringBodyTextNode jStringBodyTextNode(TokenRange tokenRange, String content) {
        return this.objectFactory.get(JStringBodyTextNode.class, tokenRange, content);
    }

    @Override
    public TypedComponentNode typedComponentNode(
            TokenRange tokenRange,
            ComponentArgsNode componentArgsNode,
            @Nullable BodyNode body
    ) {
        return this.objectFactory.get(TypedComponentNode.class, tokenRange, componentArgsNode, body);
    }

    @Override
    public FragmentComponentNode fragmentComponentNode(TokenRange tokenRange, @Nullable BodyNode bodyNode) {
        return this.objectFactory.get(FragmentComponentNode.class, tokenRange, bodyNode);
    }

    @Override
    public ComponentArgsNode componentArgsNode(
            TokenRange tokenRange,
            ComponentTypeNode componentTypeNode,
            @Nullable ComponentConstructorNode componentConstructorNode,
            List<AttrNode> attributeNodes
    ) {
        return this.objectFactory.get(
                ComponentArgsNode.class,
                tokenRange,
                componentTypeNode,
                componentConstructorNode,
                attributeNodes
        );
    }

    @Override
    public ClassComponentTypeNode classComponentTypeNode(TokenRange tokenRange) {
        return this.objectFactory.get(ClassComponentTypeNode.class, tokenRange);
    }

    @Override
    public StringComponentTypeNode stringComponentTypeNode(TokenRange tokenRange, int typeTokenIndex) {
        return this.objectFactory.get(StringComponentTypeNode.class, tokenRange, typeTokenIndex);
    }

    @Override
    public ComponentConstructorNode componentConstructorNode(TokenRange tokenRange, int groovyIndex) {
        return this.objectFactory.get(ComponentConstructorNode.class, tokenRange, groovyIndex);
    }

    @Override
    public KeyValueAttrNode keyValueAttrNode(TokenRange tokenRange, KeyNode keyNode, ValueNode valueNode) {
        return this.objectFactory.get(KeyValueAttrNode.class, tokenRange, keyNode, valueNode);
    }

    @Override
    public BooleanValueAttrNode booleanValueAttrNode(TokenRange tokenRange, KeyNode keyNode) {
        return this.objectFactory.get(BooleanValueAttrNode.class, tokenRange, keyNode);
    }

    @Override
    public KeyNode keyNode(TokenRange tokenRange, int tokenIndex) {
        return this.objectFactory.get(KeyNode.class, tokenRange, tokenIndex);
    }

    @Override
    public GStringValueNode gStringValueNode(TokenRange tokenRange, int contentTokenIndex) {
        return this.objectFactory.createInstance(GStringValueNode.class, tokenRange, contentTokenIndex);
    }

    @Override
    public JStringValueNode jStringValueNode(TokenRange tokenRange, String content) {
        return this.objectFactory.get(JStringValueNode.class, tokenRange, content);
    }

    @Override
    public ClosureValueNode closureValueNode(TokenRange tokenRange, int groovyIndex) {
        return this.objectFactory.get(ClosureValueNode.class, tokenRange, groovyIndex);
    }

    @Override
    public EmptyClosureValueNode emptyClosureValueNode(TokenRange tokenRange) {
        return this.objectFactory.get(EmptyClosureValueNode.class, tokenRange);
    }

    @Override
    public ComponentValueNode componentValueNode(TokenRange tokenRange, ComponentNode componentNode) {
        return this.objectFactory.get(ComponentValueNode.class, tokenRange, componentNode);
    }

    @Override
    public PlainScriptletNode plainScriptletNode(TokenRange tokenRange, int groovyIndex) {
        return this.objectFactory.get(PlainScriptletNode.class, tokenRange, groovyIndex);
    }

    @Override
    public DollarScriptletNode dollarScriptletNode(TokenRange tokenRange, int groovyIndex) {
        return this.objectFactory.get(DollarScriptletNode.class, tokenRange, groovyIndex);
    }

    @Override
    public DollarReferenceNode dollarReferenceNode(TokenRange tokenRange, int groovyIndex) {
        return this.objectFactory.get(DollarReferenceNode.class, tokenRange, groovyIndex);
    }

}
