package Lexeme;

import java.util.List;

public class LexemeBuffer {
    private int pos;
    public List<Lexeme> lexemes;

    public LexemeBuffer(List<Lexeme> lexemes){
        this.lexemes = lexemes;
    }

    public void nextPos() {
        pos++;
    }

    public Lexeme getLexeme() {
        return lexemes.get(pos);
    }

    public int getPos() {
        return pos;
    }
}