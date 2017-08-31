/*
 * Copyright 2011 Daisuke Miyamoto.
 * Created on 2011/10/21
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
package com.miragesql.miragesql.naming;

/**
 * An implementation of {@link NameConverter} which provides naming convention like Ruby on Rails, i.e.
 * it also pluralizes table names using the {@link Inflection} utility for English names only.
 *
 * @since 1.1.4
 * @author daisuke
 */
public class RailsLikeNameConverter extends DefaultNameConverter {

	@Override
	public String entityToTable(String entityName) {
		String pluralized = Inflection.pluralize(entityName);
		return super.entityToTable(pluralized);
	}

}
