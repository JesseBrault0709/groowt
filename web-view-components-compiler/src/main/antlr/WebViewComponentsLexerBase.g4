lexer grammar WebViewComponentsLexerBase;

import LexerFragments;

options {
    superClass = AbstractWebViewComponentsLexer;
}

tokens {
    PreambleBreak,
    GroovyCode,
    GStringAttrValueEnd,
    JStringAttrValueEnd,
    ClosureAttrValueEnd,
    DollarScriptletClose
}

@header {
    import java.util.Set;
    import static groowt.view.component.web.antlr.LexerSemanticPredicates.*;
}

@members {

    public static final Set<Integer> GroovyTokens = Set.of(
        GroovyCodeChars,
        LeftParenthesis,
        RightParenthesis,
        LeftCurly,
        RightCurly,
        LineCommentStart,
        StarCommentStart,
        JStringStart,
        GStringStart,
        TripleJStringStart,
        TripleGStringStart,
        ParenthesesSlashyStringStart,
        DollarSlashyStringStart,
        LineCommentText,
        LineCommentEnd,
        StarCommentChars,
        StarCommentEnd,
        JStringText,
        JStringEnd,
        GStringText,
        GStringDollarValueStart,
        GStringClosureStart,
        GStringEnd,
        GStringIdentifier,
        GStringDot,
        GStringPathEnd,
        GStringIdentifierStartChar,
        GStringIdentifierChar,
        TripleJStringContent,
        TripleJStringEnd,
        TripleGStringDollarValueStart,
        TripleGStringClosureStart,
        TripleGStringText,
        TripleGStringEnd,
        ParenthesesSlashyStringText,
        ParenthesesSlashyStringDollarValueStart,
        ParenthesesSlashyStringClosureStart,
        ParenthesesSlashyStringEnd,
        DollarSlashyStringText,
        DollarSlashyStringDollarValueStart,
        DollarSlashyStringClosureStart,
        DollarSlashyStringEnd
    );

    public static final Set<Integer> GStringParts = Set.of(
        GStringDollarValueStart,
        GStringClosureStart,
        GStringIdentifier,
        GStringDot,
        GStringPathEnd,
        GStringIdentifierStartChar,
        GStringIdentifierChar,
        TripleGStringDollarValueStart,
        TripleGStringClosureStart,
        ParenthesesSlashyStringDollarValueStart,
        ParenthesesSlashyStringClosureStart,
        DollarSlashyStringDollarValueStart,
        DollarSlashyStringClosureStart
    );

    private void onPreambleClose() {
        this.setType(PreambleBreak);
        this.exitPreamble();
        this.popMode();
    }

    private void onGStringClosure() {
        this.curlies.push(this::popMode); // onPop
        this.curlies.increment(); // after, curlies.currentCount == 1
        this.pushMode(GROOVY_CODE);
    }

    @Override
    protected void enterConstructor() {
        super.enterConstructor(); // setup state
        this.pushMode(GROOVY_CODE);
    }

}

// ----------------------------------------
// DEFAULT_MODE

PreambleOpen
    :   THREE_DASH ( NL | WS+ )? { this.canPreamble() }? { this.enterPreamble(); }
        -> type(PreambleBreak), pushMode(GROOVY_CODE)
    ;

ComponentOpen
    :   LT -> pushMode(IN_TAG)
    ;

ClosingComponentOpen
    :   LT FS -> pushMode(IN_TAG)
    ;

EqualsScriptletOpen
    :   LT PERCENT EQ -> pushMode(GROOVY_CODE)
    ;

PlainScriptletOpen
    :   LT PERCENT -> pushMode(GROOVY_CODE)
    ;

DollarScriptletOpen
    :   DOLLAR LEFT_CURLY {
            this.curlies.push(() -> {
                this.setType(DollarScriptletClose);
                this.popMode();
            });
            this.curlies.increment();
            this.pushMode(GROOVY_CODE);
        }
    ;

DollarReferenceStart
    :   DOLLAR { isIdentifierStartChar(this.getNextChar()) }? -> pushMode(IN_G_STRING_PATH)
    ;

QuestionTag
    :   LT QUESTION .*? QUESTION GT
    ;

HtmlComment
    :   LT BANG TWO_DASH .*? TWO_DASH GT
    ;

RawText
    :   (   ~[-<$] // n.b.: LT cannot be escaped, only via &lt;
        |   MINUS { !this.isNext("--") }?
        |   DOLLAR { !this.isNext('{') && !isIdentifierStartChar(this.getNextChar()) }?
        |   LT BANG { !this.isNext("--") }?
        )+
    ;

// ----------------------------------------
mode IN_TAG;

ComponentSelfClose
    :   FS GT -> popMode
    ;

ComponentClose
    :   GT -> popMode
    ;

Identifier
    :   IdentifierStartChar IdentifierChar*
    ;

IdentifierStartChar
    :   ~[.] { isIdentifierStartChar(this.getCurrentChar()) }?
    ;

