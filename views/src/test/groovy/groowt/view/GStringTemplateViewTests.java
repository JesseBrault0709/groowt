package groowt.view;

import groovy.lang.Closure;
import groovy.text.GStringTemplateEngine;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GStringTemplateViewTests {

    private static final class GreetingView extends GStringTemplateView {

        public GreetingView(GStringTemplateEngine engine) {
            super(Map.of(
                    "engine", engine,
                    "src", "<%= greeting %>"
            ));
        }

        public String getGreeting() {
            return "Hello, world!";
        }

    }

    private final GStringTemplateEngine engine = new GStringTemplateEngine();

    @Test
    public void helloWorld() {
        final var view = new GreetingView(this.engine);
        assertEquals("Hello, world!", view.render());
    }

    @Test
    public void coerceToClosureCallNoArgs() {
        final var view = new GreetingView(this.engine);
        final var cl = view.asClosure();
        assertEquals("Hello, world!", cl.call());
    }

    @Test
    public void coerceToClosureCallWithWriter() {
        final var view = new GreetingView(this.engine);
        final var cl = view.asClosure();
        final var w = new StringWriter();
        cl.call(w);
        assertEquals("Hello, world!", w.toString());
    }

    @Test
    public void coerceToWritable() throws IOException {
        final var view = new GreetingView(this.engine);
        final var writable = view.asWritable();
        final var w = new StringWriter();
        writable.call(w);
        assertEquals("Hello, world!", w.toString());
    }

    @Test
    public void yieldingViewSimple() {
        final var greetingView = new GreetingView(this.engine);
        final Closure<CharSequence> yieldClosure = new Closure<CharSequence>(this) {

            public String doCall() {
                return greetingView.render();
            }

        };
        final var view = new GStringTemplateView(Map.of(
                "engine", this.engine,
                "src", "Yielded: <%= yield() %>"
        ));
        assertEquals("Yielded: Hello, world!", view.render(yieldClosure));
    }

    @Test
    public void simplePartialWithLocal() {
        final var view = new GStringTemplateView(Map.of(
                "engine", this.engine,
                "src", "<%= partial templateResource('simplePartial.gst'), [greeting: 'Hello, World!'] %>"
        ));
        assertEquals("Hello from partial. Greeting: Hello, World!", view.render());
    }

}
