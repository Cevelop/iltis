package com.cevelop.iltis.cpp.resources;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

/**
 * Helps converting resources or extracting resources from other elements.
 *
 * @author tstauber
 */
public class CPPResourceHelper {

	// TODO change to service

	/**
	 * Extracts the {@link IProject} from a {@link IASTNode}
	 *
	 * @param An
	 *            {@link IASTNode} that is part of an {@link ITranslationUnit}
	 * @return The {@link IProject} of which the node's translation unit is part
	 *         of, or null if the node is not part of a file.
	 */
	public static IProject getProject(final IASTNode node) {
		final IASTNode original = node.getOriginalNode();
		final ITranslationUnit tu = original.getTranslationUnit().getOriginatingTranslationUnit();
		final IResource res = tu.getUnderlyingResource();
		if (res == null) {
			return null;
		} else {
			return res.getProject();
		}
	}

	// TODO add new methods one by one
}