IdentifierChar
    :   ~[.] { isIdentifierChar(this.getCurrentChar()) }?
    ;

ConstructorOpen
    :   LP { this.enterConstructor(); }
    ;

Dot
    :   DOT
    ;

Equals
    :   EQ
    ;

GStringAttrValueStart
    :   DQ -> pushMode(IN_G_STRING)
    ;

JStringAttrValueStart
    :   SQ -> pushMode(IN_J_STRING)
    ;

ClosureAttrValueStart
    :   LEFT_CURLY ComponentNlws? { !this.isNext('<') }?
        {
            this.curlies.push(() -> {
                this.setType(ClosureAttrValueEnd);
                this.popMode();
            });
            this.curlies.increment();
            this.pushMode(GROOVY_CODE);
        }
    ;

ComponentAttrValueStart
    :   LEFT_CURLY ComponentNlws? { this.isNext('<') }?
    ;

ComponentAttrValueEnd
    :   GT RIGHT_CURLY
    ;

ComponentNlws
    :   NLWS+
    ;

// ----------------------------------------
mode GROOVY_CODE;

PreambleClose
    :   NL? THREE_DASH ( NL | WS+ )? { this.inPreamble() }?
        {
            this.onPreambleClose();
        }
    ;

ScriptletClose
    :   PERCENT GT -> popMode
    ;

