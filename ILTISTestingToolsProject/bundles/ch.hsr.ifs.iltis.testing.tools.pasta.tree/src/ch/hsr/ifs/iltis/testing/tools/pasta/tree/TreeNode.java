package ch.hsr.ifs.iltis.testing.tools.pasta.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import ch.hsr.ifs.iltis.core.core.functional.functions.Equals;

import ch.hsr.ifs.iltis.testing.tools.pasta.tree.NodeVisitor.AfterVisitBehaviour;


@SuppressWarnings("unchecked")
public class TreeNode<DataType, SELF extends TreeNode<DataType, SELF>> {

   public final DataType data;

   protected final List<SELF> children;

   protected SELF parent;
   protected SELF ancestor;
   protected SELF thread;

   protected float   x;
   protected float   y;
   protected float   mod;
   protected int     number;
   protected float   change;
   protected float   shift;
   protected float   width;
   protected boolean treatAsLeaf;

   public TreeNode(final DataType data) {
      this(data, new ArrayList<>());
   }

   public TreeNode(final DataType data, final ArrayList<SELF> children) {
      this.parent = null;
      this.thread = null;
      this.data = data;
      this.children = children;
      this.x = 0;
      this.y = 0;
      this.mod = 0;
      this.ancestor = (SELF) this;
      this.number = 1;
      this.width = 1;
      this.treatAsLeaf = true;
   }

   public List<SELF> getChildren() {
      return children;
   }

   public SELF find(DataType data) {
      return find(data, (l, r) -> l.equals(r));
   }

   public SELF find(DataType data, Equals<DataType, DataType> comparator) {
      if (comparator.equal(data, this.data)) {
         return (SELF) this;
      } else {
         for (SELF child : getChildren()) {
            SELF subResult = child.find(data, comparator);
            if (subResult != null) return subResult;
         }
      }
      return null;
   }

   public SELF parent() {
      return parent;
   }

   /**
    * Traverse the tree post order (children first).
    *
    * @param visitor
    */
   public void visit(final NodeVisitor<DataType, SELF> visitor) {

      final AfterVisitBehaviour visit = visitor.visit((SELF) this);
      if (visit == AfterVisitBehaviour.Abort) { return; }
      for (final SELF child : children) {
         child.visit(visitor);
      }
   }

   public SELF leftMostSibling() {
      return (parent.getChildren().get(0) != this) ? parent.getChildren().get(0) : null;
   }

   public SELF leftMostChild() {
      if (thread != null) { return thread; }
      return hasChildren() ? getChildren().get(0) : null;
   }

   protected boolean hasChildren() {
      return !(children.isEmpty() || treatAsLeaf);
   }

   public SELF rightMostChild() {
      if (thread != null) { return thread; }
      return hasChildren() ? children.get(children.size() - 1) : null;
   }

   public SELF leftSibling() {
      return hasLeftSibling() ? parent.getChildren().get(this.number() - 2) : null;
   }

   public SELF rightSibling() {
      return hasRightSibling() ? parent.getChildren().get(this.number()) : null;
   }

   public boolean hasRightSibling() {
      return (parent != null && (parent.getChildren().size() > this.number()));
   }

   public boolean hasLeftSibling() {
      return (parent != null && this.number > 1);
   }

   public void addChild(SELF child) {
      children.add(child);
      child.setNumber(children.size());
      child.setY(this.y() + 1);
      child.setParent((SELF) this);
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

   protected SELF ancestor() {
      return ancestor;
   }

   protected void setAncestor(final SELF ancestor) {
      this.ancestor = ancestor;
   }

   protected float mod() {
      return mod;
   }

   protected void setMod(final float mod) {
      this.mod = mod;
   }

   protected SELF thread() {
      return thread;
   }

   public void setThread(final SELF thread) {
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

   protected void setParent(final SELF parent) {
      this.parent = parent;
   }

   public void propagateUp(final Consumer<SELF> task) {
      task.accept((SELF) this);
      if (parent != null) parent.propagateUp(task);
   }

   public void propagateDown(final Consumer<SELF> task) {
      task.accept((SELF) this);
      for (SELF child : children) {
         child.propagateDown(task);
      }
   }

   public void clearChildren() {
      children.clear();
   }

   @Override
   public String toString() {
      return data + ": number:" + number + " x:" + x + " y:" + y;
   }

   public void adjust() {
      adjust(1f, 1f);
   }

   public void adjust(final float siblingDistance, final float branchDistance) {
      JBaum.reset((SELF) this);
      JBaum.adjustTree((SELF) this, siblingDistance, branchDistance);
   }
}
