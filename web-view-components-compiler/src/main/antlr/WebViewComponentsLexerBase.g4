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
    DollarScriptletClose,
    Nlws,
    ErrorChar
}

channels {
    ERROR
}

@header {
    import java.util.Set;
    import groowt.view.component.web.WebViewComponentBugError;
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

    public WebViewComponentsLexerBase() {}

    private void onPreambleClose() {
        this.setType(PreambleBreak);
        this.exitPreamble();
        this.mode(MAIN);
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

    private boolean inAttrComponent() {
        return this.peekMode(1) == COMPONENT_ATTR_VALUE;
    }

}

// ----------------------------------------
// DEFAULT_MODE

PreambleOpen
    :   THREE_DASH ( NL | WS+ )? { this.enterPreamble(); }
        -> type(PreambleBreak), pushMode(GROOVY_CODE)
    ;

NotPreambleOpen
    :   .
        {
            this.rollbackOne();
            this.setCanPreamble(false);
        } -> skip, mode(MAIN)
    ;

// ----------------------------------------
mode MAIN;

ComponentOpen
    :   LT { !isAnyOf(this.getNextChar(), '/', '>') }? -> pushMode(TAG_START)
    ;

ClosingComponentOpen
    :   LT FS { !this.isNext('>') }? -> pushMode(TAG_START)
    ;

FragmentOpen
    :   LT GT
    ;

FragmentClose
    :   LT FS GT
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
    :   DOLLAR { isGStringIdentifierStartChar(this.getNextChar()) }? -> pushMode(IN_G_STRING_PATH)
    ;

QuestionTagOpen
    :   LT QUESTION
    ;

QuestionTagClose
    :   QUESTION GT
    ;

HtmlCommentOpen
    :   LT BANG TWO_DASH
    ;

HtmlCommentClose
    :   TWO_DASH GT
    ;

RawText
    :   (   ~[-<$?]
        |   MINUS { !this.isNext("->") }?
        |   LT { canFollowLessThan(this.getNextCharAsString()) }?
        |   LT BANG { !this.isNext("--") }?
        |   DOLLAR { !(this.isNext('{') || isIdentifierStartChar(this.getNextChar())) }?
        |   QUESTION { !this.isNext('>') }?
        )+
    ;

MainError
    :   .   -> type(ErrorChar), channel(ERROR)
    ;

// ----------------------------------------
mode TAG_START;

TypedIdentifier
    :   ( PackageIdentifier DOT )* ClassIdentifier ( DOT ClassIdentifier )* -> mode(IN_TAG)
    ;

fragment
PackageIdentifier
    :   PackageIdentifierStartChar PackageIdentifierChar*
    ;

fragment
PackageIdentifierStartChar
    :   [\p{Ll}]
    ;

fragment
PackageIdentifierChar
    :   [\p{L}_0-9]
    ;

fragment
ClassIdentifier
    :   ClassIdentifierStartChar ClassIdentifierChar*
    ;

fragment
ClassIdentifierStartChar
    :   [\p{Lu}]
    ;

fragment
ClassIdentifierChar
    :   [\p{L}_0-9]
    ;

StringIdentifier
    :   StringIdentifierStartChar StringIdentifierChar* -> mode(IN_TAG)
    ;

fragment
StringIdentifierStartChar
    :   [\p{Ll}]
    ;

fragment
StringIdentifierChar
    :   [-_0-9\p{L}]
    ;

TagStartNlws
    :   NLWS+ -> type(Nlws), channel(HIDDEN)
    ;

TagStartError
    :   . -> type(ErrorChar), channel(ERROR)
    ;

// ----------------------------------------
mode IN_TAG;

ComponentClose
    :   GT
        {
            if (this.inAttrComponent() && this.attrComponentFinished()) {
                this.popMode();
                this.popMode();
            } else {
                this.popMode();
            }
        }
    ;

ComponentSelfClose
    :   FS GT
        {
            if (this.inAttrComponent()) {
                this.exitAttrComponent();
                if (this.attrComponentFinished()) {
                    this.popMode();
                    this.popMode();
                }
            } else {
                this.popMode();
            }
        }
    ;

ConstructorOpen
    :   LP { this.enterConstructor(); }
    ;

AttributeIdentifier
    :   AttributeIdentifierStartChar AttributeIdentifierChar*
    ;

fragment
AttributeIdentifierStartChar
    :   [\p{L}_$]
    ;

fragment
AttributeIdentifierChar
    :   [\p{L}_$0-9]
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
    :   LEFT_CURLY { !this.isNextIgnoreNlws('<') }?
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
    :   LEFT_CURLY InTagNlws? { this.isNext('<') }?
        {
            this.enterAttrComponent();
            this.pushMode(COMPONENT_ATTR_VALUE);
        }
    ;

ComponentAttrValueEnd
    :   InTagNlws? RIGHT_CURLY { this.popAttrComponent(); }
    ;

InTagNlws
    :   NLWS+ -> type(Nlws), channel(HIDDEN)
    ;

TagError
    :   . -> channel(ERROR)
    ;

// ----------------------------------------
mode COMPONENT_ATTR_VALUE;

AttrComponentOpen
    :   LT { !isAnyOf(this.getNextChar(), '/', '>') }? -> type(ComponentOpen), pushMode(TAG_START)
    ;

AttrClosingComponentOpen
    :   LT FS { !this.isNext('>') }?
        {
            this.exitAttrComponent();
        } -> type(ClosingComponentOpen), pushMode(TAG_START)
    ;

AttrFragmentOpen
    :   LT GT -> type(FragmentOpen)
    ;

AttrFragmentClose
    :   LT FS GT {
            this.exitAttrComponent();
        } -> type(FragmentClose), popMode
    ;

AttrEqualsScriptletOpen
    :   LT PERCENT EQ -> type(EqualsScriptletOpen), pushMode(GROOVY_CODE)
    ;

AttrPlainScriptletOpen
    :   LT PERCENT -> type(PlainScriptletOpen), pushMode(GROOVY_CODE)
    ;

AttrDollarScriptletOpen
    :   DOLLAR LEFT_CURLY {
            this.curlies.push(() -> {
                this.setType(DollarScriptletClose);
                this.popMode();
            });
            this.curlies.increment();
            this.setType(DollarScriptletOpen);
            this.pushMode(GROOVY_CODE);
        }
    ;

AttrDollarReferenceStart
    :   DOLLAR { isGStringIdentifierStartChar(this.getNextChar()) }?
        -> type(DollarReferenceStart), pushMode(IN_G_STRING_PATH)
    ;

AttrQuestionTagOpen
    :   LT QUESTION -> type(QuestionTagOpen)
    ;

AttrQuestionTagClose
    :   QUESTION GT -> type(QuestionTagClose)
    ;

AttrHtmlCommentOpen
    :   LT BANG TWO_DASH -> type(HtmlCommentOpen)
    ;

AttrHtmlCommentClose
    :   TWO_DASH GT -> type(HtmlCommentClose)
    ;

AttrRawText
    :   (   ~[-<$?]
        |   MINUS { !this.isNext("->") }?
        |   LT { canFollowLessThan(this.getNextCharAsString()) }?
        |   LT BANG { !this.isNext("--") }?
        |   DOLLAR { !(this.isNext('{') || isIdentifierStartChar(this.getNextChar())) }?
        |   QUESTION { !this.isNext('>') }?
        )+ -> type(RawText)
    ;

AttrError
    :   .   -> type(ErrorChar), channel(ERROR)
    ;

// ----------------------------------------
mode GROOVY_CODE;

PreambleClose
    :   THREE_DASH { this.inPreamble() && this.getCharPositionInLine() == 3 }? WS* NL? { this.onPreambleClose(); }
    ;

ScriptletClose
    :   PERCENT GT -> popMode
    ;

GroovyCodeChars
    :   (   ~[-/$%(){}'"]
        |   MINUS { !(this.getCharPositionInLine() == 1 && this.isNext("--")) }?
        |   FS { !isAnyOf(this.getNextChar(), '/', '*') }?
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
    :   ~'*' | ( '*' { !this.isNext('/') }? )
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
        |   DOLLAR { !isGStringIdentifierStartChar(this.getNextChar()) }?
        )+
    ;

GStringDollarValueStart
    :   DOLLAR { !this.isNext('{') && isGStringIdentifierStartChar(this.getNextChar()) }? -> pushMode(IN_G_STRING_PATH)
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
                case COMPONENT_ATTR_VALUE -> new StringContinueEndSpec();
                case MAIN -> new StringContinueEndSpec();
                default -> throw new IllegalStateException(
                    "not a valid gStringPath context: " + this.getModeName(this.peekMode(1))
                );
            };
            switch (endSpec) {
                case StringContinueEndSpec ignored -> {
                    this.popMode();
                    this.rollbackOne(true);
                    this.skip();
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

TripleJStringContent
    :   (   ~[']
        |   SQ { !(this.isNext("''") && this._input.LA(3) != '\'') }?
        )+
    ;

TripleJStringEnd
    :   SQ SQ SQ { !this.isNext('\'') }? -> popMode
    ;

// ----------------------------------------
mode IN_TRIPLE_G_STRING;

TripleGStringDollarValueStart
    :   DOLLAR { isGStringIdentifierStartChar(this.getNextChar()) }? -> pushMode(IN_G_STRING_PATH)
    ;

TripleGStringClosureStart
    :   DOLLAR LEFT_CURLY { this.onGStringClosure(); }
    ;

TripleGStringText
    :   (   ~["$]
        |   DQ { !(this.isNext("\"\"") && this._input.LA(3) != '"') }?
        |   BS DOLLAR
        |   DOLLAR { !isGStringIdentifierStartChar(this.getNextChar()) }?
        )+
    ;

TripleGStringEnd
    :   DQ DQ DQ { !this.isNext('"') }? -> popMode
    ;

// ----------------------------------------
mode IN_PARENTHESES_SLASHY_STRING;

ParenthesesSlashyStringText
    :   ( ~'/' | BS FS )+
    ;

ParenthesesSlashyStringDollarValueStart
    :   DOLLAR { isGStringIdentifierStartChar(this.getNextChar()) }? -> pushMode(IN_G_STRING_PATH)
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
        |   DOLLAR { !isGStringIdentifierStartChar(this.getNextChar()) }?
        |   FS { !this.isNext('$') }?
        )+
    ;

DollarSlashyStringDollarValueStart
    :   DOLLAR { isGStringIdentifierStartChar(this.getNextChar()) }?
            -> pushMode(IN_G_STRING_PATH)
    ;

DollarSlashyStringClosureStart
    :   DOLLAR LEFT_CURLY { this.onGStringClosure(); }
    ;

DollarSlashyStringEnd
    :   FS DOLLAR { !this.isNext('$') }? -> popMode
    ;
