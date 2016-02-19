/*
 * Copyright 2011 Daisuke Miyamoto.
 * Created on 2013/08/07
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
package jp.sf.amateras.mirage.util;

import java.lang.annotation.Annotation;

/**
 * TODO for daisuke
 * 
 * @author daisuke
 */
public class AnnotationUtils {
	
	/**
	 * Find a single {@link Annotation} of {@code annotationType} from the supplied {@link Class},
	 * traversing its interfaces and superclasses if no annotation can be found on the given class itself.
	 * <p>This method explicitly handles class-level annotations which are not declared as
	 * {@link java.lang.annotation.Inherited inherited} <i>as well as annotations on interfaces</i>.
	 * <p>The algorithm operates as follows: Searches for an annotation on the given class and returns
	 * it if found. Else searches all interfaces that the given class declares, returning the annotation
	 * from the first matching candidate, if any. Else proceeds with introspection of the superclass
	 * of the given class, checking the superclass itself; if no annotation found there, proceeds
	 * with the interfaces that the superclass declares. Recurring up through the entire superclass
	 * hierarchy if no match is found.
	 * @param clazz the class to look for annotations on
	 * @param annotationType the annotation class to look for
	 * @return the annotation found, or {@code null} if none found
	 */
	public static <A extends Annotation>A findAnnotation(Class<?> clazz, Class<A> annotationType) {
		if (clazz == null) {
			throw new NullPointerException("Class must not be null"); //$NON-NLS-1$
		}
		A annotation = clazz.getAnnotation(annotationType);
		if (annotation != null) {
			return annotation;
		}
		for (Class<?> ifc : clazz.getInterfaces()) {
			annotation = findAnnotation(ifc, annotationType);
			if (annotation != null) {
				return annotation;
			}
		}
		if (!Annotation.class.isAssignableFrom(clazz)) {
			for (Annotation ann : clazz.getAnnotations()) {
				annotation = findAnnotation(ann.annotationType(), annotationType);
				if (annotation != null) {
					return annotation;
				}
			}
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass == null || superClass.equals(Object.class)) {
			return null;
		}
		return findAnnotation(superClass, annotationType);
	}
}
