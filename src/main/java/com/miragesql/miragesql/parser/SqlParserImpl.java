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

import java.util.Stack;

import com.miragesql.miragesql.bean.BeanDescFactory;
import com.miragesql.miragesql.exception.TwoWaySQLException;
import com.miragesql.miragesql.parser.SqlTokenizer.TokenType;
import com.miragesql.miragesql.util.StringUtil;

/**
 * Default {@link SqlParser} implementation.
 *
 * @author higa
 */
public class SqlParserImpl implements SqlParser {

	private BeanDescFactory beanDescFactory;
	
    private SqlTokenizer tokenizer;

    private Stack<Node> nodeStack = new Stack<>();


    public SqlParserImpl(String sql, BeanDescFactory beanDescFactory) {
		sql = sql.trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        tokenizer = new SqlTokenizerImpl(sql);
        this.beanDescFactory = beanDescFactory;
    }

//	@Override
    public Node parse() {
        push(new ContainerNode());
        while (TokenType.EOF != tokenizer.next()) {
            parseToken();
        }
        return pop();
    }

    protected void parseToken() {
        switch (tokenizer.getTokenType()) {
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
            break;
        }
    }

    /**
     * Parse the SQL.
     */
    protected void parseSql() {
        String sql = tokenizer.getToken();
        if (isElseMode()) {
            sql = StringUtil.replace(sql, "--", "");
        }
        Node node = peek();
        if ((node instanceof IfNode || node instanceof ElseNode)
                && node.getChildSize() == 0) {

            SqlTokenizer st = new SqlTokenizerImpl(sql);
            st.skipWhitespace();
            String token = st.skipToken();
            st.skipWhitespace();
            if (sql.startsWith(",")) {
                if (sql.startsWith(", ")) {
                    node.addChild(new PrefixSqlNode(", ", sql.substring(2)));
                } else {
                    node.addChild(new PrefixSqlNode(",", sql.substring(1)));
                }
            } else if ("AND".equalsIgnoreCase(token)
                    || "OR".equalsIgnoreCase(token)) {
                node.addChild(new PrefixSqlNode(st.getBefore(), st.getAfter()));
            } else {
                node.addChild(new SqlNode(sql));
            }
        } else {
            node.addChild(new SqlNode(sql));
        }
    }

    /**
     * Parse an SQL comment.
     */
    protected void parseComment() {
        String comment = tokenizer.getToken();
        if (isTargetComment(comment)) {
            if (isIfComment(comment)) {
                parseIf();
            } else if (isBeginComment(comment)) {
                parseBegin();
            } else if (isEndComment(comment)) {
                return;
            } else {
                parseCommentBindVariable();
            }
        } else if(isHintComment(comment)){
        	peek().addChild(new SqlNode("/*" + comment + "*/"));
        }
    }

    /**
     * Parse an IF node.
     */
    protected void parseIf() {
        String condition = tokenizer.getToken().substring(2).trim();
        if (StringUtil.isEmpty(condition)) {
        	throw new TwoWaySQLException("If condition not found.");
        }
        IfNode ifNode = new IfNode(condition);
        peek().addChild(ifNode);
        push(ifNode);
        parseEnd();
    }

    /**
     * Parse a BEGIN node.
     */
    protected void parseBegin() {
        BeginNode beginNode = new BeginNode();
        peek().addChild(beginNode);
        push(beginNode);
        parseEnd();
    }

    /**
     * Parse an END node.
     */
    protected void parseEnd() {
        while (TokenType.EOF != tokenizer.next()) {
            if (tokenizer.getTokenType() == TokenType.COMMENT
                    && isEndComment(tokenizer.getToken())) {

                pop();
                return;
            }
            parseToken();
        }
        throw new TwoWaySQLException(String.format(
                "END comment of %s not found.", tokenizer.getSql()));
    }

    /**
     * Parse an ELSE node.
     */
    protected void parseElse() {
        Node parent = peek();
        if (!(parent instanceof IfNode)) {
            return;
        }
        IfNode ifNode = (IfNode) pop();
        ElseNode elseNode = new ElseNode();
        ifNode.setElseNode(elseNode);
        push(elseNode);
        tokenizer.skipWhitespace();
    }

    /**
     * Parse bind variables comment.
     */
    protected void parseCommentBindVariable() {
        String expr = tokenizer.getToken();
        String s = tokenizer.skipToken();
        if (s.startsWith("(") && s.endsWith(")")) {
            peek().addChild(new ParenBindVariableNode(expr));
        } else if (expr.startsWith("$")) {
            peek().addChild(new EmbeddedValueNode(expr.substring(1), beanDescFactory));
        } else if (expr.equals("orderBy")) {
            peek().addChild(new EmbeddedValueNode(expr, beanDescFactory));
        } else {
            peek().addChild(new BindVariableNode(expr, beanDescFactory));
        }
    }

    /**
     * Parse the bind variable.
     */
    protected void parseBindVariable() {
        String expr = tokenizer.getToken();
        peek().addChild(new BindVariableNode(expr, beanDescFactory));
    }

    /**
     * Pop (remove from the stack) the top node.
     *
     * @return the top node.
     */
    protected Node pop() {
        return nodeStack.pop();
    }

    /**
     * Peek the top node.
     *
     * @return the top node.
     */
    protected Node peek() {
        return nodeStack.peek();
    }

    /**
     * Push a node
     *
     * @param node the node to push.
     */
    protected void push(Node node) {
        nodeStack.push(node);
    }

    /**
     * @return <code>true</code> if in the ELSE branch, <code>false</code> otherwise.
     */
    protected boolean isElseMode() {
        for (int i = 0; i < nodeStack.size(); ++i) {
            if (nodeStack.get(i) instanceof ElseNode) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this comment is a "Mirage-SQL" comment, i.e. can be a keyword or a bind variable.
     *
     * @param comment the comment to check
     * @return <code>true</code> if it's a Mirage-SQL comment.
     */
    protected static boolean isTargetComment(String comment) {
        return comment != null && comment.length() > 0
                && Character.isJavaIdentifierStart(comment.charAt(0));
    }

    /**
     * Checks if this comment is a "Mirage-SQL" <code>IF</code> keyword.
     *
     * @param comment the comment to check
     * @return <code>true</code> if this comment is an <code>IF</code> keyword.
     */
    protected static boolean isIfComment(String comment) {
        return comment.startsWith("IF");
    }

    /**
     * Checks if this comment is a "Mirage-SQL" <code>BEGIN</code> keyword.
     *
     * @param content the comment to check
     * @return <code>true</code> if this comment is an <code>BEGIN</code> keyword.
    */
    protected static boolean isBeginComment(String content) {
        return content != null && "BEGIN".equals(content);
    }

    /**
     * Checks if this comment is a "Mirage-SQL" <code>END</code> keyword.
     *
     * @param content the comment to check
     * @return <code>true</code> if this comment is an <code>END</code> keyword.
     */
    protected static boolean isEndComment(String content) {
        return content != null && "END".equals(content);
    }

    /**
     * Checks if the comment is an Oracle optimizer hint.
     *
     * @param content the comment to check
     * @return <code>true</code> if this comment is an Oracle optimizer hint.
     */
    protected static boolean isHintComment(String content) {
        return content.startsWith("+");
    }
}