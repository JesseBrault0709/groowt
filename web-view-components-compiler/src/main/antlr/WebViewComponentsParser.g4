parser grammar WebViewComponentsParser;

options {
    tokenVocab = WebViewComponentsLexerBase;
}

compilationUnit
    :   preamble? body? EOF
    ;

preamble
    :   PreambleBreak ( GroovyCode? PreambleBreak )?
    ;

body
    :   ( component | bodyText )+
    ;

bodyText
    :   ( questionTag | htmlComment | text | bodyTextGroovyElement )+
    ;

questionTag
    :   QuestionTagOpen ( text | bodyTextGroovyElement )* QuestionTagClose
    ;

htmlComment
    :   HtmlCommentOpen ( text | bodyTextGroovyElement )* HtmlCommentClose
    ;

text
    :   RawText
    ;

bodyTextGroovyElement
    :   plainScriptlet | equalsScriptlet | dollarScriptlet | dollarReference
    ;

component
    :   selfClosingComponent | componentWithChildren | fragmentComponent
    ;

selfClosingComponent
    :   ComponentOpen componentArgs ComponentSelfClose
    ;

componentWithChildren
    :   openComponent children=body? closingComponent
    ;

openComponent
    :   ComponentOpen componentArgs ComponentClose
    ;

closingComponent
    :   ClosingComponentOpen
        componentType
        ComponentClose
    ;

fragmentComponent
    :   FragmentOpen body FragmentClose
    ;

componentArgs
    :   componentType
        ( attr )*
        componentConstructor?
        ( attr )*
    ;

componentType
    :   TypedIdentifier | StringIdentifier
    ;

componentConstructor
    :   ConstructorOpen GroovyCode? ConstructorClose
    ;

attr
    :   keyValueAttr | booleanAttr
    ;

keyValueAttr
    :   AttributeIdentifier Equals value
    ;

booleanAttr
    :   AttributeIdentifier
    ;

value
    :   gStringAttrValue | jStringAttrValue | closureAttrValue | componentAttrValue
    ;

gStringAttrValue
    :   GStringAttrValueStart GroovyCode? GStringAttrValueEnd
    ;

jStringAttrValue
    :   JStringAttrValueStart GroovyCode? JStringAttrValueEnd
    ;

closureAttrValue
    :   ClosureAttrValueStart GroovyCode? ClosureAttrValueEnd
    ;

componentAttrValue
    :   ComponentAttrValueStart component ComponentAttrValueEnd
    ;

equalsScriptlet
    :   EqualsScriptletOpen GroovyCode? ScriptletClose
    ;

plainScriptlet
    :   PlainScriptletOpen GroovyCode? ScriptletClose
    ;

dollarScriptlet
    :   DollarScriptletOpen GroovyCode? DollarScriptletClose
    ;

dollarReference
    :   DollarReferenceStart GroovyCode
    ;
