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
    :   bodyText? ( component bodyText? )+ | bodyText
    ;

bodyText
    :   gStringBodyText | jStringBodyText
    ;

gStringBodyText
    :   jStringBodyText? ( gStringBodyTextGroovyElement jStringBodyText? )+
    ;

jStringBodyText
    :   ( QuestionTag | HtmlComment | RawText )+
    ;

gStringBodyTextGroovyElement
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
    :   ClosingComponentOpen ComponentNlws?
        componentType ComponentNlws?
        ComponentClose
    ;

fragmentComponent
    :   ComponentOpen ComponentClose
        body
        ClosingComponentOpen ComponentClose
    ;

componentArgs
    :   ComponentNlws? componentType
        ComponentNlws? componentConstructor?
        ComponentNlws? ( attr ComponentNlws? )*
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
