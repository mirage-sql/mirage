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
package jp.sf.amateras.mirage.parser;

import java.util.Stack;

import jp.sf.amateras.mirage.exception.TwoWaySQLException;
import jp.sf.amateras.mirage.parser.SqlTokenizer.TokenType;
import jp.sf.amateras.mirage.util.StringUtil;

/**
 * Default {@link SqlParser} implementation.
 *
 * @author higa
 */
public class SqlParserImpl implements SqlParser {

    private SqlTokenizer tokenizer;

    private Stack<Node> nodeStack = new Stack<Node>();

    /**
     * Constructs.
     *
     * @param sql
     */
    public SqlParserImpl(String sql) {
        sql = sql.trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        tokenizer = new SqlTokenizerImpl(sql);
    }

//	@Override
    public Node parse() {
        push(new ContainerNode());
        while (TokenType.EOF != tokenizer.next()) {
            parseToken();
        }
        return pop();
    }

    /**
     * トークンを解析します。
     */
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
     * SQLを解析します。
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
     * コメントを解析します。
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
     * IFを解析します。
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
     * BEGINを解析します。
     */
    protected void parseBegin() {
        BeginNode beginNode = new BeginNode();
        peek().addChild(beginNode);
        push(beginNode);
        parseEnd();
    }

    /**
     * ENDを解析します。
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
     * ELSEを解析します。
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
     * バインド変数コメントを解析します。
     */
    protected void parseCommentBindVariable() {
        String expr = tokenizer.getToken();
        String s = tokenizer.skipToken();
        if (s.startsWith("(") && s.endsWith(")")) {
            peek().addChild(new ParenBindVariableNode(expr));
        } else if (expr.startsWith("$")) {
            peek().addChild(new EmbeddedValueNode(expr.substring(1)));
        } else if (expr.equals("orderBy")) {
            peek().addChild(new EmbeddedValueNode(expr));
        } else {
            peek().addChild(new BindVariableNode(expr));
        }
    }

    /**
     * バインド変数を解析します。
     */
    protected void parseBindVariable() {
        String expr = tokenizer.getToken();
        peek().addChild(new BindVariableNode(expr));
    }

    /**
     * 一番上のノードを取り出します。
     *
     * @return 一番上のノード
     */
    protected Node pop() {
        return nodeStack.pop();
    }

    /**
     * 一番上のノードを返します。
     *
     * @return 一番上のノード
     */
    protected Node peek() {
        return nodeStack.peek();
    }

    /**
     * ノードを一番上に追加します。
     *
     * @param node
     *            ノード
     */
    protected void push(Node node) {
        nodeStack.push(node);
    }

    /**
     * ELSEモードかどうかを返します。
     *
     * @return ELSEモードかどうか
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
     * 対象とするコメントかどうかを返します。
     *
     * @param comment
     *            コメント
     * @return 対象とするコメントかどうか
     */
    protected static boolean isTargetComment(String comment) {
        return comment != null && comment.length() > 0
                && Character.isJavaIdentifierStart(comment.charAt(0));
    }

    /**
     * IFコメントかどうかを返します。
     *
     * @param comment
     *            コメント
     * @return IFコメントかどうか
     */
    protected static boolean isIfComment(String comment) {
        return comment.startsWith("IF");
    }

    /**
     * BEGINコメントかどうかを返します。
     *
     * @param content
     *            コメント
     * @return BEGINコメントかどうか
     */
    protected static boolean isBeginComment(String content) {
        return content != null && "BEGIN".equals(content);
    }

    /**
     * ENDコメントかどうかを返します。
     *
     * @param content
     *            コメント
     * @return ENDコメントかどうか
     */
    protected static boolean isEndComment(String content) {
        return content != null && "END".equals(content);
    }

    /**
     * Oracleのヒントコメントかどうかを返します。
     * <p>
     * TODO Oracle専用の処理がここにあるのもちょっと微妙かも…。
     *
     * @param content コメント
     * @return Oracleのヒントコメントかどうか
     */
    protected static boolean isHintComment(String content) {
        return content.startsWith("+");
    }

}