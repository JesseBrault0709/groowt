package groowt.view.component.web.runtime;

import groovy.lang.Closure;
import groowt.view.component.ViewComponent;
import groowt.view.component.context.ComponentContext;
import groowt.view.component.runtime.AbstractRenderContext;
import groowt.view.component.runtime.ComponentWriter;
import groowt.view.component.web.WebViewComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultWebViewRenderContext extends AbstractRenderContext implements WebViewComponentRenderContext {

    DefaultWebViewRenderContext(ComponentContext componentContext, ComponentWriter writer) {
        super(componentContext, writer);
    }

    @Override
    public WebViewComponent create(
            Resolved<? extends WebViewComponent> resolved,
            Map<String, Object> attr,
            Object[] constructorArgs
    ) {
        final WebViewComponent created;
        if (resolved instanceof ResolvedStringType<? extends WebViewComponent> resolvedStringType) {
            created = resolvedStringType.componentFactory().create(
                    resolvedStringType.typeName(),
                    this.getComponentContext(),
                    attr,
                    constructorArgs
            );
        } else if (resolved instanceof ResolvedClassType<? extends WebViewComponent> resolvedClassType) {
            created = resolvedClassType.componentFactory().create(
                    resolvedClassType.alias(),
                    resolvedClassType.requestedType(),
                    this.getComponentContext(),
                    attr,
                    constructorArgs
            );
        } else {
            throw new UnsupportedOperationException(
                    "Cannot create from a Resolved that is not a ResolvedStringType or ResolvedClassType."
            );
        }
        created.setContext(this.getComponentContext());
        created.setChildren(new ArrayList<>());
        return created;
    }

    @Override
    public WebViewComponent create(
            Resolved<? extends WebViewComponent> resolved,
            Map<String, Object> attr,
            Object[] constructorArgs,
            Closure<Void> childrenClosure
    ) {
        final WebViewComponent created = this.create(resolved, attr, constructorArgs);
        childrenClosure.setDelegate(created);
        childrenClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
        final List<Object> children = new ArrayList<>();
        childrenClosure.call(children);
        created.setChildren(children);
        return created;
    }

    @Override
    public ViewComponent createFragment(WebViewComponent fragment, Closure<Void> childrenClosure) {
        final List<Object> children = new ArrayList<>();
        childrenClosure.call(children);
        fragment.setChildren(children);
        fragment.setContext(this.getComponentContext());
        return fragment;
    }

}
