package groowt.view.component.web.ast.extension;

import groowt.util.di.annotation.Given;
import groowt.view.component.web.antlr.TokenList;
import groowt.view.component.web.ast.node.Node;
import groowt.view.component.web.util.TokenRange;
import jakarta.inject.Inject;

@Deprecated
public non-sealed class GStringScriptletExtension extends GStringNodeExtension {

    @Inject
    public GStringScriptletExtension(TokenList allTokens, @SelfNode Node self, @Given TokenRange rawTokenRange) {
        super(self, allTokens.getRange(rawTokenRange));
    }

}
