package groowt.view.component.web.transpile;

import groowt.view.component.compiler.ComponentTemplateCompileException;
import groowt.view.component.compiler.ComponentTemplateCompileUnit;
import groowt.view.component.compiler.ComponentTemplateCompilerConfiguration;
import groowt.view.component.web.ast.node.BodyNode;
import groowt.view.component.web.ast.node.CompilationUnitNode;
import groowt.view.component.web.ast.node.PreambleNode;
import groowt.view.component.web.compiler.MultipleWebViewComponentCompileErrorsException;
import groowt.view.component.web.compiler.WebViewComponentTemplateCompileException;
import groowt.view.component.web.compiler.WebViewComponentTemplateCompileUnit;
import groowt.view.component.web.transpile.groovy.GroovyUtil;
import groowt.view.component.web.transpile.resolve.ClassLoaderComponentClassNodeResolver;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static groowt.view.component.web.transpile.TranspilerUtil.*;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class DefaultGroovyTranspiler implements GroovyTranspiler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultGroovyTranspiler.class);

    protected TranspilerConfiguration getConfiguration(
            ClassLoaderComponentClassNodeResolver classLoaderComponentClassNodeResolver
    ) {
        return SimpleTranspilerConfiguration.withDefaults(classLoaderComponentClassNodeResolver);
    }

    protected void addAutomaticImports(WebViewComponentModuleNode moduleNode, TranspilerConfiguration configuration) {
        configuration.getImports().forEach(moduleNode::addImport);
        configuration.getStaticImports().forEach(staticImport -> moduleNode.addStaticImport(
                staticImport.getV1(), staticImport.getV2(), staticImport.getV3()
        ));
        configuration.getStarImports().forEach(moduleNode::addStarImport);
        configuration.getStaticStarImports().forEach(moduleNode::addStaticStarImport);
    }

    protected WebViewComponentModuleNode initModuleNode(
            ComponentTemplateCompileUnit compileUnit,
            WebViewComponentSourceUnit sourceUnit,
            TranspilerConfiguration configuration
    ) {
        final var moduleNode = new WebViewComponentModuleNode(sourceUnit);
        sourceUnit.setModuleNode(moduleNode);

        final String defaultPackageName = compileUnit.getDefaultPackageName();
        if (!defaultPackageName.trim().isEmpty()) {
            moduleNode.setPackageName(defaultPackageName);
        }

        this.addAutomaticImports(moduleNode, configuration);
        return moduleNode;
    }

    protected ClassNode initMainClassNode(
            ComponentTemplateCompileUnit compileUnit,
            String templateClassName,
            WebViewComponentModuleNode moduleNode
    ) {
        final ClassNode mainClassNode = new ClassNode(
                compileUnit.getDefaultPackageName() + templateClassName,
                ACC_PUBLIC,
                ClassHelper.OBJECT_TYPE
        );
        mainClassNode.addInterface(TranspilerUtil.COMPONENT_TEMPLATE);
        moduleNode.addClass(mainClassNode);
        return mainClassNode;
    }

    protected void checkPreambleClasses(String templateName, List<ClassNode> classNodes) {
        final ClassNode offending = classNodes.stream()
                .filter(classNode -> classNode.getName().equals(templateName))
                .findAny()
                .orElse(null);
        if (offending != null) {
            throw new IllegalArgumentException(
                    templateName + " cannot define itself in the template. " +
                            "Remove the class with that name."
            );
        }
    }

    protected void handlePreamble(
            String templateClassName,
            PreambleNode preambleNode,
            ClassNode mainClassNode,
            WebViewComponentModuleNode moduleNode,
            PositionSetter positionSetter
    ) {
        final GroovyUtil.ConvertResult convertResult = GroovyUtil.convert(
                preambleNode.getGroovyCode().getAsValidGroovyCode()
        );
        final ModuleNode convertModuleNode = convertResult.moduleNode();

        final PositionVisitor positionVisitor = new PositionVisitor(positionSetter, preambleNode);

        convertModuleNode.getImports().forEach(moduleNode::addImport);
        convertModuleNode.getStarImports().forEach(moduleNode::addStarImport);
        convertModuleNode.getStaticImports().forEach(moduleNode::addStaticImport);
        convertModuleNode.getStaticStarImports().forEach(moduleNode::addStaticStarImport);
        positionVisitor.visitImports(moduleNode);

        // if user supplied a package, use it
        if (convertResult.moduleNode().hasPackage()) {
            moduleNode.setPackage(convertResult.moduleNode().getPackage());
            mainClassNode.setName(moduleNode.getPackageName() + templateClassName);
            positionVisitor.visitPackage(moduleNode.getPackage());
        }

        final BlockStatement preambleBlock = convertResult.blockStatement();
        if (!(preambleBlock == null
                || preambleBlock.isEmpty()
                || preambleBlock.getStatements().getFirst() instanceof ReturnStatement)
        ) {
            logger.warn(
                    "{} contains script statements which are not supported. Currently, only classes and"
                            + " methods are supported. The rest will be ignored.",
                    templateClassName
            );
        }

        // move methods from script class
        final ClassNode scriptClass = convertResult.scriptClass();
        if (scriptClass != null) {
            scriptClass.getMethods().stream()
                    .filter(method -> !(method.getName().equals("main") || method.getName().equals("run")))
                    .forEach(method -> {
                        mainClassNode.addMethod(method);
                        positionVisitor.visitMethod(method);
                    });
        }

        // handle classes
        final List<ClassNode> classNodes = convertResult.classNodes();
        this.checkPreambleClasses(templateClassName, classNodes);
        classNodes.stream()
                .filter(classNode -> classNode != convertResult.scriptClass())
                .forEach(classNode -> {
                    moduleNode.addClass(classNode);
                    positionVisitor.visitClass(classNode);
                });
    }

    protected ExpressionStatement constructRenderContext(
            ClassNode renderContextImplementation,
            Variable componentContextParam,
            Variable writerParam,
            VariableExpression renderContextVariableExpr
    ) {
        final ConstructorCallExpression renderContextConstructor = new ConstructorCallExpression(
                renderContextImplementation,
                new ArgumentListExpression(
                        new VariableExpression(componentContextParam),
                        new VariableExpression(writerParam)
                )
        );
        final BinaryExpression renderContextAssignExpr = new DeclarationExpression(
                renderContextVariableExpr,
                getAssignToken(),
                renderContextConstructor
        );
        return new ExpressionStatement(renderContextAssignExpr);
    }

    protected ExpressionStatement assignComponentContextRenderContext(
            Parameter componentContextParam,
            VariableExpression renderContextVariableExpr
    ) {
        final BinaryExpression componentContextRenderContextAssign = new BinaryExpression(
                new PropertyExpression(new VariableExpression(componentContextParam), "renderContext"),
                getAssignToken(),
                renderContextVariableExpr
        );
        return new ExpressionStatement(componentContextRenderContextAssign);
    }

    protected ExpressionStatement assignWriterRenderContext(
            Parameter writerParam,
            VariableExpression renderContextVariableExpr
    ) {
        final BinaryExpression writerRenderContextAssign = new BinaryExpression(
                new PropertyExpression(new VariableExpression(writerParam), "renderContext"),
                getAssignToken(),
                renderContextVariableExpr
        );
        return new ExpressionStatement(writerRenderContextAssign);
    }

    protected ExpressionStatement assignWriterComponentContext(Parameter writerParam, Parameter componentContextParam) {
        final BinaryExpression writerComponentContextAssign = new BinaryExpression(
                new PropertyExpression(new VariableExpression(writerParam), "componentContext"),
                getAssignToken(),
                new VariableExpression(componentContextParam)
        );
        return new ExpressionStatement(writerComponentContextAssign);
    }

    protected Statement handleBody(
            BodyNode bodyNode,
            TranspilerConfiguration transpilerConfiguration,
            TranspilerState state
    ) {
        return transpilerConfiguration.getBodyTranspiler().transpileBody(bodyNode, state);
    }

    @Override
    public WebViewComponentSourceUnit transpile(
            ComponentTemplateCompilerConfiguration compilerConfiguration,
            WebViewComponentTemplateCompileUnit compileUnit,
            CompilationUnitNode compilationUnitNode,
            String templateClassSimpleName
    ) throws ComponentTemplateCompileException {
        // resolver, transpilerConfiguration, and positionSetter
        final ClassLoaderComponentClassNodeResolver resolver = new ClassLoaderComponentClassNodeResolver(
                compileUnit,
                compilerConfiguration.getGroovyClassLoader()
        );
        final var transpilerConfiguration = this.getConfiguration(resolver);
        final PositionSetter positionSetter = transpilerConfiguration.getPositionSetter();

        // prepare sourceUnit
        final CompilerConfiguration groovyCompilerConfiguration =
                compilerConfiguration.getGroovyCompilerConfiguration();
        final WebViewComponentSourceUnit sourceUnit = new WebViewComponentSourceUnit(
                compileUnit.getDescriptiveName(),
                compileUnit.getGroovyReaderSource(),
                groovyCompilerConfiguration,
                compilerConfiguration.getGroovyClassLoader(),
                new ErrorCollector(groovyCompilerConfiguration)
        );

        // prepare moduleNode
        final WebViewComponentModuleNode moduleNode = this.initModuleNode(
                compileUnit, sourceUnit, transpilerConfiguration
        );

        // set resolver's moduleNode
        resolver.setModuleNode(moduleNode);

        // prepare mainClassNode
        final ClassNode mainClassNode = this.initMainClassNode(compileUnit, templateClassSimpleName, moduleNode);

        // handle preamble
        final PreambleNode preambleNode = compilationUnitNode.getPreambleNode();
        if (preambleNode != null) {
            this.handlePreamble(templateClassSimpleName, preambleNode, mainClassNode, moduleNode, positionSetter);
        }

        // getRenderer method and render closure
        // first, getRenderer params
        final Parameter componentContextParam = new Parameter(COMPONENT_CONTEXT_TYPE, COMPONENT_CONTEXT_NAME);
        final Parameter writerParam = new Parameter(COMPONENT_WRITER_TYPE, COMPONENT_WRITER_NAME);
        final VariableExpression renderContextVariable = new VariableExpression(
                RENDER_CONTEXT_NAME,
                WEB_VIEW_COMPONENT_RENDER_CONTEXT_TYPE
        );

        // returned closure body
        final BlockStatement renderBlock = new BlockStatement();

        // init renderContext, componentContext, and writer properties
        renderBlock.addStatement(this.constructRenderContext(
                transpilerConfiguration.getRenderContextImplementation(),
                componentContextParam,
                writerParam,
                renderContextVariable
        ));
        renderBlock.addStatement(this.assignComponentContextRenderContext(
                componentContextParam, renderContextVariable
        ));
        renderBlock.addStatement(this.assignWriterRenderContext(writerParam, renderContextVariable));
        renderBlock.addStatement(this.assignWriterComponentContext(writerParam, componentContextParam));

        // init transpiler state
        final TranspilerState state = TranspilerState.withRootScope(
                componentContextParam,
                writerParam,
                renderContextVariable
        );
        renderBlock.setVariableScope(state.getCurrentScope()); // root scope

        // body
        final BodyNode bodyNode = compilationUnitNode.getBodyNode();
        if (bodyNode != null) {
            renderBlock.addStatement(this.handleBody(bodyNode, transpilerConfiguration, state));
        }

        // return null from render closure
        renderBlock.addStatement(new ReturnStatement(ConstantExpression.NULL));

        // make the closure
        final ClosureExpression renderer = new ClosureExpression(
                new Parameter[] { componentContextParam, writerParam },
                renderBlock
        );

        // getRenderer() return statement
        final Statement returnRendererStmt = new ReturnStatement(renderer);

        // getRenderer() return type is Closure<Void>
        final var voidClosure = ClassHelper.CLOSURE_TYPE.getPlainNodeReference();
        voidClosure.setGenericsTypes(new GenericsType[] { new GenericsType(ClassHelper.void_WRAPPER_TYPE) });

        // getRenderer method
        final var getRenderer = new MethodNode(
                GET_RENDERER,
                ACC_PUBLIC,
                voidClosure,
                Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,
                returnRendererStmt
        );
        mainClassNode.addMethod(getRenderer);

        // check for errors
        if (state.hasErrors()) {
            final List<ComponentTemplateCompileException> errors = state.getErrors();
            if (errors.size() == 1) {
                throw new WebViewComponentTemplateCompileException(compileUnit, errors.getFirst());
            } else {
                throw new MultipleWebViewComponentCompileErrorsException(compileUnit, errors);
            }
        }

        // return the sourceUnit for later processing
        return sourceUnit;
    }

}