GroovyCodeChars
    :   (   ~[/\n\r\-$%(){}'"]
        |   FS { !isAnyOf(this.getNextChar(), '/', '*') }?
        |   NL { !this.inPreamble() || !this.isNext("---") }?
        |   MINUS { !(this.getCharPositionInLine() == 1 && this.isNext("--")) }?
        |   DOLLAR { !this.isNext('/') }?
        |   PERCENT { !this.isNext('>') }?
        )+
    ;

LeftParenthesis
    :   LP { !this.isNext('/') }?
        {
            if (this.parentheses.isCounting()) {
                this.parentheses.increment();
            }
        }
    ;

ConstructorClose
    :   RP { this.canExitConstructor() }? { this.exitConstructor(); }
    ;

RightParenthesis
    :   RP { !this.inConstructor() }?
        {
            if (this.parentheses.isCounting()) {
                this.parentheses.decrement();
            }
        }
    ;

LeftCurly
    :   LEFT_CURLY
        {
            if (this.curlies.isCounting()) {
                this.curlies.increment();
            }
        }
    ;

RightCurly
    :   RIGHT_CURLY
        {
            if (this.curlies.isCounting()) {
                if (this.curlies.isLast()) {
                    this.curlies.pop(); // calls this.pop() in onPop
                } else {
                    this.curlies.decrement();
                }
            }
        }
    ;

LineCommentStart
    :   FS FS -> pushMode(IN_LINE_COMMENT)
    ;

StarCommentStart
    :   FS STAR -> pushMode(IN_STAR_COMMENT)
    ;

JStringStart
    :   SQ { canFollowJStringOpening(this.getNextCharsAsString(2)) }? -> pushMode(IN_J_STRING)
    ;

GStringStart
    :   DQ { canFollowGStringOpening(this.getNextCharsAsString(2)) }? -> pushMode(IN_G_STRING)
    ;

TripleJStringStart
    :   SQ SQ SQ -> pushMode(IN_TRIPLE_J_STRING)
    ;

TripleGStringStart
    :   DQ DQ DQ -> pushMode(IN_TRIPLE_G_STRING)
    ;

ParenthesesSlashyStringStart
    :   LP FS { !isAnyOf(this.getNextChar(), '/', '*') }? -> pushMode(IN_PARENTHESES_SLASHY_STRING)
    ;

DollarSlashyStringStart
    :   DOLLAR FS -> pushMode(IN_DOLLAR_SLASHY_STRING)
    ;

// ----------------------------------------
mode IN_LINE_COMMENT;

LineCommentText
    :   ~[\n\r]+
    ;

LineCommentEnd
    :   NL -> popMode
    ;

// ----------------------------------------
mode IN_STAR_COMMENT;

StarCommentChars
    :   ~'*'+
    ;

StarCommentEnd
    :   STAR FS -> popMode
    ;

// ----------------------------------------
mode IN_J_STRING;

JStringText
    :   ( ~[\n\r'] | BS SQ )+
    ;

JStringEnd
    :   SQ
        {
            if (this.peekMode(1) == IN_TAG) {
                this.setType(JStringAttrValueEnd);
            }
            this.popMode();
        }
    ;

// ----------------------------------------
mode IN_G_STRING;

GStringText
    :   (   ~[\n\r"$]
        |   BS DQ
        |   BS DOLLAR
        )+
    ;

GStringDollarValueStart
    :   DOLLAR { !this.isNext('{') }? -> pushMode(IN_G_STRING_PATH)
    ;

GStringClosureStart
    :   DOLLAR LEFT_CURLY { this.onGStringClosure(); }
    ;

GStringEnd
    :   DQ
        {
            if (this.peekMode(1) == IN_TAG) {
                this.setType(GStringAttrValueEnd);
            }
            this.popMode();
        }
    ;

// ----------------------------------------
mode IN_G_STRING_PATH;

GStringIdentifier
    :   GStringIdentifierStartChar GStringIdentifierChar*
    ;

GStringDot
    :   DOT { isGStringIdentifierStartChar(this.getNextChar()) }?
    ;

GStringPathEnd
    :   ~[."/] { !isGStringIdentifierChar(this.getCurrentChar()) }?
        {
            final var current = this.getCurrentChar();
            final GStringPathEndSpec endSpec = switch (this.peekMode(1)) {
                case IN_G_STRING -> {
                    if (current == '"') {
                        yield new StringClosingEndSpec(GStringEnd, 2);
                    } else {
                        yield new StringContinueEndSpec();
                    }
                }
                case IN_TRIPLE_G_STRING -> {
                    if (current == '"' && this.isNext("\"\"")) {
                        yield new StringClosingEndSpec(TripleGStringEnd, 2);
                    } else {
                        yield new StringContinueEndSpec();
                    }
                }
                case IN_PARENTHESES_SLASHY_STRING -> {
                    if (current == '/' && this.isNext(')')) {
                        yield new StringClosingEndSpec(ParenthesesSlashyStringEnd, 2);
                    } else {
                        yield new StringContinueEndSpec();
                    }
                }
                case IN_DOLLAR_SLASHY_STRING -> {
                    if (current == '/' && this.isNext('$')) {
                        yield new StringClosingEndSpec(DollarSlashyStringEnd, 2);
                    } else {
                        yield new StringContinueEndSpec();
                    }
                }
                case IN_TAG -> {
                    if (current == '"') {
                        yield new StringClosingEndSpec(GStringAttrValueEnd, 2);
                    } else {
                        yield new StringContinueEndSpec();
                    }
                }
                case DEFAULT_MODE -> new StringContinueEndSpec();
                default -> throw new IllegalStateException("not a valid gString context: " + this.getModeName(this.peekMode(1)));
            };
            switch (endSpec) {
                case StringContinueEndSpec ignored -> {
                    this.popMode();
                    this.rollbackOne();
                }
                case StringClosingEndSpec closingEndSpec -> {
                    this.setType(closingEndSpec.type());
                    for (int i = 0; i < closingEndSpec.popCount(); i++) {
                        this.popMode();
                    }
                }
            }
        }
    ;

GStringIdentifierStartChar
    :   ~'.' { isGStringIdentifierStartChar(this.getCurrentChar()) }?
    ;

GStringIdentifierChar
    :   ~'.' { isGStringIdentifierChar(this.getCurrentChar()) }?
    ;

// ----------------------------------------
mode IN_TRIPLE_J_STRING;

// TODO: check for unescaped SQ, I think groovy allows them
TripleJStringContent
    :   ( ~['] | BS SQ )+
    ;

TripleJStringEnd
    :   SQ SQ SQ -> popMode
    ;

// ----------------------------------------
mode IN_TRIPLE_G_STRING;

// TODO: check for unescaped DQ, I think groovy allows them
TripleGStringDollarValueStart
    :   DOLLAR { !this.isNext('{') }? -> pushMode(IN_G_STRING_PATH)
    ;

TripleGStringClosureStart
    :   DOLLAR LEFT_CURLY { this.onGStringClosure(); }
    ;

TripleGStringText
    :   ( ~["$] | BS DQ | BS DOLLAR )+
    ;

TripleGStringEnd
    :   DQ DQ DQ -> popMode
    ;

// ----------------------------------------
mode IN_PARENTHESES_SLASHY_STRING;

ParenthesesSlashyStringText
    :   ( ~'/' | BS FS )+
    ;

ParenthesesSlashyStringDollarValueStart
    :   DOLLAR { !this.isNext('{') }? -> pushMode(IN_G_STRING_PATH)
    ;
ParenthesesSlashyStringClosureStart
    :   DOLLAR LEFT_CURLY { this.onGStringClosure(); }
    ;

ParenthesesSlashyStringEnd
    :   FS RP -> popMode
    ;

// ----------------------------------------
mode IN_DOLLAR_SLASHY_STRING;

DollarSlashyStringText
    :   (   ~[$/]
        |   DOLLAR DOLLAR
        |   DOLLAR FS
        |   FS DOLLAR DOLLAR
        )+
    ;

DollarSlashyStringDollarValueStart
    :   DOLLAR { !isAnyOf(this.getNextChar(), '/', '$', '{') }? -> pushMode(IN_G_STRING_PATH)
    ;

DollarSlashyStringClosureStart
    :   DOLLAR LEFT_CURLY { this.onGStringClosure(); }
    ;

DollarSlashyStringEnd
    :   FS DOLLAR { !this.isNext('$') }? -> popMode
    ;
