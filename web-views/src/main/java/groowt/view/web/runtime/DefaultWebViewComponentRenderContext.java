package groowt.view.web.runtime;

import groovy.lang.Closure;
import groowt.view.component.ViewComponent;
import groowt.view.component.context.ComponentContext;
import groowt.view.component.runtime.ComponentWriter;
import groowt.view.component.runtime.DefaultRenderContext;
import groowt.view.web.WebViewComponent;
import org.jetbrains.annotations.ApiStatus;

public class DefaultWebViewComponentRenderContext extends DefaultRenderContext
        implements WebViewComponentRenderContext {

    DefaultWebViewComponentRenderContext(ComponentContext componentContext, ComponentWriter writer) {
        super(componentContext, writer);
    }

    @Override
    public ViewComponent create(Resolved<?> resolved, Object... args) {
        if (args != null && args.length > 0) {
            final Object last = args[args.length - 1];
            if (last instanceof WebViewComponentChildCollectorClosure cl) {
                final Object[] argsWithoutChildren = new Object[args.length - 1];
                System.arraycopy(args, 0, argsWithoutChildren, 0, args.length - 1);
                final WebViewComponent self = (WebViewComponent) super.create(resolved, argsWithoutChildren);
                final var childCollector = new DefaultWebViewComponentChildCollector(
                        cl.getTemplate(),
                        this.getWriter()
                );
                cl.setDelegate(self);
                cl.setResolveStrategy(Closure.DELEGATE_FIRST);
                cl.call(childCollector);
                self.setChildren(childCollector.getChildren());
                return self;
            }
        }
        return super.create(resolved, args);
    }

    @ApiStatus.Internal
    public ViewComponent createFragment(WebViewComponent fragment, WebViewComponentChildCollectorClosure cl) {
        final var childCollection = new DefaultWebViewComponentChildCollector(cl.getTemplate(), this.getWriter());
        cl.call(childCollection);
        fragment.setChildren(childCollection.getChildren());
        return fragment;
    }

}
