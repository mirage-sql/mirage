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

/**
 * ANDやORのプレフィックスのための{@link Node}です。
 *
 * @author higa
 *
 */
public class PrefixSqlNode extends AbstractNode {

	private String prefix;

	private String sql;

	/**
	 * <code>PrefixSqlNode</code>を作成します。
	 *
	 * @param prefix
	 * @param sql
	 */
	public PrefixSqlNode(String prefix, String sql) {
		this.prefix = prefix;
		this.sql = sql;
	}

	/**
	 * プレフィックスを返します。
	 *
	 * @return
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * SQLを返します。
	 *
	 * @return
	 */
	public String getSql() {
		return sql;
	}

//	@Override
	public void accept(SqlContext ctx) {
		if (ctx.isEnabled()) {
			ctx.addSql(prefix);
		}
		ctx.addSql(sql);
	}

	@Override
	public String toString() {
		return "PrefixSqlNode [prefix=" + prefix + ", sql=" + sql + ", children=" + children + "]";
	}
}