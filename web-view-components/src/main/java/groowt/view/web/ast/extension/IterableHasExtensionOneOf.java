package groowt.view.web.ast.extension;

import groowt.util.di.filters.IterableFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IterableFilter
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface IterableHasExtensionOneOf {
    Class<? extends NodeExtension>[] value();
}
