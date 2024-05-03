package groowt.view.web.ast.extension;

import groowt.util.di.*;
import groowt.view.web.ast.node.Node;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

public final class SelfNodeRegistryExtension implements RegistryExtension, QualifierHandlerContainer {

    private static final class SelfNodeQualifierHandler implements QualifierHandler<SelfNode> {

        private final SelfNodeRegistryExtension extension;

        public SelfNodeQualifierHandler(SelfNodeRegistryExtension extension) {
            this.extension = extension;
        }

        @SuppressWarnings("unchecked")
        @Override
        public @Nullable <T> Binding<T> handle(SelfNode annotation, Class<T> dependencyClass) {
            return Node.class.isAssignableFrom(dependencyClass) ? (Binding<T>) this.extension.selfNodeBinding : null;
        }

    }

    private final QualifierHandler<SelfNode> handler = new SelfNodeQualifierHandler(this);
    private @Nullable Binding<Node> selfNodeBinding;

    public void setSelfNode(@Nullable Node self) {
        if (self == null) {
            this.selfNodeBinding = null;
        } else {
            this.selfNodeBinding = new SingletonBinding<>(self);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <A extends Annotation> QualifierHandler<A> getQualifierHandler(Class<A> qualifierType) {
        return SelfNode.class.equals(qualifierType) ? (QualifierHandler<A>) this.handler : null;
    }

}
