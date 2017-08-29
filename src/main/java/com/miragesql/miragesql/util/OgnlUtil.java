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
package com.miragesql.miragesql.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;

import com.miragesql.miragesql.exception.OgnlRuntimeException;
import ognl.*;

/**
 * Ognl用のユーティリティクラスです。
 *
 * @author higa
 *
 */
@SuppressWarnings("rawtypes")
public class OgnlUtil {

    /**
     * インスタンスを構築します。
     */
    protected OgnlUtil() {
    }

    /**
     * 値を返します。
     *
     * @param exp
     * @param root
     * @return 値
     * @see #getValue(Object, Map, Object, String, int)
     */
    public static Object getValue(Object exp, Object root) {
        return getValue(exp, root, null, 0);
    }

    /**
     * 値を返します。
     *
     * @param exp
     * @param root
     * @param path
     * @param lineNumber
     * @return 値
     * @see #getValue(Object, Map, Object, String, int)
     */
    public static Object getValue(Object exp, Object root, String path,
            int lineNumber) {
        return getValue(exp, null, root, path, lineNumber);
    }

    /**
     * 値を返します。
     *
     * @param exp
     * @param ctx
     * @param root
     * @return 値
     * @see #getValue(Object, Map, Object, String, int)
     */
    public static Object getValue(Object exp, Map ctx, Object root) {
        return getValue(exp, ctx, root, null, 0);
    }

    /**
     * 値を返します。
     *
     * @param exp
     * @param ctx
     * @param root
     * @param path
     * @param lineNumber
     * @return 値
     * @throws OgnlRuntimeException
     *             OgnlExceptionが発生した場合
     */
    public static Object getValue(Object exp, Map ctx, Object root,
            String path, int lineNumber) {
        try {
            OgnlContext context = new OgnlContext(null, null, new DefaultMemberAccess(true));
            return Ognl.getValue(exp, context, root);
        } catch (OgnlException ex) {
            throw new OgnlRuntimeException(ex.getReason() == null ? ex : ex
                    .getReason(), path, lineNumber);
        } catch (Exception ex) {
            throw new OgnlRuntimeException(ex, path, lineNumber);
        }
    }

    /**
     * 式を解析します。
     *
     * @param expression
     * @return 解析した結果
     * @see #parseExpression(String, String, int)
     */
    public static Object parseExpression(String expression) {
        return parseExpression(expression, null, 0);
    }

    /**
     * 式を解析します。
     *
     * @param expression
     * @param path
     * @param lineNumber
     * @return 解析した結果
     * @throws OgnlRuntimeException
     *             OgnlExceptionが発生した場合
     */
    public static Object parseExpression(String expression, String path,
            int lineNumber) {
        try {
            return Ognl.parseExpression(expression);
        } catch (Exception ex) {
            throw new OgnlRuntimeException(ex, path, lineNumber);
        }
    }

    public static class DefaultMemberAccess implements MemberAccess {

        private boolean allowPrivateAccess = false;
        private boolean allowProtectedAccess = false;
        private boolean allowPackageProtectedAccess = false;

        /*
         * =================================================================== Constructors
         * ===================================================================
         */
        public DefaultMemberAccess( boolean allowAllAccess ){
            this( allowAllAccess, allowAllAccess, allowAllAccess );
        }

        public DefaultMemberAccess( boolean allowPrivateAccess, boolean allowProtectedAccess,
                                    boolean allowPackageProtectedAccess ){
            super();
            this.allowPrivateAccess = allowPrivateAccess;
            this.allowProtectedAccess = allowProtectedAccess;
            this.allowPackageProtectedAccess = allowPackageProtectedAccess;
        }

        /*
         * =================================================================== Public methods
         * ===================================================================
         */
        public boolean getAllowPrivateAccess(){
            return allowPrivateAccess;
        }

        public void setAllowPrivateAccess( boolean value ){
            allowPrivateAccess = value;
        }

        public boolean getAllowProtectedAccess() {
            return allowProtectedAccess;
        }

        public void setAllowProtectedAccess( boolean value ) {
            allowProtectedAccess = value;
        }

        public boolean getAllowPackageProtectedAccess() {
            return allowPackageProtectedAccess;
        }

        public void setAllowPackageProtectedAccess( boolean value ) {
            allowPackageProtectedAccess = value;
        }

        @Override
        public Object setup(Map context, Object target, Member member, String propertyName) {
            Object result = null;

            if ( isAccessible( context, target, member, propertyName ) ) {
                AccessibleObject accessible = (AccessibleObject) member;

                if ( !accessible.isAccessible() ) {
                    result = Boolean.TRUE;
                    accessible.setAccessible( true );
                }
            }
            return result;
        }

        @Override
        public void restore(Map context, Object target, Member member, String propertyName, Object state) {
            if ( state != null ) {
                ( (AccessibleObject) member ).setAccessible( (Boolean) state );
            }
        }

        @Override
        public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
            int modifiers = member.getModifiers();
            boolean result = Modifier.isPublic( modifiers );

            if ( !result ) {
                if ( Modifier.isPrivate( modifiers ) ) {
                    result = getAllowPrivateAccess();
                } else {
                    if ( Modifier.isProtected( modifiers ) ) {
                        result = getAllowProtectedAccess();
                    } else {
                        result = getAllowPackageProtectedAccess();
                    }
                }
            }
            return result;
        }
    }
}
