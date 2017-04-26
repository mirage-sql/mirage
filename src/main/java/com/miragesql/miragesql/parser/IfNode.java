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
import com.miragesql.miragesql.util.OgnlUtil;


/**
 * {@link Node} representing an <code>IF</code> comment.
 *
 * @author higa
 */
public class IfNode extends ContainerNode {

    private String expression;

    private Object parsedExpression;

    private ElseNode elseNode;

    /**
     * Creates n <code>IfNode</code> from a string expression.
     *
     * @param expression the string expression
     */
    public IfNode(String expression) {
        this.expression = expression;
        this.parsedExpression = OgnlUtil.parseExpression(expression);
    }

    /**
     * @return the IF Expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Returns the {@link ElseNode}.
     *
     * @return the ElseNode
     */
    public ElseNode getElseNode() {
        return elseNode;
    }

    /**
     * Sets the {@link ElseNode}
     *
     * @param elseNode the ElseNode
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