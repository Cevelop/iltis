package ch.hsr.ifs.iltis.cpp.ast.nodefactory;

import org.eclipse.cdt.core.dom.ast.c.ICNodeFactory;
import org.eclipse.cdt.internal.core.dom.parser.c.CNodeFactory;


@SuppressWarnings("restriction")
/**
 * An alternative to the cdt ASTNodeFactoryFactory
 * This factory provides a IBetterFactory as ICPPNodeFactory
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @author tstauber
 *
 */
public abstract class ASTNodeFactoryFactory {

   public static ICNodeFactory getDefaultCNodeFactory() {
      return CNodeFactory.getDefault();
   }

   public static IBetterFactory getDefaultCPPNodeFactory() {
      return ExtendedNodeFactory.getDefault();
   }

}
