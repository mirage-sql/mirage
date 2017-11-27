package com.miragesql.miragesql.bean;

import java.util.Map;

import junit.framework.TestCase;


public class FieldPropertyExtractorTest extends TestCase {

	FieldPropertyExtractor extractor = new FieldPropertyExtractor();

	public void testDefaultPropertyExtractor_book() {
		Map<String, PropertyWrapper> properties = extractor.extractProperties(Book.class);
		assertTrue(properties.containsKey("bookId"));
		assertTrue(properties.containsKey("bookName"));
		assertTrue(properties.containsKey("bookType"));
		assertEquals(3, properties.size());
	}

	public void testDefaultPropertyExtractor_magazine() {
		Map<String, PropertyWrapper> properties = extractor.extractProperties(Magazine.class);

		// The property name is extracted in preference to the name of the field rather than the name of the accessor
		assertTrue(properties.containsKey("magazineId"));

		// Even when there is no accessor, the field is extracted as a property
		assertTrue(properties.containsKey("magazineCode"));

		// Extract invisible fields as well
		assertTrue(properties.containsKey("price"));

		// static final constants are not extracted
		assertFalse(properties.containsKey("CONSTANT"));
		assertEquals(3, properties.size());
	}
}
