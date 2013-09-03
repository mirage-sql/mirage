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

import jp.sf.amateras.mirage.exception.TwoWaySQLException;
import jp.sf.amateras.mirage.util.OgnlUtil;


/**
 * If用の{@link Node}です。
 *
 * @author higa
 *
 */
public class IfNode extends ContainerNode {

    private String expression;

    private Object parsedExpression;

    private ElseNode elseNode;

    /**
     * <code>IfNode</code>を作成します。
     *
     * @param expression
     */
    public IfNode(String expression) {
        this.expression = expression;
        this.parsedExpression = OgnlUtil.parseExpression(expression);
    }

    /**
     * 式を返します。
     *
     * @return
     */
    public String getExpression() {
        return expression;
    }

    /**
     * {@link ElseNode}を返します。
     *
     * @return
     */
    public ElseNode getElseNode() {
        return elseNode;
    }

    /**
     * {@link ElseNode}を設定します。
     *
     * @param elseNode
     */
    public void setElseNode(ElseNode elseNode) {
        this.elseNode = elseNode;
    }

	@Override
    public void accept(SqlContext ctx) {
        Object result = OgnlUtil.getValue(parsedExpression, ctx);
        if (result instanceof Boolean) {
            if (((Boolean) result).booleanValue()) {
                super.accept(ctx);
                ctx.setEnabled(true);
            } else if (elseNode != null) {
                elseNode.accept(ctx);
                ctx.setEnabled(true);
            }
        } else {
            throw new TwoWaySQLException(String.format(
                "%s is not bool expression.", expression));
        }
    }

	@Override
	public String toString() {
		return "IfNode [expression=" + expression + ", parsedExpression=" + parsedExpression + ", elseNode=" + elseNode
				+", children=" + children + "]";
	}

	
}