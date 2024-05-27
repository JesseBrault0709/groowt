package groowt.view.component.web.ast;

import groowt.view.component.web.ast.node.*;
import groowt.view.component.web.util.TokenRange;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface NodeFactory {

    CompilationUnitNode compilationUnitNode(
            TokenRange tokenRange,
            @Nullable PreambleNode preambleNode,
            @Nullable BodyNode bodyNode
    );

    PreambleNode preambleNode(TokenRange tokenRange, int groovyIndex);

    BodyNode bodyNode(TokenRange tokenRange, List<? extends BodyChildNode> children);

    BodyTextNode bodyTextNode(TokenRange tokenRange, List<? extends BodyTextChild> children);

    QuestionNode questionTagNode(
            TokenRange tokenRange,
            Token openToken,
            Token closeToken,
            List<? extends QuestionTagChild> children
    );

    HtmlCommentNode htmlCommentNode(
            TokenRange tokenRange,
            Token openToken,
            Token closeToken,
            List<? extends HtmlCommentChild> children
    );

    TextNode textNode(TokenRange tokenRange, String content);

    TypedComponentNode typedComponentNode(
            TokenRange tokenRange,
            ComponentArgsNode componentArgsNode,
            @Nullable BodyNode body
    );

    FragmentComponentNode fragmentComponentNode(TokenRange tokenRange, BodyNode bodyNode);

    ComponentArgsNode componentArgsNode(
            TokenRange tokenRange,
            ComponentTypeNode componentTypeNode,
            @Nullable ComponentConstructorNode componentConstructorNode,
            List<AttrNode> attributeNodes
    );

    ClassComponentTypeNode classComponentTypeNode(TokenRange tokenRange);

    StringComponentTypeNode stringComponentTypeNode(TokenRange tokenRange, int typeTokenIndex);

    ComponentConstructorNode componentConstructorNode(TokenRange tokenRange, int groovyIndex);

    KeyValueAttrNode keyValueAttrNode(TokenRange tokenRange, KeyNode keyNode, ValueNode valueNode);

    BooleanValueAttrNode booleanValueAttrNode(TokenRange tokenRange, KeyNode keyNode);

    KeyNode keyNode(TokenRange tokenRange, int keyTokenIndex);

    GStringValueNode gStringValueNode(TokenRange tokenRange, int contentTokenIndex);

    JStringValueNode jStringValueNode(TokenRange tokenRange, String content);

    ClosureValueNode closureValueNode(TokenRange tokenRange, int groovyIndex);

    EmptyClosureValueNode emptyClosureValueNode(TokenRange tokenRange);

    ComponentValueNode componentValueNode(TokenRange tokenRange, ComponentNode componentNode);

    EqualsScriptletNode equalsScriptletNode(TokenRange tokenRange, String groovyCode);

    PlainScriptletNode plainScriptletNode(TokenRange tokenRange, String groovyCode);

    DollarScriptletNode dollarScriptletNode(TokenRange tokenRange, String groovyCode);

    DollarReferenceNode dollarReferenceNode(TokenRange tokenRange, List<String> parts);

}
