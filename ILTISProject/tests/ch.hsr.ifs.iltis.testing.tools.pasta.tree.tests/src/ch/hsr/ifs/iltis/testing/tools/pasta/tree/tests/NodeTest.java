package ch.hsr.ifs.iltis.testing.tools.pasta.tree.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


public class NodeTest {

    private TestNode<String> root;
    private TestNode<String> leftChild;
    private TestNode<String> middleChild;
    private TestNode<String> rightChild;

    @Before
    public void beforeEachTest() {
        root = new TestNode<>("root");
        leftChild = new TestNode<>("leftChild");
        middleChild = new TestNode<>("centerChild");
        rightChild = new TestNode<>("rightChild");
        root.addChild(leftChild);
        root.addChild(middleChild);
        root.addChild(rightChild);
        root.treatAsLeaf(false);
    }

    @Test
    public void testHasLeftSibling() {
        assertTrue(rightChild.hasLeftSibling());
    }

    @Test
    public void testHasLeftSiblingIfNotExisting() {
        assertFalse(leftChild.hasLeftSibling());
    }

    @Test
    public void testGetLeftSibling() {
        assertEquals(middleChild, rightChild.leftSibling());
    }

    @Test
    public void testGetLeftSiblingIfNotExisting() {
        assertEquals(null, leftChild.leftSibling());
    }

    @Test
    public void testHasRightSibling() {
        assertTrue(leftChild.hasRightSibling());
    }

    @Test
    public void testHasRightSiblingIfNotExisting() {
        assertFalse(rightChild.hasRightSibling());
    }

    @Test
    public void testGetRightSibling() {
        assertEquals(middleChild, leftChild.rightSibling());
    }

    @Test
    public void testGetRightSiblingIfNotExisting() {
        assertEquals(null, rightChild.rightSibling());
    }

    @Test
    public void testgetLeftMostSibling() {
        assertEquals(leftChild, rightChild.leftMostSibling());
    }

    @Test
    public void testgetLeftMostSiblingIfNotExisting() {
        assertEquals(null, leftChild.leftMostSibling());
    }

    @Test
    public void testgetLeftMostChild() {
        assertEquals(leftChild, root.leftMostChild());
    }

    @Test
    public void testgetLeftMostChildIfNotExisting() {
        assertEquals(null, leftChild.leftMostChild());
    }

    @Test
    public void testgetRightMostChild() {
        assertEquals(rightChild, root.rightMostChild());
    }

    @Test
    public void testgetRightMostChildIfNotExisting() {
        assertEquals(null, rightChild.rightMostChild());
    }

    @Test
    public void testShouldReturnThread() {
        TestNode<String> thread = new TestNode<>("thread");
        rightChild.setThread(thread);
        assertEquals(thread, rightChild.leftMostChild());
        assertEquals(thread, rightChild.rightMostChild());
    }

    @Test
    public void testParent() {
        assertEquals(root, rightChild.parent());
    }

    @Test
    public void testDepthParent() {
        assertEquals(0d, root.y(), 0.0d);
    }

    @Test
    public void testDepthChildren() {
        assertEquals(root.y() + 1, leftChild.y(), 0.0d);
        assertEquals(root.y() + 1, middleChild.y(), 0.0d);
        assertEquals(root.y() + 1, rightChild.y(), 0.0d);
    }

    @Test
    public void testGetNumber() {
        assertEquals(1, leftChild.number());
        assertEquals(2, middleChild.number());
        assertEquals(3, rightChild.number());
    }
}
