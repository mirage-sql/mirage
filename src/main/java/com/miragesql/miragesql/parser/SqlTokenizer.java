/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.miragesql.miragesql.parser;

/**
 * The SQL Tokenizer Interface to decompose a SQL into tokens.
 * 
 * @author higa
 */
public interface SqlTokenizer {

    /**
     * @return a token
     */
    String getToken();

    /**
     * @return the SQL String
     */
    String getSql();

    /**
     * @return the SQL String until the actual position of the Tokenizer.
     */
    String getBefore();

    /**
     * @return the SQL String after the current position of the Tokenizer.
     */
    String getAfter();

    /**
     * @return the position that is currently being analyzed ty the Tokenizer.
     */
    int getPosition();

    /**
     * @return the current token type.
     */
    TokenType getTokenType();

    /**
     * @return the next token type.
     */
    TokenType getNextTokenType();

    /**
     * @return advanced the Tokenizer to the next token.
     */
    TokenType next();

    /**
     * Skips a token.
     * 
     * @return the token being skipped
     */
    String skipToken();

    /**
     * Skips the whitespace.
     * 
     * @return the whitespace being skipped
     */
    String skipWhitespace();
    
    enum TokenType {
        SQL,
        COMMENT,
        ELSE,
        BIND_VARIABLE,
        EOF
    }
}