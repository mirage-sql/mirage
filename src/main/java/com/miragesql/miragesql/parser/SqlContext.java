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

/**
 * The <code>SqlContext</code> is a hierarchical data structure that holds the required information to execute
 * an SQL as supported by Mirage SQL. It contains the SQL itself, and the required bind variables.
 *
 * @author higa
 */
public interface SqlContext {

    /**
     * Returns an Argument from the context corresponding to this <code>name</code>.
     *
     * @param name the name
     * @return the Argument object corresponding to <code>name</code>
     */
    Object getArg(String name);

    /**
     * Returns <code>true</code> if for this <code>name</code> there's an Argument object in the context.
     *
     * @param name the name
     * @return true if there's an Argument for this name, <code>false</code> otherwise.
     */
    boolean hasArg(String name);

    /**
     * Returns the type (class) of the argument for the given <code>name</code>, or <code>null</code>
     * if the <code>name</code> was not found in the context.
     *
     * @param name the name
     * @return the type of the argument.
     */
    Class<?> getArgType(String name);

    /**
     * Adds to the context a argument object, with type and name.
     *
     * @param name the name of the argument
     * @param arg the argument object
     * @param argType the type (class) of the argument object.
     */
    void addArg(String name, Object arg, Class<?> argType);

    /**
     * @return the <code>SQL</code> from the context.
     */
    String getSql();

    /**
     * @return an array of all added bind variables (arguments) in the context.
     */
    Object[] getBindVariables();

    /**
     * @return an array of all added bind variable types (arguments) in the context.
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