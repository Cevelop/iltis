package ch.hsr.ifs.iltis.cpp.core.wrappers;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.ui.refactoring.changes.CreateFileChange;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.resource.DeleteResourceChange;
import org.eclipse.ltk.core.refactoring.resource.MoveResourceChange;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.eclipse.ltk.core.refactoring.resource.ResourceChange;


/**
 * A wrapper class for the cdt ModificationCollector. Using this wrapper reduces the amount of warnings respectively the amount of
 * {@code @SuppressWarnings} tags.
 *
 * It further provides an API to allow for more than just file creation changes.
 *
 * @author tstauber, hpatzen
 *
 */
@SuppressWarnings("restriction")
public class ModificationCollector {

    private final IResourceChangeDescriptionFactory deltaFactory;

    // Each translation unit can have only one ASTRewrite.
    private final MutableMap<String, ASTRewrite> rewriters = Maps.mutable.empty();
    private final MutableList<Change>            changes   = Lists.mutable.empty();

    public ModificationCollector() {
        this(null);
    }

    public ModificationCollector(final IResourceChangeDescriptionFactory deltaFactory) {
        this.deltaFactory = deltaFactory;
    }

    public ASTRewrite rewriterForTranslationUnit(final IASTTranslationUnit ast) {
        final String filePath = ast.getFilePath();
        if (!rewriters.containsKey(filePath)) {
            rewriters.put(filePath, ASTRewrite.create(ast));
            addToDelta((IFile) ast.getOriginatingTranslationUnit().getResource());
        }
        return rewriters.get(filePath);
    }

    /**
     * @use {@link #createResourceChange(String, IPath, String, String)} instead.
     *
     * @param change
     * The createFileChange
     */
    @Deprecated
    public void addFileChange(final CreateFileChange change) {
        changes.add(change);
        addToDelta(change);
    }

    /**
     * Allows to add changes that will be merged into the change created by
     * {@link #createFinalChange()}
     *
     * @param change
     * The {@link TextChange} to merge.
     */
    public void addChange(final TextChange change) {
        changes.add(change);
    }

    /**
     * Creates and adds a {@link CreateFileChange}.
     *
     * @param name
     * the file name
     * @param path
     * the file path
     * @param source
     * the file content
     * @param encoding
     * the file encoding
     */
    public void createResourceChange(final String name, final IPath path, final String source, final String encoding) {
        final CreateFileChange change = new CreateFileChange(name, path, source, encoding);
        changes.add(change);
        addToDelta(change);
    }

    /**
     * Creates and adds {@link DeleteResourceChange}
     *
     * @param resourcePath
     * the resource path
     * @param forceOutOfSync
     * if <code>true</code>, deletes the resource with {@link IResource#FORCE}
     */
    public void deleteResourceChange(final IPath resourcePath, final boolean forceOutOfSync) {
        deleteResourceChange(resourcePath, forceOutOfSync, false);
    }

    /**
     * Creates and adds {@link DeleteResourceChange}
     *
     * @param resourcePath
     * the project path
     * @param forceOutOfSync
     * if <code>true</code>, deletes the resource with {@link IResource#FORCE}
     * @param deleteContent
     * if <code>true</code> delete the project contents.
     * The content delete is not undoable. This setting only applies to projects and is not used when deleting files or folders.
     */
    public void deleteResourceChange(final IPath resourcePath, final boolean forceOutOfSync, final boolean deleteContent) {
        final DeleteResourceChange change = new DeleteResourceChange(resourcePath, forceOutOfSync, deleteContent);
        changes.add(change);
        addToDelta(change);
    }

    /**
     * Creates and adds {@link MoveResourceChange}
     *
     * @param source
     * the resource to move
     * @param target
     * the container the resource is moved to. An existing resource at the destination will be
     * replaced.
     */
    public void moveResourceChange(final IResource source, final IContainer target) {
        final MoveResourceChange change = new MoveResourceChange(source, target);
        changes.add(change);
        addToDelta(change, target.getFullPath());
    }

    /**
     * Creates and adds {@link RenameResourceChange}
     *
     * @param resourcePath
     * the path of the resource to rename
     * @param newName
     * the new name. Must not be empty.
     */
    public void renameResourceChange(final IPath resourcePath, final String newName) {
        final RenameResourceChange change = new RenameResourceChange(resourcePath, newName);
        changes.add(change);
        addToDelta(change, resourcePath.removeLastSegments(1).append(newName));
    }

    /**
     * Creates the final change, merging all other changes into one {@link ch.hsr.ifs.iltis.cpp.core.wrappers.CCompositeChange}.
     *
     * @return a new {@link ch.hsr.ifs.iltis.cpp.core.wrappers.CCompositeChange}.
     */
    public CCompositeChange createFinalChange() {
        // Synthetic changes aren't displayed and therefore don't need a name
        final CCompositeChange result = new CCompositeChange("");
        result.markAsSynthetic();

        rewriters.forEachValue(r -> result.flatAdd(r.rewriteAST()));
        changes.forEach(result::flatAdd);

        return result;
    }

    private IResource getResource(final ResourceChange change) {
        return (IResource) change.getModifiedElement();
    }

    private void addToDelta(final IFile file) {
        if (deltaFactory == null) return;
        deltaFactory.change(file);
    }

    private void addToDelta(final CreateFileChange change) {
        if (deltaFactory == null) return;
        deltaFactory.create(getResource(change));
    }

    private void addToDelta(final DeleteResourceChange change) {
        if (deltaFactory == null) return;
        deltaFactory.delete(getResource(change));
    }

    private void addToDelta(final MoveResourceChange change, final IPath destination) {
        if (deltaFactory == null) return;
        deltaFactory.move(getResource(change), destination);
    }

    private void addToDelta(final RenameResourceChange change, final IPath destination) {
        if (deltaFactory == null) return;
        deltaFactory.move(getResource(change), destination);
    }
}
