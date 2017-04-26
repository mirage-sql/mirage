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

import java.util.Arrays;

import com.miragesql.miragesql.bean.BeanDesc;
import com.miragesql.miragesql.bean.BeanDescFactory;
import com.miragesql.miragesql.bean.PropertyDesc;
import com.miragesql.miragesql.util.StringUtil;

/**
 * {@link Node} corresponding to bind variables (as comments).
 *
 * @author higa
 */
public class BindVariableNode extends AbstractNode {

	private String expression;

	private String[] names;

	private BeanDescFactory beanDescFactory;

	/**
	 * Creates a <code>BindVariableNode</code> from a string expression.
	 *
	 * @param expression string expression
	 */
	public BindVariableNode(String expression, BeanDescFactory beanDescFactory) {
		this.expression = expression;
		names = StringUtil.split(expression, ".");
		this.beanDescFactory = beanDescFactory;
	}

	/**
	 * @return the bind expression
	 */
	public String getExpression() {
		return expression;
	}

//	@Override
	public void accept(SqlContext ctx) {
		Object value = ctx.getArg(names[0]);
		Class<?> clazz = ctx.getArgType(names[0]);
		for (int pos = 1; pos < names.length; pos++) {
			BeanDesc beanDesc = beanDescFactory.getBeanDesc(clazz);
			PropertyDesc pd = beanDesc.getPropertyDesc(names[pos]);
			if (value == null) {
				break;
			}
			value = pd.getValue(value);
			clazz = pd.getPropertyType();
		}
		ctx.addSql("?", value, clazz);
	}

	@Override
	public String toString() {
		return "BindVariableNode [expression=" + expression + ", names=" + Arrays.toString(names)
				+ ", children=" + children + "]";
	}
}