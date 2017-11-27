package com.miragesql.miragesql.bean;

import java.util.Map;

import junit.framework.TestCase;


public class DefaultPropertyExtractorTest extends TestCase {

	DefaultPropertyExtractor extractor = new DefaultPropertyExtractor();

	public void testDefaultPropertyExtractor_book() {
		Map<String, PropertyWrapper> properties = extractor.extractProperties(Book.class);
		assertTrue(properties.containsKey("bookId"));
		assertTrue(properties.containsKey("bookName"));
		assertTrue(properties.containsKey("bookType"));
		assertEquals(3, properties.size());
	}

	public void testDefaultPropertyExtractor_magazine() {
		Map<String, PropertyWrapper> properties = extractor.extractProperties(Magazine.class);

		// The property name is extracted by giving preference to the name of the accessor rather than the field name
		assertTrue(properties.containsKey("id"));

		// Even when there is no accessor, the field is extracted as a property
		assertTrue(properties.containsKey("magazineCode"));

		// Do not extract invisible fields
		assertFalse(properties.containsKey("price"));

		// static final constants are not extracted
		assertFalse(properties.containsKey("CONSTANT"));
		assertEquals(2, properties.size());
	}
}
