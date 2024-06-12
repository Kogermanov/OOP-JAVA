package Lexeme;

public class LexemeNode {
    private Lexeme lexeme;
    private LexemeNode leftChild;
    private LexemeNode rightChild;

    public LexemeNode(Lexeme lexeme) {
        this.lexeme = lexeme;
        this.leftChild = null;
        this.rightChild = null;
    }

    public Lexeme getLexeme() {
        return this.lexeme;
    }

    public void setLexeme(final Lexeme lexeme) {
        this.lexeme = lexeme;
    }

    public LexemeNode getLeftChild() {
        return this.leftChild;
    }

    public LexemeNode getRightChild() {
        return this.rightChild;
    }

    public void setLeftChild(final LexemeNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(final LexemeNode rightChild) {
        this.rightChild = rightChild;
    }

    public void printPrefix(LexemeNode node) {
        if (node == null) {
            return;
        }
        System.out.print(node.lexeme.value + " ");
        printPrefix(node.getLeftChild());
        printPrefix(node.getRightChild());
    }

    public void printPostfix(LexemeNode node) {
        if (node == null) {
            return;
        }
        printPostfix(node.getLeftChild());
        printPostfix(node.getRightChild());
        System.out.print(node.lexeme.value + " ");
    }
}
