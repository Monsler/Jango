package com.monsler.jango;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private String input;
    private int pos;
    private Character current;

    public List<String> tokenize(String input) {
        this.input = input;
        this.pos = 0;
        this.current = input.charAt(0);

        final List<String> tokens = new ArrayList<>();

        while (current != null) {
            if (Character.isSpaceChar(current)) {
                skipWitheSpaces();
            } else if (current == '+') {
                tokens.add(current.toString());
                advance();
            } else if (current == '-') {
                tokens.add(current.toString());
                advance();
            } else if (current == '*') {
                tokens.add(current.toString());
                advance();
            } else if (current == '/') {
                tokens.add(current.toString());
                advance();
            } else if (current == '=') {
                tokens.add(current.toString());
                advance();
            } else if (current == '(') {
                tokens.add(current.toString());
                advance();
            } else if (current == ')') {
                tokens.add(current.toString());
                advance();
            } else if (current == ';') {
                tokens.add(current.toString());
                advance();
            } else if (current == ':') {
                tokens.add(current.toString());
                advance();
            } else if (Character.isDigit(current)) {
                tokens.add(integer());
            } else if (current == '"' || current == '\'') {
                tokens.add(consumeString(current));
                advance();
            } else if (Character.isAlphabetic(current)) {
                tokens.add(identifierOrKeyword());
            } else if(current == '.'){
                tokens.add(current.toString());
                advance();
            } else if (current == '[') {
                tokens.add(current.toString());
                advance();
            } else if (current == ']') {
                tokens.add(current.toString());
                advance();
            } else if (current == '_') {
                tokens.add(current.toString());
                advance();
            } else if (current == '>') {
                tokens.add(current.toString());
                advance();
            } else if (current == '<') {
                tokens.add(current.toString());
                advance();
            } else if (current == ',') {
                tokens.add(current.toString());
                advance();
            } else if (current == '{') {
                tokens.add(current.toString());
                advance();
            } else if (current == '}') {
                tokens.add(current.toString());
                advance();
            } else if (current == '!') {
                tokens.add(current.toString());
                advance();
            } else {
                advance();
            }
        }

        return tokens;
    }

    private void advance() {

        pos++;

        if (pos > input.length() - 1) {
            current = null;
        } else {
            current = input.charAt(pos);
        }
    }

    private void skipWitheSpaces() {

        while (current != null && Character.isSpaceChar(current)) {
            advance();
        }
    }

    private String consumeString(char type) {

        final StringBuilder sb = new StringBuilder();

        advance();

        while (current != null && current != type) {
            sb.append(current);
            advance();
        }

        return type+sb.toString()+type;
    }

    private String identifierOrKeyword() {

        final StringBuilder sb = new StringBuilder();

        while (current != null && Character.isLetterOrDigit(current)) {
            sb.append(current);
            advance();
        }

        final String result = sb.toString();

        return result;
    }
    private String integer() {

        final StringBuilder result = new StringBuilder();

        while (current != null && Character.isDigit(current)) {
            result.append(current);
            advance();
        }

        return result.toString();
    }
}
