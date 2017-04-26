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

		// accessorの名前よりも、フィールド名を優先してプロパティ名を抽出する
		assertTrue(properties.containsKey("magazineId"));

		// アクセサが無い場合も、フィールドをプロパティとして抽出する
		assertTrue(properties.containsKey("magazineCode"));

		// 見えないフィールドも抽出する
		assertTrue(properties.containsKey("price"));

		// staticだったりfinalだったりする、定数フィールドはプロパティとして抽出しない
		assertFalse(properties.containsKey("CONSTANT"));
		assertEquals(3, properties.size());
	}
}
