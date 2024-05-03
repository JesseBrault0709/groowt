package groowt.util.di;

import jakarta.inject.Qualifier;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

public interface QualifierHandlerContainer {

    static void checkIsValidQualifier(Class<? extends Annotation> annotationClass) {
        if (!annotationClass.isAnnotationPresent(Qualifier.class)) {
            throw new IllegalArgumentException(
                    "The given qualifier annotation + " + annotationClass + " is itself not annotated with @Qualifier"
            );
        }
    }

    <A extends Annotation> @Nullable QualifierHandler<A> getQualifierHandler(Class<A> qualifierType);

}
