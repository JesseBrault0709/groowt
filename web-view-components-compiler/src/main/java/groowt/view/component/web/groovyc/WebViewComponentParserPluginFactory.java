package groowt.view.component.web.groovyc;

import org.apache.groovy.parser.antlr4.Antlr4ParserPlugin;
import org.codehaus.groovy.control.ParserPlugin;
import org.codehaus.groovy.control.ParserPluginFactory;

public class WebViewComponentParserPluginFactory extends ParserPluginFactory {

    @Override
    public ParserPlugin createParserPlugin() {
        return new DelegatingWebViewComponentTemplateParserPlugin(new Antlr4ParserPlugin());
    }

}
