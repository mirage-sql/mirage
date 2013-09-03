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

import java.util.Arrays;

import jp.sf.amateras.mirage.bean.BeanDesc;
import jp.sf.amateras.mirage.bean.BeanDescFactory;
import jp.sf.amateras.mirage.bean.PropertyDesc;
import jp.sf.amateras.mirage.util.StringUtil;

/**
 * バインド変数のための{@link Node}です。
 *
 * @author higa
 *
 */
public class BindVariableNode extends AbstractNode {

	private String expression;

	private String[] names;

	/**
	 * <code>BindVariableNode</code>を作成します。
	 *
	 * @param expression
	 */
	public BindVariableNode(String expression) {
		this.expression = expression;
		names = StringUtil.split(expression, ".");
	}

	/**
	 * 式を返します。
	 *
	 * @return
	 */
	public String getExpression() {
		return expression;
	}

//	@Override
	public void accept(SqlContext ctx) {
		Object value = ctx.getArg(names[0]);
		Class<?> clazz = ctx.getArgType(names[0]);
		for (int pos = 1; pos < names.length; pos++) {
			BeanDesc beanDesc = BeanDescFactory.getBeanDesc(clazz);
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