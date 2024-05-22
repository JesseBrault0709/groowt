package groowt.view.component.web.antlr;

import org.antlr.v4.runtime.CharStream;

public class WebViewComponentsLexer extends WebViewComponentsLexerBase {

    public WebViewComponentsLexer(CharStream input) {
        super(input);
        this._interp = new PositionAdjustingLexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public WebViewComponentsLexer() {
        super();
    }

    @Override
    protected PositionAdjustingLexerATNSimulator getPositionAdjustingInterpreter() {
        return (PositionAdjustingLexerATNSimulator) this.getInterpreter();
    }

}
