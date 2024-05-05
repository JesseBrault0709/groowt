package groowt.view;

import groovy.lang.Closure;
import groovy.lang.Writable;
import groovy.text.GStringTemplateEngine;
import groovy.text.Template;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Delegates to self.
 */
public class GStringTemplateView implements View {

    private final GStringTemplateEngine engine;
    private final Object src;
    private final Charset charset;
    private final Template template;

    private final ThreadLocal<Writer> currentWriter = new ThreadLocal<>();
    private final ThreadLocal<Closure<CharSequence>> yieldClosure = new ThreadLocal<>();

    public GStringTemplateView(Map<String, Object> args) {
        this.engine = (GStringTemplateEngine) args.getOrDefault("engine", new GStringTemplateEngine());
        if (!args.containsKey("src")) {
            throw new IllegalArgumentException("args.src must not be null");
        }
        this.src = args.get("src");
        this.charset = switch (args.get("charset")) {
            case null -> null;
            case Charset c -> c;
            default -> throw new IllegalArgumentException("args.charset, if not null, must be an instance of Charset");
        };
        try {
            switch (this.src) {
                case String s -> this.template = this.engine.createTemplate(s);
                case File f -> {
                    if (this.charset != null) {
                        this.template = this.engine.createTemplate(f, this.charset);
                    } else {
                        this.template = this.engine.createTemplate(f);
                    }
                }
                case Reader r -> this.template = this.engine.createTemplate(r);
                case URL url -> {
                    if (this.charset != null) {
                        this.template = this.engine.createTemplate(url, this.charset);
                    } else {
                        this.template = this.engine.createTemplate(url);
                    }
                }
                default -> throw new IllegalArgumentException("args.src must be a String, File, Reader, or URL.");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void renderTo(Writer writer) throws IOException {
        this.currentWriter.set(writer);
        final Closure<?> closure = (Closure<?>) this.template.make();
        closure.setDelegate(this);
        closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        final Writable writable = (Writable) closure;
        writable.writeTo(writer);
        this.currentWriter.remove();
    }

    public String render(Closure<CharSequence> yieldClosure) {
        this.yieldClosure.set(yieldClosure);
        final String result = this.render();
        this.yieldClosure.remove();
        return result;
    }

    public void renderTo(Writer writer, Closure<CharSequence> yieldClosure) throws IOException {
        this.yieldClosure.set(yieldClosure);
        this.renderTo(writer);
        this.yieldClosure.remove();
    }

    public CharSequence yield(Object... args) {
        final Closure<?> yieldClosure = this.yieldClosure.get();
        if (yieldClosure == null) {
            throw new IllegalStateException("Cannot yield in a GStringTemplateView without passing a Closure to render() or renderTo().");
        }
        final Class<?>[] paramTypes = yieldClosure.getParameterTypes();

        // if Writer is first param, pass that
        if (paramTypes.length > 0) {
            final Class<?> firstParamType = paramTypes[0];
            if (Writer.class.isAssignableFrom(firstParamType)) {
                yieldClosure.call(this.currentWriter.get(), args);
                return "";
            }
        }

        // else just give whatever was passed to yield
        final Object returned = yieldClosure.call(args);
        if (returned instanceof CharSequence cs) {
            return cs;
        } else {
            throw new RuntimeException("The yield Closure must return an instance of CharSequence or a subtype thereof; given: " + returned + " of type " + returned.getClass());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public String partial(Object... args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Must provide at least 1 argument. Signature: partial(String | File | Reader | URL | Map, Map | Closure | [Map, Closure])");
        }

        final Object arg0 = args[0];
        final Object arg1 = args.length >= 2 ? args[1] : null;
        final Object arg2 = args.length >= 3 ? args[2] : null;

        final Map<String, Object> createArgs = new HashMap<>();

        switch (arg0) {
            case String s -> createArgs.put("src", new File(s));
            case File f -> createArgs.put("src", f);
            case Reader r -> createArgs.put("src", r);
            case URL url -> createArgs.put("src", url);
            case Map m -> createArgs.putAll(m);
            default -> throw new IllegalArgumentException("First argument must be any of String, File, Reader, URL, or Map.");
        }

        if (!createArgs.containsKey("engine")) {
            createArgs.put("engine", this.engine);
        }
        if (!createArgs.containsKey("charset") && this.charset != null) {
            createArgs.put("charset", this.charset);
        }
        if (!createArgs.containsKey("parent")) {
            createArgs.put("parent", this);
        }

        switch (arg1) {
            case null -> {
                final View partial = new StandardGStringTemplateView(createArgs);
                try {
                    partial.renderTo(this.currentWriter.get());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            case Map m -> {
                createArgs.put("locals", m);
                final GStringTemplateView view = new StandardGStringTemplateView(createArgs);
                final Closure<CharSequence> cl = switch (arg2) {
                    case null -> null;
                    case Closure closure -> closure;
                    default -> throw new IllegalArgumentException("Third argument, if not null, must be a Closure.");
                };
                try {
                    if (cl != null) {
                        view.renderTo(this.currentWriter.get(), cl);
                    } else {
                        view.renderTo(this.currentWriter.get());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case Closure cl -> {
                final GStringTemplateView view = new StandardGStringTemplateView(createArgs);
                try {
                    view.renderTo(this.currentWriter.get(), cl);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> throw new IllegalArgumentException("Second argument, if not null, must be a Map or a Closure.");
        }
        
        return "";
    }

    public URL templateResource(String name) {
        return this.getClass().getResource(name);
    }

    @Override
    public String toString() {
        return String.format("GStringTemplateView(src: %s)", this.src);
    }

}
