package groowt.view.component.web.ast;

import groowt.view.component.web.ast.node.*;
import groowt.view.component.web.util.TokenRange;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public abstract class NodeFactoryTests {

    private final Function<NodeFactoryTests, NodeFactory> getNodeFactory;
    private NodeFactory nodeFactory;
    private Token startAndEndToken;

    public NodeFactoryTests(Function<NodeFactoryTests, NodeFactory> getNodeFactory) {
        this.getNodeFactory = getNodeFactory;
    }

    protected Token getStartAndEndToken() {
        if (this.startAndEndToken == null) {
            this.startAndEndToken = mock(Token.class);
        }
        return this.startAndEndToken;
    }

    protected TokenRange getTokenRange() {
        return TokenRange.of(this.getStartAndEndToken());
    }

    @BeforeEach
    public void beforeEach() {
        this.nodeFactory = this.getNodeFactory.apply(this);
    }

    @Test
    public void compilationUnitNode(@Mock PreambleNode preambleNode, @Mock BodyNode bodyNode) {
        assertNotNull(this.nodeFactory.compilationUnitNode(this.getTokenRange(), preambleNode, bodyNode));
    }

    @Test
    public void compilationUnitNodePreambleOnly(@Mock PreambleNode preambleNode) {
        assertNotNull(this.nodeFactory.compilationUnitNode(this.getTokenRange(), preambleNode, null));
    }

    @Test
    public void compilationUnitNodeBodyOnly(@Mock BodyNode bodyNode) {
        assertNotNull(this.nodeFactory.compilationUnitNode(this.getTokenRange(), null, bodyNode));
    }

    @Test
    public void compilationUnitNodeNullNull() {
        assertNotNull(this.nodeFactory.compilationUnitNode(this.getTokenRange(), null, null));
    }

    @Test
    public void preambleNode() {
        assertNotNull(this.nodeFactory.preambleNode(this.getTokenRange(), 0));
    }

    @Test
    public void bodyNode(@Mock BodyChildNode child, @Mock TreeNode childAsNode) {
        when(child.asNode()).thenReturn(childAsNode);
        assertNotNull(this.nodeFactory.bodyNode(this.getTokenRange(), List.of(child)));
    }

    @Test
    public void bodyTextNode(@Mock BodyTextChild child, @Mock TreeNode childAsNode) {
        when(child.asNode()).thenReturn(childAsNode);
        assertNotNull(this.nodeFactory.bodyTextNode(this.getTokenRange(), List.of(child)));
    }

    @Test
    public void questionTagNode(@Mock QuestionTagChild child, @Mock TreeNode childAsNode) {
        when(child.asNode()).thenReturn(childAsNode);
        assertNotNull(this.nodeFactory.questionTagNode(this.getTokenRange(), List.of(child)));
    }

    @Test
    public void htmlCommentNode(@Mock HtmlCommentChild child, @Mock TreeNode childAsNode) {
        when(child.asNode()).thenReturn(childAsNode);
        assertNotNull(this.nodeFactory.htmlCommentNode(this.getTokenRange(), List.of(child)));
    }

    @Test
    public void textNode() {
        assertNotNull(this.nodeFactory.textNode(this.getTokenRange(), "Hello, World!"));
    }

    @Test
    public void typedComponentNode(@Mock ComponentArgsNode componentArgsNode, @Mock BodyNode bodyNode) {
        assertNotNull(this.nodeFactory.typedComponentNode(this.getTokenRange(), componentArgsNode, bodyNode));
    }

    @Test
    public void typedComponentNodeBodyNull(@Mock ComponentArgsNode componentArgsNode) {
        assertNotNull(this.nodeFactory.typedComponentNode(this.getTokenRange(), componentArgsNode, null));
    }

    @Test
    public void fragmentComponentNode(@Mock BodyNode bodyNode) {
        assertNotNull(this.nodeFactory.fragmentComponentNode(this.getTokenRange(), bodyNode));
    }

    @Test
    public void componentArgsNodeWithClassComponentType(
            @Mock ClassComponentTypeNode componentTypeNode,
            @Mock ComponentConstructorNode componentConstructorNode
    ) {
        assertNotNull(this.nodeFactory.componentArgsNode(
                this.getTokenRange(),
                componentTypeNode,
                componentConstructorNode,
                List.of()
        ));
    }

    @Test
    public void componentArgsNodeWithStringComponentType(
            @Mock StringComponentTypeNode componentTypeNode,
            @Mock ComponentConstructorNode componentConstructorNode
    ) {
        assertNotNull(this.nodeFactory.componentArgsNode(
                this.getTokenRange(),
                componentTypeNode,
                componentConstructorNode,
                List.of()
        ));
    }

    @Test
    public void componentArgsNodeNullConstructorNodeWithClassComponentType(
            @Mock ClassComponentTypeNode componentTypeNode
    ) {
        assertNotNull(this.nodeFactory.componentArgsNode(
                this.getTokenRange(), componentTypeNode, null, List.of()
        ));
    }

    @Test
    public void componentArgsNodeNullConstructorNodeWithStringComponentType(
            @Mock StringComponentTypeNode componentTypeNode
    ) {
        assertNotNull(this.nodeFactory.componentArgsNode(
                this.getTokenRange(), componentTypeNode, null, List.of()
        ));
    }

    @Test
    public void classComponentTypeNode() {
        assertNotNull(this.nodeFactory.classComponentTypeNode(this.getTokenRange()));
    }

    @Test
    public void stringComponentTypeNode() {
        assertNotNull(this.nodeFactory.stringComponentTypeNode(this.getTokenRange(), 0));
    }

    @Test
    public void componentConstructorNode() {
        assertNotNull(this.nodeFactory.componentConstructorNode(this.getTokenRange(), 0));
    }

    @Test
    public void keyValueAttrNode(@Mock KeyNode keyNode, @Mock ValueNode valueNode, @Mock TreeNode valueNodeAsNode) {
        when(valueNode.asNode()).thenReturn(valueNodeAsNode);
        assertNotNull(this.nodeFactory.keyValueAttrNode(this.getTokenRange(), keyNode, valueNode));
    }

    @Test
    public void booleanValueAttrNode(@Mock KeyNode keyNode) {
        assertNotNull(this.nodeFactory.booleanValueAttrNode(this.getTokenRange(), keyNode));
    }

    @Test
    public void keyNode() {
        assertNotNull(this.nodeFactory.keyNode(this.getTokenRange(), 0));
    }

    @Test
    public void gStringValueNode() {
        assertNotNull(this.nodeFactory.gStringValueNode(this.getTokenRange(), 0));
    }

    @Test
    public void jStringValueNode() {
        assertNotNull(this.nodeFactory.jStringValueNode(this.getTokenRange(), ""));
    }

    @Test
    public void closureValueNode() {
        assertNotNull(this.nodeFactory.closureValueNode(this.getTokenRange(), 0));
    }

    @Test
    public void componentValueNodeWithTypedComponentNode(@Mock TypedComponentNode node) {
        assertNotNull(this.nodeFactory.componentValueNode(this.getTokenRange(), node));
    }

    @Test
    public void componentValueNodeWithFragmentComponentNode(@Mock FragmentComponentNode node) {
        assertNotNull(this.nodeFactory.componentValueNode(this.getTokenRange(), node));
    }

    @Test
    public void plainScriptletNode() {
        assertNotNull(this.nodeFactory.plainScriptletNode(this.getTokenRange(), 0));
    }

    @Test
    public void dollarScriptletNode() {
        assertNotNull(this.nodeFactory.dollarScriptletNode(this.getTokenRange(), 0));
    }

    @Test
    public void dollarReferenceNode() {
        assertNotNull(this.nodeFactory.dollarReferenceNode(this.getTokenRange(), 0));
    }

}
