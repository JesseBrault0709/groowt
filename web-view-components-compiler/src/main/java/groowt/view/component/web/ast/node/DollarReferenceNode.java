package groowt.view.component.web.ast.node;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.ast.extension.NodeExtensionContainer;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;

import java.util.List;

public class DollarReferenceNode extends AbstractLeafNode implements GroovyBodyNode {

    private final List<String> parts;

    @Inject
    public DollarReferenceNode(
            NodeExtensionContainer extensionContainer,
            @Given TokenRange tokenRange,
            List<String> parts
    ) {
        super(tokenRange, extensionContainer);
        this.parts = parts;
    }

    public List<String> getParts() {
        return this.parts;
    }

}
