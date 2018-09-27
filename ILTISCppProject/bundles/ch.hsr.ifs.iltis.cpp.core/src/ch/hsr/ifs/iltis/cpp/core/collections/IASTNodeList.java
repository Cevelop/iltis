package ch.hsr.ifs.iltis.cpp.core.collections;

import java.util.Collection;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;


public class IASTNodeList<T extends IASTNode> extends FastList<T> implements IASTNodeCollection<T> {

   public IASTNodeList() {}

   public IASTNodeList(int initialCapacity) {
      super(initialCapacity);
   }

   public IASTNodeList(Collection<? extends T> source) {
      super(source);
   }

   public boolean envelops(IASTNode node) {
      return Iterate.anySatisfy(this, n -> n.contains(node));
   }

   public boolean envelopsAll(Collection<? extends IASTNode> nodes) {
      return envelopsAll(nodes.toArray(new IASTNode[nodes.size()]));
   }

   public boolean envelopsAll(IASTNode... nodes) {
      return ArrayIterate.allSatisfy(nodes, this::envelops);
   }

   /* From FastList */

   @Override
   public IASTNodeList<T> reject(Predicate<? super T> predicate) {
      return this.reject(predicate, new IASTNodeList<>());
   }

}
