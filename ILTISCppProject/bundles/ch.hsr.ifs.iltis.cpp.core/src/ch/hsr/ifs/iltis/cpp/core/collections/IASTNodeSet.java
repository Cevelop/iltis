package ch.hsr.ifs.iltis.cpp.core.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;


public class IASTNodeSet<NodeType extends IASTNode> extends TreeSortedSet<NodeType> implements IASTNodeCollection<NodeType> {

   public IASTNodeSet() {
      super();
   }

   public IASTNodeSet(Iterable<NodeType> iterable) {
      super(iterable);
   }

   public IASTNodeSet(Comparator<NodeType> comparator) {
      super(comparator);
   }

   public IASTNodeSet(SortedSet<NodeType> set) {
      super(set);
   }

   public IASTNodeSet(Comparator<NodeType> comparator, Iterable<NodeType> iterable) {
      super(comparator, iterable);
   }

   /**
    * Checks is the node is contained in one of the nodes stored in this list
    * 
    * @param node
    * @return
    */
   public boolean envelops(IASTNode node) {
      return Iterate.anySatisfy(this, n -> n.contains(node));
   }

   public boolean envelopsAll(Collection<? extends IASTNode> nodes) {
      return envelopsAll(nodes.toArray(new IASTNode[nodes.size()]));
   }

   public boolean envelopsAll(IASTNode... nodes) {
      return ArrayIterate.allSatisfy(nodes, this::envelops);
   }

}
