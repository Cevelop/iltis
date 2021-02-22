package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.collections.impl.utility.ArrayIterate;

/**
 * 
 * @author tstauber
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * 
 * @since 3.0
 *
 */
public class ASTNodeUtil {

   /**
    * Copies every node in the array
    * 
    * @param nodes
    * @param style
    * @return
    */
   public static <T extends IASTNode> T[] copy(T[] nodes, CopyStyle style) {
      return ArrayIterate.collect(nodes, n -> n.copy(style)).toArray(Arrays.copyOfRange(nodes, 0, nodes.length - 1));
   }

   //TODO javadoc
   //   public static <T extends IASTNode> IASTNodeList<T> copy(IASTNodeList<T> nodes, CopyStyle style) {
   //      return nodes.collect(n -> (T) n.copy(style), new IASTNodeList<T>());
   //   }
}
