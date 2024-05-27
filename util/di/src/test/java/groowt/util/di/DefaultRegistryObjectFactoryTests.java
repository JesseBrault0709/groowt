package groowt.util.di;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.Test;

import static groowt.util.di.BindingUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class DefaultRegistryObjectFactoryTests {

    public interface Greeter {
        String greet();
    }

    public static final class DefaultGreeter implements Greeter {

        @Override
        public String greet() {
            return "Hello, World!";
        }

    }

    public static final class GivenArgGreeter implements Greeter {

        private final String greeting;

        @Inject
        public GivenArgGreeter(String greeting) {
            this.greeting = greeting;
        }

        @Override
        public String greet() {
            return this.greeting;
        }

    }

    public static final class InjectedArgGreeter implements Greeter {

        private final String greeting;

        @Inject
        public InjectedArgGreeter(String greeting) {
            this.greeting = greeting;
        }

        @Override
        public String greet() {
            return this.greeting;
        }

    }

    public static final class InjectedNamedArgGreeter implements Greeter {

        private final String greeting;

        @Inject
        public InjectedNamedArgGreeter(@Named("greeting") String greeting) {
            this.greeting = greeting;
        }

        @Override
        public String greet() {
            return this.greeting;
        }

    }

    public static final class InjectedNamedSetterGreeter implements Greeter {

        private String greeting;

        @Inject
        public void setGreeting(@Named("greeting") String greeting) {
            this.greeting = greeting;
        }

        @Override
        public String greet() {
            return this.greeting;
        }

    }

    @Test
    public void classSmokeScreen() {
        final var b = DefaultRegistryObjectFactory.Builder.withDefaults();
        b.configureRegistry(registry -> {
            registry.bind(Greeter.class, bc -> bc.to(DefaultGreeter.class));
        });
        final RegistryObjectFactory container = b.build();
        final Greeter greeter = container.get(Greeter.class);
        assertEquals("Hello, World!", greeter.greet());
    }

    @Test
    public void singletonSmokeScreen() {
        final var b = DefaultRegistryObjectFactory.Builder.withDefaults();
        b.configureRegistry(registry -> {
            registry.bind(Greeter.class, toSingleton(new DefaultGreeter()));
        });
        final RegistryObjectFactory container = b.build();
        final Greeter greeter = container.get(Greeter.class);
        assertEquals("Hello, World!", greeter.greet());
    }

    @Test
    public void providerSmokeScreen() {
        final var b = DefaultRegistryObjectFactory.Builder.withDefaults();
        b.configureRegistry(registry -> {
            registry.bind(Greeter.class, toProvider(DefaultGreeter::new));
        });
        final RegistryObjectFactory container = b.build();
        final Greeter greeter = container.get(Greeter.class);
        assertEquals("Hello, World!", greeter.greet());
    }

    @Test
    public void givenArgSmokeScreen() {
        final var b = DefaultRegistryObjectFactory.Builder.withDefaults();
        b.configureRegistry(registry -> {
            registry.bind(Greeter.class, bc -> bc.to(GivenArgGreeter.class));
        });
        final RegistryObjectFactory container = b.build();
        final Greeter greeter = container.get(Greeter.class, "Hello, World!");
        assertEquals("Hello, World!", greeter.greet());
    }

    @Test
    public void injectedArg() {
        final var b = DefaultRegistryObjectFactory.Builder.withDefaults();
        b.configureRegistry(registry -> {
            registry.bind(Greeter.class, bc -> bc.to(InjectedArgGreeter.class));
            registry.bind(String.class, toSingleton("Hello, World!"));
        });
        final RegistryObjectFactory container = b.build();
        final Greeter greeter = container.get(Greeter.class);
        assertEquals("Hello, World!", greeter.greet());
    }

    @Test
    public void injectedNamedArg() {
        final var b = DefaultRegistryObjectFactory.Builder.withDefaults();
        b.configureRegistry(registry -> {
            registry.bind(Greeter.class, bc -> bc.to(InjectedNamedArgGreeter.class));
            registry.bind(named("greeting", String.class), toSingleton("Hello, World!"));
        });
        final RegistryObjectFactory container = b.build();
        final Greeter greeter = container.get(Greeter.class);
        assertEquals("Hello, World!", greeter.greet());
    }

    @Test
    public void injectedSetter() {
        final var b = DefaultRegistryObjectFactory.Builder.withDefaults();
        b.configureRegistry(r -> {
            r.bind(Greeter.class, toClass(InjectedNamedSetterGreeter.class));
            r.bind(named("greeting", String.class), toSingleton("Hello, World!"));
        });
        final RegistryObjectFactory f = b.build();
        final Greeter greeter = f.get(Greeter.class);
        assertEquals("Hello, World!", greeter.greet());
    }

    public static final class GreeterDependency {

        private GreeterDependencyUser greeter;

        @Inject
        public void setGreeter(GreeterDependencyUser greeter) {
            this.greeter = greeter;
        }

        public String filterGreeting() {
            return this.greeter.getGreeting().toUpperCase();
        }

    }

    public static final class GreeterDependencyUser implements Greeter {

        private final GreeterDependency greeterDependency;

        @Inject
        public GreeterDependencyUser(GreeterDependency greeterDependency) {
            this.greeterDependency = greeterDependency;
        }

        @Override
        public String greet() {
            return this.greeterDependency.filterGreeting();
        }

        public String getGreeting() {
            return "hello, world!";
        }

    }

    @Test
    public void injectedDeferred() {
        final var b = DefaultRegistryObjectFactory.Builder.withDefaults();
        b.configureRegistry(r -> {
            r.bind(GreeterDependencyUser.class, toSelf());
            r.bind(GreeterDependency.class, toSelf());
        });
        final var f = b.build();
        final var g = f.get(GreeterDependencyUser.class);
        assertEquals("HELLO, WORLD!", g.greet());
    }

}
