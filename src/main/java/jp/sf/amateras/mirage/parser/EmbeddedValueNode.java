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

import jp.sf.amateras.mirage.bean.BeanDesc;
import jp.sf.amateras.mirage.bean.BeanDescFactory;
import jp.sf.amateras.mirage.bean.PropertyDesc;
import jp.sf.amateras.mirage.exception.TwoWaySQLException;
import jp.sf.amateras.mirage.util.StringUtil;


/**
 * {@link Node} for embedding values.
 *
 * @author higa
 */
public class EmbeddedValueNode extends AbstractNode {

    private String expression;

    private String baseName;

    private String propertyName;

	private BeanDescFactory beanDescFactory;

    /**
     * Creates a <code>EmbeddedValueNode</code> from a string expression.
     *
     * @param expression the string expression to create the node from.
     * @param beanDescFactory beanDescFactory
     */
    public EmbeddedValueNode(String expression, BeanDescFactory beanDescFactory) {
        this.expression = expression;
		this.beanDescFactory = beanDescFactory;
        String[] array = StringUtil.split(expression, ".");
        this.baseName = array[0];
        if (array.length > 1) {
            this.propertyName = array[1];
        }
    }

    /**
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

//	@Override
    public void accept(SqlContext ctx) {
        Object value = ctx.getArg(baseName);
        Class<?> clazz = ctx.getArgType(baseName);
        if (propertyName != null) {
            BeanDesc beanDesc = beanDescFactory.getBeanDesc(clazz);
            PropertyDesc pd = beanDesc.getPropertyDesc(propertyName);
            value = pd.getValue(value);
            clazz = pd.getPropertyType();
        }
        if (value != null) {
            String sql = value.toString();
            if (sql.indexOf(';') >= 0) {
                throw new TwoWaySQLException("semicolon is not allowed.");
            }
            ctx.addSql(sql);
        }
    }

	@Override
	public String toString() {
		return "EmbeddedValueNode [expression=" + expression + ", baseName=" + baseName + ", propertyName="
				+ propertyName + ", children=" + children + "]";
	}
}