package ch.hsr.ifs.iltis.testing.tools.pasta.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.hsr.ifs.iltis.testing.tools.pasta.tree.NodeVisitor.AfterVisitBehaviour;


public class TreeNode<DataType> {

   public final DataType             data;
   private final List<TreeNode<DataType>> children;

   private TreeNode<DataType> parent;
   private TreeNode<DataType> ancestor;
   private TreeNode<DataType> thread;
   private float   x;
   private float   y;
   private float   mod;
   private int     number;
   private float   change;
   private float   shift;
   private float   width;
   private boolean treatAsLeaf;

   public TreeNode(final DataType data) {
      this(new ArrayList<TreeNode<DataType>>(), data);
   }

   public TreeNode(final List<TreeNode<DataType>> children, final DataType data) {
      this.parent = null;
      this.thread = null;
      this.data = data;
      this.children = children;
      this.x = 0;
      this.y = 0;
      this.mod = 0;
      this.ancestor = this;
      this.number = 1;
      this.width = 1;
      this.treatAsLeaf = false;
   }

   public List<TreeNode<DataType>> children() {
      return hasChildren() ? children : Collections.<TreeNode<DataType>>emptyList();
   }

   public List<TreeNode<DataType>> getChildren() {
      return children;
   }

   public TreeNode<DataType> parent() {
      return parent;
   }

   /**
    * Traverse the tree post order (children first).
    *
    * @param visitor
    */
   public void visit(final NodeVisitor<DataType> visitor) {

      final AfterVisitBehaviour visit = visitor.visit(this);
      if (visit == AfterVisitBehaviour.Abort) { return; }
      for (final TreeNode<DataType> child : children) {
         child.visit(visitor);
      }
   }

   public TreeNode<DataType> leftMostSibling() {
      return (parent.children().get(0) != this) ? parent.children().get(0) : null;
   }

   public TreeNode<DataType> leftMostChild() {
      if (thread != null) { return thread; }
      return hasChildren() ? children.get(0) : null;
   }

   private boolean hasChildren() {
      return !(children.isEmpty() || treatAsLeaf);
   }

   public TreeNode<DataType> rightMostChild() {
      if (thread != null) { return thread; }
      return hasChildren() ? children.get(children.size() - 1) : null;
   }

   public TreeNode<DataType> leftSibling() {
      return hasLeftSibling() ? parent.children().get(this.number() - 2) : null;
   }

   public TreeNode<DataType> rightSibling() {
      return hasRightSibling() ? parent.children().get(this.number()) : null;
   }

   public boolean hasRightSibling() {
      return (parent != null && (parent.children().size() > this.number()));
   }

   public boolean hasLeftSibling() {
      return (parent != null && this.number > 1);
   }

   public void addChild(final TreeNode<DataType> child) {
      children.add(child);
      child.setNumber(children.size());
      child.setY(this.y() + 1);
      child.setParent(this);
   }

   public float x() {
      return x;
   }

   protected void setX(final float x) {
      this.x = x;
   }

   public float y() {
      return y;
   }

   protected void setY(final float y) {
      this.y = y;
   }

   public float width() {
      return width;
   }

   public void setWidth(final float width) {
      this.width = width;

   }

   public void treatAsLeaf(final boolean isLeaf) {
      this.treatAsLeaf = isLeaf;
   }

   public boolean isTreatedAsLeaf() {
      return treatAsLeaf;
   }

   protected TreeNode<DataType> ancestor() {
      return ancestor;
   }

   protected void setAncestor(final TreeNode<DataType> ancestor) {
      this.ancestor = ancestor;
   }

   protected float mod() {
      return mod;
   }

   protected void setMod(final float mod) {
      this.mod = mod;
   }

   protected TreeNode<DataType> thread() {
      return thread;
   }

   public void setThread(final TreeNode<DataType> thread) {
      this.thread = thread;
   }

   protected void setNumber(final int number) {
      this.number = number;
   }

   public int number() {
      return number;
   }

   protected float change() {
      return change;
   }

   protected void setChange(final float change) {
      this.change = change;

   }

   protected float shift() {
      return shift;
   }

   protected void setShift(final float shift) {
      this.shift = shift;
   }

   protected void setParent(final TreeNode<DataType> parent) {
      this.parent = parent;
   }

   @Override
   public String toString() {
      return data + ": number:" + number + " x:" + x + " y:" + y;
   }

   public void adjust() {
      JBaum.reset(this);
      JBaum.adjustTree(this, 1f, 1f);
   }

   public void adjust(final float siblingDistance, final float branchDistance) {
      JBaum.reset(this);
      JBaum.adjustTree(this, siblingDistance, branchDistance);
   }
}
