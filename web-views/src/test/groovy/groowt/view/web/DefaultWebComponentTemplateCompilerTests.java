package groowt.view.web;

import groowt.view.component.*;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultWebComponentTemplateCompilerTests {

    private static ComponentTemplate doCompile(Class<? extends ViewComponent> componentClass, Reader source) {
        final var compiler = new DefaultWebComponentTemplateCompiler(
                CompilerConfiguration.DEFAULT,
                componentClass.getPackageName()
        );
        return compiler.compile(componentClass, source);
    }

    private static ComponentTemplate doCompile(Class<? extends ViewComponent> componentClass, String source) {
        return doCompile(componentClass, new StringReader(source));
    }

    private static final class Greeter extends DefaultWebViewComponent {

        private final String target;

        public Greeter(Map<String, Object> attr) {
            super(doCompile(Greeter.class, "Hello, $target!"));
            this.target = (String) Objects.requireNonNull(attr.get("target"));
        }

        public String getTarget() {
            return this.target;
        }

    }

    private static final class GreeterFactory extends AbstractComponentFactory<Greeter> {

        public Greeter doCreate(Map<String, Object> attr) {
            return new Greeter(attr);
        }

    }

    private static final class UsingGreeter extends DefaultWebViewComponent {

        public UsingGreeter(ComponentContext context) {
            super(doCompile(UsingGreeter.class, "<Greeter target='World' />"));
            this.setContext(context);
        }

    }

    @Test
    public void usingGreeter() {
        final var context = new DefaultComponentContext();
        final var scope = new DefaultComponentScope();
        scope.add("Greeter", new GreeterFactory());
        context.pushScope(scope);

        final UsingGreeter usingGreeter = new UsingGreeter(context);
        assertEquals("Hello, World!", usingGreeter.render());
    }

}
