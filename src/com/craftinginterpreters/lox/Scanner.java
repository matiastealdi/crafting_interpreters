package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {
    private static HashMap<String, TokenType> keywords;
    static {
        //AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NILL, OR, PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,
        keywords = new HashMap<>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("fun", FUN);
        keywords.put("for", FOR);
        keywords.put("if", IF);
        keywords.put("nill", NILL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    private String source;
    private List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while(!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '/':
                if (match('/')) {
                    while(peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case '*': addToken(STAR); break;

            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case ' ':
            case '\t':
            case '\r':
                break;
            case '\n':
                line ++;
                break;

            case '"': string(); break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    literal();
                } else {
                    Lox.error(line, "Unexpected Character.");
                }
                break;
        }

    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }
    private static boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void literal() {
        while(isAlphaNumeric(peek())) advance();

        String value = source.substring(start, current);
        TokenType tokenType = keywords.getOrDefault(value, IDENTIFIER);
        addToken(tokenType);
    }

    private void number() {
        while (isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
        }

        Double value = Double.parseDouble(source.substring(start, current));
        addToken(NUMBER, value);
    }

    private void string() {
      while(peek() != '"' && !isAtEnd()) {
          if (peek() == '\n') line ++;
          advance();
      }
      if (isAtEnd()) {
          Lox.error(line, "Unterminated string.");
          return;
      }
      advance();

      String value = source.substring(start + 1, current - 1);
      addToken(STRING, value);
    }

    private boolean match(char toMatch) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != toMatch) return false;

        current ++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType tt) {
        addToken(tt, null);
    }

    private void addToken(TokenType tt, Object literal) {
        tokens.add(new Token(tt, null, literal, line));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
