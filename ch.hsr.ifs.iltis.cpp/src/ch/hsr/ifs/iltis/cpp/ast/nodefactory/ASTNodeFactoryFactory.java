package ch.hsr.ifs.iltis.cpp.ast.nodefactory;

import org.eclipse.cdt.core.dom.ast.c.ICNodeFactory;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.internal.core.dom.parser.c.CNodeFactory;

@SuppressWarnings("restriction")
public abstract class ASTNodeFactoryFactory {

   public static ICNodeFactory getDefaultCNodeFactory() {
      return CNodeFactory.getDefault();
   }

   public static ICPPNodeFactory getDefaultCPPNodeFactory() {
      return ExtendedNodeFactory.getDefault();
   }
   
}
