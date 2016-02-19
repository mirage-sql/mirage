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
 * ｊ<code>SQL</code>を実行するときのコンテキストです。 コンテキストで<code>SQL</code>を実行するのに必要な情報を組み立てた後、
 * <code>getSql()</code>, <code>getBindVariables()</code>,
 * <code>getBindVariableTypes()</code>で、 情報を取り出して<code>SQL</code>を実行します。
 * <code>SQL</code>で<code>BEGIN</code>コメントと<code>END</code>コメントで囲まれている部分が、
 * 子供のコンテキストになります。 通常は、 <code>WHERE</code>句を<code>BEGIN</code>コメントと<code>END</code>コメントで囲み、
 * <code>WHERE</code>句の中の<code>IF</code>コメントが1つでも成立した場合、<code>enabled</code>になります。
 *
 * @author higa
 *
 */
public interface SqlContext {

    /**
     * 名前に応じた引数を返します。
     *
     * @param name
     *            引数名
     * @return 名前に応じた引数
     */
    Object getArg(String name);

    /**
     * 引数が存在するかどうかを返します。
     *
     * @param name
     *            引数名
     * @return 引数が存在するか
     */

    boolean hasArg(String name);

    /**
     * 名前に応じた引数のクラスを返します。 <code>getArg()</code>が<code>null</code>を返す場合があるので、
     * このメソッドが用意されています。
     *
     * @param name
     *            引数名
     * @return 名前に応じた引数のクラスを返します。
     */
    Class<?> getArgType(String name);

    /**
     * 引数を追加します。
     *
     * @param name
     *            引数名
     * @param arg
     *            引数
     * @param argType
     *            引数の型
     */
    void addArg(String name, Object arg, Class<?> argType);

    /**
     * 追加されたすべての<code>SQL</code>を返します。
     *
     * @return <code>SQL</code>
     */
    String getSql();

    /**
     * 追加されたすべてのバインド変数の配列を返します。
     *
     * @return バインド変数の配列
     */
    Object[] getBindVariables();

    /**
     * 追加されたすべてのバインド変数の型の配列を返します。
     *
     * @return バインド変数の型の配列
     */
    Class<?>[] getBindVariableTypes();

    /**
     * Adds the <code>SQL</code> the the SqlContext.
     *
     * @param sql the SQL to add to the context
     * @return the SqlContext itself
     */
    SqlContext addSql(String sql);

    /**
     * Adds the <code>SQL</code> with a bind variable and a type the the SqlContext.
     *
     * @param sql the SQL to add to the context
     * @param bindVariable the bind variable
     * @param bindVariableType the bind variable type
     * @return the SqlContext itself
     */
    SqlContext addSql(String sql, Object bindVariable, Class<?> bindVariableType);

    /**
     * Adds the <code>SQL</code> with an array of bind variables and their type the the SqlContext.
     *
     * @param sql the SQL to add to the context
     * @param bindVariables array of bind variables
     * @param bindVariableTypes array with types for the bind variables from <code>bindVariables</code>
     * @return the SqlContext itself
     */
    SqlContext addSql(String sql, Object[] bindVariables,
            Class<?>[] bindVariableTypes);

    /**
     * <code>BEGIN</code>コメントと<code>END</code>コメントで、
     * 囲まれた子供のコンテキストが有効かどうかを返します。
     *
     * @return 有効かどうか
     */
    boolean isEnabled();

    /**
     * <code>BEGIN</code>コメントと<code>END</code>コメントで、
     * 囲まれた子供のコンテキストが有効かどうかを設定します。
     *
     * @param enabled
     *            有効かどうか
     */
    void setEnabled(boolean enabled);
}