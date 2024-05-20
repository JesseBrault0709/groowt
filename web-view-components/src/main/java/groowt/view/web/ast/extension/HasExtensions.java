package groowt.view.web.ast.extension;

import groowt.util.di.filters.Filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Filter
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface HasExtensions {
    Class<? extends NodeExtension>[] value();
}
