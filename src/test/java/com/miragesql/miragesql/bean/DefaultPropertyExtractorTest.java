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

		// フィールド名よりも、accessorの名前を優先してプロパティ名を抽出する
		assertTrue(properties.containsKey("id"));

		// アクセサが無い場合も、フィールドをプロパティとして抽出する
		assertTrue(properties.containsKey("magazineCode"));

		// 見えないフィールドは抽出しない
		assertFalse(properties.containsKey("price"));

		// staticだったりfinalだったりする、定数フィールドはプロパティとして抽出しない
		assertFalse(properties.containsKey("CONSTANT"));
		assertEquals(2, properties.size());
	}
}
