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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedList;

import jp.sf.amateras.mirage.util.OgnlUtil;

/**
 * INのバインド変数用の{@link Node}です。
 *
 * @author higa
 * @author shuji.w6e
 */
public class ParenBindVariableNode extends AbstractNode {

    private String expression;

    private Object parsedExpression;

    /**
     * <code>ParenBindVariableNode</code>を作成します。
     *
     * @param expression
     */
    public ParenBindVariableNode(String expression) {
        this.expression = expression;
        this.parsedExpression = OgnlUtil.parseExpression(expression);
    }

    /**
     * 式を返します。
     *
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

//	@Override
    public void accept(SqlContext ctx) {
        Object var = OgnlUtil.getValue(parsedExpression, ctx);
		if (var instanceof Collection) {
			bindArray(ctx, Collection.class.cast(var).toArray());
		} else if (var instanceof Iterable) {
			bindArray(ctx, toArray(Iterable.class.cast(var)));
        } else if (var == null) {
            return;
        } else if (var.getClass().isArray()) {
            bindArray(ctx, var);
        } else {
            ctx.addSql("?", var, var.getClass());
        }

    }

	private Object[] toArray(Iterable<?> iterable) {
		LinkedList<Object> list = new LinkedList<Object>();
		for (Object o : iterable) {
			list.add(o);
		}
		return list.toArray();
	}

    /**
     * @param ctx
     * @param array
     */
    protected void bindArray(SqlContext ctx, Object array) {
        int length = Array.getLength(array);
        if (length == 0) {
            return;
        }
        Class<?> clazz = null;
        for (int i = 0; i < length; ++i) {
            Object o = Array.get(array, i);
            if (o != null) {
                clazz = o.getClass();
            }
        }
        ctx.addSql("(");
        Object value = Array.get(array, 0);
        ctx.addSql("?", value, clazz);
        for (int i = 1; i < length; ++i) {
            ctx.addSql(", ");
            value = Array.get(array, i);
            ctx.addSql("?", value, clazz);
        }
        ctx.addSql(")");
    }

	@Override
	public String toString() {
		return "ParenBindVariableNode [expression=" + expression + ", parsedExpression=" + parsedExpression
				+ ", children=" + children + "]";
	}
}