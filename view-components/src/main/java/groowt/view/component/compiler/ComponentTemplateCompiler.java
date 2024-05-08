package groowt.view.component.compiler;

public interface ComponentTemplateCompiler<U extends ComponentTemplateCompileUnit> {

    ComponentTemplateCompileResult compile(U compileUnit) throws ComponentTemplateCompileException;

}
