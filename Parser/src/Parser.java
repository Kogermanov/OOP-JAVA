import java.util.ArrayList;
import java.util.List;
import Lexeme.*;

/*
==========================================================================================
PARSER RULES

expr : plusminus* EOF;

plusminus : multdiv (('+' | '-')multdiv)*;

multdiv : factor (('*' | '/')factor)*;

factor : NUMBER | '(' expr ')';
==========================================================================================
*/

public class Parser {

    public static void main(String[] args) {
        String expressionText = "22 + 3 - 2 * (2 * 5 + 2) * 4";
//        String expressionText = "2 + 2 * 2";
//        String expressionText = "a * (d - e) + (f + g) / c";

        List<Lexeme> lexemes = lexAnalise(expressionText);
        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
        LexemeNode root = expr(lexemeBuffer);

        if (root != null) {
            root.printPrefix(root);
            System.out.print("\n");
            root.printPostfix(root);
            System.out.print("\n");
        }
    }

    public static List<Lexeme> lexAnalise(String expText) {
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos < expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                    pos++;
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                    pos++;
                    continue;
                default:
                    if (c >= '0' && c <= '9') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (c >= '0' && c <= '9');
                        lexemes.add(new Lexeme(LexemeType.NUMBER, sb.toString()));
                    } else if (c >= 'a' && c <= 'z') {
                        lexemes.add(new Lexeme(LexemeType.LETTER, c));
                        pos++;
                    } else if (c == ' ') {
                        pos++;
                    } else {
                        throw new RuntimeException("Unexpected character: " + c);
                    }
            }
        }
        lexemes.add(new Lexeme(LexemeType.EOF, ""));
        return lexemes;
    }

    public static LexemeNode expr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.getLexeme();
        if (lexeme.type == LexemeType.EOF) {
            return null;
        }
        return plusminus(lexemes);
    }

    public static LexemeNode plusminus(LexemeBuffer lexemes) {
        LexemeNode leftTerm = multdiv(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.getLexeme();
            LexemeNode rightTerm;
            switch (lexeme.type) {
                case OP_PLUS:
                    LexemeNode plus = new LexemeNode(lexeme);
                    lexemes.nextPos();
                    rightTerm = multdiv(lexemes);

                    plus.setLeftChild(leftTerm);
                    plus.setRightChild(rightTerm);
                    leftTerm = plus;
                    break;
                case OP_MINUS:
                    LexemeNode minus = new LexemeNode(lexeme);
                    lexemes.nextPos();
                    rightTerm = multdiv(lexemes);

                    minus.setLeftChild(leftTerm);
                    minus.setRightChild(rightTerm);
                    leftTerm = minus;
                    break;
                default:
                    return leftTerm;
            }
        }
    }

    public static LexemeNode multdiv(LexemeBuffer lexemes) {
        LexemeNode left_factor = factor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.getLexeme();
            switch (lexeme.type) {
                case OP_MUL:
                    LexemeNode mul = new LexemeNode(lexeme);
                    lexemes.nextPos();
                    LexemeNode right_mul_factor = factor(lexemes);

                    mul.setLeftChild(left_factor);
                    mul.setRightChild(right_mul_factor);
                    left_factor = mul;
                    break;
                case OP_DIV:
                    LexemeNode div = new LexemeNode(lexeme);
                    lexemes.nextPos();
                    LexemeNode right_div_factor = factor(lexemes);

                    div.setLeftChild(left_factor);
                    div.setRightChild(right_div_factor);
                    left_factor = div;
                    break;
                default:
                    return left_factor;
            }
        }
    }

    public static LexemeNode factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.getLexeme();
        switch (lexeme.type) {
            case NUMBER:
                lexemes.nextPos();
                return new LexemeNode(lexeme);
            case LETTER:
                lexemes.nextPos();
                return new LexemeNode(lexeme);
            case LEFT_BRACKET:
                lexemes.nextPos();
                LexemeNode expr = expr(lexemes);
                lexeme = lexemes.getLexeme();
                if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw new RuntimeException("Unexpected token " + lexeme.value + " at position: " + lexemes.getPos());
                }
                lexemes.nextPos();
                return expr;
            default:
                throw new RuntimeException("Unexpected token " + lexeme.value + " at position: " + lexemes.getPos());
        }
    }
}

/*
NEW INFO:

1) enum -

2) Character - объект с полем char c, и удобными методами

3) toString() - метод класса Character переводящий переменную типа char в String

4) List, ArrayList, .add - коллекции и метод добавления элемента в список

5) switch

StringBuilder
 */