package groowt.view.component.web.ast.extension;

import groowt.util.di.filters.FilterHandler;
import groowt.util.di.filters.FilterHandlers;
import groowt.util.di.filters.IterableFilterHandler;
import groowt.util.di.filters.IterableFilterHandlers;
import groowt.view.component.web.ast.node.Node;

import java.util.Arrays;

public final class ExtensionUtil {

    private ExtensionUtil() {}

    public static final FilterHandler<HasExtensions, Node> hasExtensionsFilterHandler =
            FilterHandlers.of(HasExtensions.class, Node.class, (annotation, node) ->
                    Arrays.stream(annotation.value()).allMatch(node::hasExtension)
            );

    public static final IterableFilterHandler<IterableHasExtensions, Node> iterableHasExtensionsFilterHandler =
            IterableFilterHandlers.of(IterableHasExtensions.class, (annotation, node) ->
                    Arrays.stream(annotation.value()).allMatch(node::hasExtension)
            );

    public static final FilterHandler<HasExtensionOneOf, Node> hasExtensionOneOfNodeFilterHandler =
            FilterHandlers.of(HasExtensionOneOf.class, Node.class, (annotation, node) ->
                    Arrays.stream(annotation.value()).anyMatch(node::hasExtension)
            );

    public static final IterableFilterHandler<IterableHasExtensionOneOf, Node> iterableHasExtensionOneOfFilterHandler =
            IterableFilterHandlers.of(IterableHasExtensionOneOf.class, (annotation, node) ->
                    Arrays.stream(annotation.value()).anyMatch(node::hasExtension)
            );

}
