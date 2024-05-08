package groowt.view.web.runtime;

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
                final var childCollector = new DefaultWebViewComponentChildCollector(
                        cl.getTemplate(),
                        this.getWriter()
                );
                args[args.length - 1] = childCollector;
                cl.call(childCollector);
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
