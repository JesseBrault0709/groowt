package groowt.view.web.ast.extension;

import groowt.util.di.annotation.Given;
import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.node.Node;
import groowt.view.web.util.TokenRange;
import jakarta.inject.Inject;

public non-sealed class GStringPathExtension extends GStringNodeExtension {

    @Inject
    public GStringPathExtension(TokenList allTokens, @SelfNode Node self, @Given TokenRange rawTokenRange) {
        super(self, allTokens.getRange(rawTokenRange));
    }

    @Override
    public String getAsValidEmbeddableCode() {
        return "$" + super.getAsValidEmbeddableCode();
    }

}