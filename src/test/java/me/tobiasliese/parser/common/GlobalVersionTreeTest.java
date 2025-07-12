package me.tobiasliese.parser.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalVersionTreeTest {

    @Test
    void testSingleLineInsert() {
        var tree = new GlobalVersionTree();
        var begin = new Position(0, 0);
        var start = new Range(begin, begin);
        var create = new Edit(start,"Hello Test");
        tree.applyEdit(create);

        begin = new Position(6, 0);
        start = new Range(begin, begin);
        create = new Edit(start, "World what an amazing ");
        tree.applyEdit(create);

        assertEquals("Hello World what an amazing Test", tree.getContent());
    }

    @Test
    void testBlockInsert() {
        var tree = new GlobalVersionTree();
        var begin = new Position(0, 0);
        var start = new Range(begin, begin);
        var create = new Edit(start,"Hello Test");
        tree.applyEdit(create);

        begin = new Position(6, 0);
        start = new Range(begin, begin);
        create = new Edit(start, "World\nwhat an\namazing ");
        tree.applyEdit(create);

        assertEquals("""
Hello World
what an
amazing Test""", tree.getContent());
    }

    @Test
    void simpleDeleteTest() {
        var tree = new GlobalVersionTree();
        var begin = new Position(0, 0);
        var range = new Range(begin, begin);
        var edit = new Edit(range,"Hello Test");
        tree.applyEdit(edit);

        begin = new Position(5, 0);
        var end = new Position(10, 0);
        range = new Range(begin, end);
        edit = new Edit(range, "");
        tree.applyEdit(edit);

        assertEquals("Hello", tree.getContent());
    }

    @Test
    void simpleReplaceTest() {
        var tree = new GlobalVersionTree();
        var begin = new Position(0, 0);
        var range = new Range(begin, begin);
        var edit = new Edit(range,"Hello Test");
        tree.applyEdit(edit);

        begin = new Position(6, 0);
        var end = new Position(10, 0);
        range = new Range(begin, end);
        edit = new Edit(range, "World");
        tree.applyEdit(edit);

        assertEquals("Hello World", tree.getContent());
    }
}
