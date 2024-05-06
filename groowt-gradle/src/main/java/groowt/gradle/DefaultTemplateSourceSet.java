package groowt.gradle;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.reflect.HasPublicType;
import org.gradle.api.reflect.TypeOf;
import org.gradle.util.internal.ConfigureUtil;

import javax.inject.Inject;

public class DefaultTemplateSourceSet implements TemplateSourceSet, HasPublicType {

    private final TemplateSourceDirectorySet templateSourceDirectorySet;
    private final SourceDirectorySet allTemplates;

    @Inject
    public DefaultTemplateSourceSet(ObjectFactory objectFactory, String name, String displayName) {
        this.templateSourceDirectorySet = objectFactory.newInstance(
                DefaultTemplateSourceDirectorySet.class,
                objectFactory.sourceDirectorySet(name, displayName + " ComponentTemplate sources")
        );
        this.templateSourceDirectorySet.getFilter().include("**/*.wvc", "**/*.gst");
        this.allTemplates = objectFactory.sourceDirectorySet(
                "all" + name,
                displayName + " ComponentTemplate sources"
        );
        this.allTemplates.source(this.templateSourceDirectorySet);
        this.allTemplates.getFilter().include("**/*.wvc", "**/*.gst");
    }

    @Override
    public TypeOf<?> getPublicType() {
        return TypeOf.typeOf(TemplateSourceSet.class);
    }

    @Override
    public TemplateSourceDirectorySet getTemplates() {
        return this.templateSourceDirectorySet;
    }

    @Override
    public TemplateSourceSet templates(Action<? super TemplateSourceDirectorySet> action) {
        action.execute(this.templateSourceDirectorySet);
        return this;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public TemplateSourceSet templates(Closure closure) {
        ConfigureUtil.configure(closure, this.templateSourceDirectorySet);
        return this;
    }

    @Override
    public SourceDirectorySet getAllTemplates() {
        return this.allTemplates;
    }

}
