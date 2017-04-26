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

import com.miragesql.miragesql.exception.TwoWaySQLException;


/**
 * {@link SqlTokenizer} Implementation.
 *
 * @author higa
 */
public class SqlTokenizerImpl implements SqlTokenizer {

    private String sql;

    private int position = 0;

    private String token;

    private TokenType tokenType = TokenType.SQL;

    private TokenType nextTokenType = TokenType.SQL;

    private int bindVariableNum = 0;

    public SqlTokenizerImpl(String sql) {
        this.sql = sql;
    }

//	@Override
    public String getSql() {
        return sql;
    }

//	@Override
    public int getPosition() {
        return position;
    }

//	@Override
    public String getToken() {
        return token;
    }

//	@Override
    public String getBefore() {
        return sql.substring(0, position);
    }

//	@Override
    public String getAfter() {
        return sql.substring(position);
    }

//	@Override
    public TokenType getTokenType() {
        return tokenType;
    }

//	@Override
    public TokenType getNextTokenType() {
        return nextTokenType;
    }

//	@Override
    public TokenType next() {
        if (position >= sql.length()) {
            token = null;
            tokenType = TokenType.EOF;
            nextTokenType = TokenType.EOF;
            return tokenType;
        }
        switch (nextTokenType) {
        case SQL:
            parseSql();
            break;
        case COMMENT:
            parseComment();
            break;
        case ELSE:
            parseElse();
            break;
        case BIND_VARIABLE:
            parseBindVariable();
            break;
        default:
            parseEof();
            break;
        }
        return tokenType;
    }

    /**
     * Parse the SQL.
     */
    protected void parseSql() {
        int commentStartPos = sql.indexOf("/*", position);
        int commentStartPos2 = sql.indexOf("#*", position);
        if (0 < commentStartPos2 && commentStartPos2 < commentStartPos) {
            commentStartPos = commentStartPos2;
        }
        int lineCommentStartPos = sql.indexOf("--", position);
        int bindVariableStartPos = sql.indexOf("?", position);
        int elseCommentStartPos = -1;
        int elseCommentLength = -1;
        if (lineCommentStartPos >= 0) {
            int skipPos = skipWhitespace(lineCommentStartPos + 2);
            if (skipPos + 4 < sql.length()
                    && "ELSE".equals(sql.substring(skipPos, skipPos + 4))) {
                elseCommentStartPos = lineCommentStartPos;
                elseCommentLength = skipPos + 4 - lineCommentStartPos;
            }
        }
        int nextStartPos = getNextStartPos(commentStartPos,
                elseCommentStartPos, bindVariableStartPos);
        if (nextStartPos < 0) {
            token = sql.substring(position);
            nextTokenType = TokenType.EOF;
            position = sql.length();
            tokenType = TokenType.SQL;
        } else {
            token = sql.substring(position, nextStartPos);
            tokenType = TokenType.SQL;
            boolean needNext = nextStartPos == position;
            if (nextStartPos == commentStartPos) {
                nextTokenType = TokenType.COMMENT;
                position = commentStartPos + 2;
            } else if (nextStartPos == elseCommentStartPos) {
                nextTokenType = TokenType.ELSE;
                position = elseCommentStartPos + elseCommentLength;
            } else if (nextStartPos == bindVariableStartPos) {
                nextTokenType = TokenType.BIND_VARIABLE;
                position = bindVariableStartPos;
            }
            if (needNext) {
                next();
            }
        }
    }

    /**
     * Returns the next starting position.
     *
     * @param commentStartPos starting position of the comment
     * @param elseCommentStartPos starting position of the ELSE comment
     * @param bindVariableStartPos starting position of the bind variable
     * @return the next starting position.
     */
    protected int getNextStartPos(int commentStartPos, int elseCommentStartPos,
            int bindVariableStartPos) {

        int nextStartPos = -1;
        if (commentStartPos >= 0) {
            nextStartPos = commentStartPos;
        }
        if (elseCommentStartPos >= 0
                && (nextStartPos < 0 || elseCommentStartPos < nextStartPos)) {
            nextStartPos = elseCommentStartPos;
        }
        if (bindVariableStartPos >= 0
                && (nextStartPos < 0 || bindVariableStartPos < nextStartPos)) {
            nextStartPos = bindVariableStartPos;
        }
        return nextStartPos;
    }

    /**
     * Parse the comment.
     */
    protected void parseComment() {
        int commentEndPos = sql.indexOf("*/", position);
        int commentEndPos2 = sql.indexOf("*#", position);
        if (0 < commentEndPos2 && commentEndPos2 < commentEndPos) {
            commentEndPos = commentEndPos2;
        }
        if (commentEndPos < 0) {
            throw new TwoWaySQLException(String.format(
                    "%s is not closed with %s.", sql.substring(position), "*/"));
        }
        token = sql.substring(position, commentEndPos);
        nextTokenType = TokenType.SQL;
        position = commentEndPos + 2;
        tokenType = TokenType.COMMENT;
    }

    /**
     * Parse the bind variable.
     */
    protected void parseBindVariable() {
        token = nextBindVariableName();
        nextTokenType = TokenType.SQL;
        position += 1;
        tokenType = TokenType.BIND_VARIABLE;
    }

    /**
     * Parse the ELSE comment.
     */
    protected void parseElse() {
        token = null;
        nextTokenType = TokenType.SQL;
        tokenType = TokenType.ELSE;
    }

    /**
     * Parse the end of the SQL.
     */
    protected void parseEof() {
        token = null;
        tokenType = TokenType.EOF;
        nextTokenType = TokenType.EOF;
    }

    /**
     * @return the bind variable name for the position parameters.
     */
    protected String nextBindVariableName() {
        return "$" + ++bindVariableNum;
    }

//	@Override
    public String skipToken() {
        int index = sql.length();
        char quote = position < sql.length() ? sql.charAt(position) : '\0';
        boolean quoting = quote == '\'' || quote == '(';
        if (quote == '(') {
            quote = ')';
        }
        for (int i = quoting ? position + 1 : position; i < sql.length(); ++i) {
            char c = sql.charAt(i);
            if ((Character.isWhitespace(c) || c == ',' || c == ')' || c == '(')
                    && !quoting) {
                index = i;
                break;
            } else if (c == '/' && i + 1 < sql.length()
                    && sql.charAt(i + 1) == '*') {
                index = i;
                break;
            } else if (c == '-' && i + 1 < sql.length()
                    && sql.charAt(i + 1) == '-') {
                index = i;
                break;
            } else if (quoting && quote == '\'' && c == '\''
                    && (i + 1 >= sql.length() || sql.charAt(i + 1) != '\'')) {
                index = i + 1;
                break;
            } else if (quoting && c == quote) {
                index = i + 1;
                break;
            }
        }
        token = sql.substring(position, index);
        tokenType = TokenType.SQL;
        nextTokenType = TokenType.SQL;
        position = index;
        return token;
    }

//	@Override
    public String skipWhitespace() {
        int index = skipWhitespace(position);
        token = sql.substring(position, index);
        position = index;
        return token;
    }

    private int skipWhitespace(int position) {
        int index = sql.length();
        for (int i = position; i < sql.length(); ++i) {
            char c = sql.charAt(i);
            if (!Character.isWhitespace(c)) {
                index = i;
                break;
            }
        }
        return index;
    }
}