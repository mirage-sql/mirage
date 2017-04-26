/*
 * Copyright 2011 Daisuke Miyamoto.
 * Created on 2011/10/24
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
package com.miragesql.miragesql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that specifies that a persistent property or field should be persisted as an enumerated type.
 * 
 * <p>It may be used in conjunction with the Basic annotation.</p> 
 * 
 * @since 1.0
 * @author daisuke
 */
@Target({
	ElementType.METHOD,
	ElementType.FIELD,
	ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Enumerated {
	
	/** (Optional) The type used in mapping an enum type. */
	EnumType value() default EnumType.ORDINAL;
	
	
	/**
	 * Defines a mapping for the enumerated types. The constants of this enumerated type specify how persistent the property
	 * or field should be persisted as a enumerated type. 
	 * 
	 * @since 1.0
	 * @version $Id$
	 * @author daisuke
	 */
	enum EnumType {
		/** Persist enumerated type property or field as an integer */
		ORDINAL,
		
		/** Persist enumerated type property or field as an integer */
		ONE_BASED_ORDINAL,
		
		/** Persist enumerated type property or field as a string */
		STRING,
	}
}
