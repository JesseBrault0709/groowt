package groowt.view.web.analysis.classes;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationFailedException;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public non-sealed class ClassLoaderClassLocator implements ClassLocator {

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderClassLocator.class);

    protected sealed interface CachedLocatedClass
            permits ClazzCachedLocatedClass, FailedGroovyCachedLocatedClass, CustomCachedLocatedClass {}

    protected record ClazzCachedLocatedClass(Class<?> cached) implements CachedLocatedClass {}

    protected record FailedGroovyCachedLocatedClass(
            CompilationFailedException exception) implements CachedLocatedClass {}

    protected non-sealed interface CustomCachedLocatedClass extends CachedLocatedClass {
        Class<?> get();
    }

    protected final ClassLoader classLoader;
    private final Map<String, CachedLocatedClass> cache = new HashMap<>();

    public ClassLoaderClassLocator(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoaderClassLocator() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    protected final void addToCache(String name, CachedLocatedClass cachedLocatedClass) {
        this.cache.put(name, cachedLocatedClass);
    }

    protected final boolean cacheHas(String name) {
        return this.cache.containsKey(name);
    }

    protected final void removeFromCacheIf(BiPredicate<? super String, ? super CachedLocatedClass> predicate) {
        final List<String> targets = new ArrayList<>();
        this.cache.forEach((name, cached) -> {
            if (predicate.test(name, cached)) {
                targets.add(name);
            }
        });
        targets.forEach(this.cache::remove);
    }

    protected final <T extends CachedLocatedClass> void removeFromCacheIf(Class<T> ofType, BiPredicate<? super String, T> predicate) {
        this.removeFromCacheIf((name, cached) -> {
            if (ofType.isAssignableFrom(cached.getClass())) {
                return predicate.test(name, ofType.cast(cached));
            }
            return false;
        });
    }

    protected final <T extends CachedLocatedClass> void removeFromCacheByType(Class<T> type) {
        this.removeFromCacheIf((name, cached) -> type.isAssignableFrom(cached.getClass()));
    }

    protected final <T extends CachedLocatedClass> Map<String, T> getFromCacheByType(Class<T> type) {
        final Map<String, T> result = new HashMap<>();
        this.cache.forEach((name, cached) -> {
            if (type.isAssignableFrom(cached.getClass())) {
                result.put(name, type.cast(cached));
            }
        });
        return result;
    }

    protected final @Nullable Class<?> loadFromCache(String name) {
        final var cachedLocated = this.cache.getOrDefault(name, null);
        if (cachedLocated == null) {
            return null;
        } else {
            return switch (cachedLocated) {
                case ClazzCachedLocatedClass(var cached) -> cached;
                case CustomCachedLocatedClass custom -> custom.get();
                case FailedGroovyCachedLocatedClass(var exception) ->
                        throw new RuntimeException("Cannot load Groovy class because compilation failed.", exception);
            };
        }
    }

    protected @Nullable Class<?> searchClassLoader(String name) {
        if (classLoader instanceof GroovyClassLoader gcl) {
            try {
                Class<?> clazz = gcl.loadClass(name, true, true, false);
                this.addToCache(name, new ClazzCachedLocatedClass(clazz));
                return clazz;
            } catch (ClassNotFoundException ignored) {
                // Ignored
            } catch (CompilationFailedException cfe) {
                logger.warn("Could not compile class: {}", name);
                this.addToCache(name, new FailedGroovyCachedLocatedClass(cfe));
                // return null because we don't actually have a class
                return null;
            }
        } else {
            try {
                Class<?> clazz = classLoader.loadClass(name);
                this.addToCache(name, new ClazzCachedLocatedClass(clazz));
                return clazz;
            } catch (ClassNotFoundException ignored) {}
        }        return null;
    }

    @Override
    public boolean hasClassForFQN(String name) {
        return this.cacheHas(name) || this.searchClassLoader(name) != null;
    }

    public void clearCache() {
        this.cache.clear();
    }

}