package groowt.view.web.transpile;

import groowt.view.web.antlr.TokenList;
import groowt.view.web.ast.node.PreambleNode;
import groowt.view.web.transpile.util.GroovyUtil;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultPreambleTranspiler implements PreambleTranspiler {

    @Override
    public PreambleResult getPreambleResult(
            @Nullable PreambleNode preambleNode,
            String templateName,
            TokenList tokens
    ) {
        if (preambleNode == null) {
            return new PreambleResult(null, null, List.of());
        } else {
            final Token groovyToken = tokens.getGroovyToken(preambleNode.getGroovyCodeIndex());
            final GroovyUtil.ConvertResult convertResult = GroovyUtil.convert(groovyToken.getText(), templateName);
            return new PreambleResult(
                    convertResult.moduleNode(),
                    convertResult.scriptClass(),
                    convertResult.classNodes()
            );
        }
    }

}
