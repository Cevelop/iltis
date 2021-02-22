package ch.hsr.ifs.iltis.testing.tools.pasta.util;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;

import ch.hsr.ifs.iltis.testing.tools.pasta.visualtree.VisualASTNode;


public class VASTNodeMouseTrackAndClickAdapter implements MouseListener, MouseTrackListener, Cloneable {

    private VisualASTNode visualASTNode;

    public VASTNodeMouseTrackAndClickAdapter cloneForNode(VisualASTNode visualASTNode) {
        VASTNodeMouseTrackAndClickAdapter clone;
        try {
            clone = (VASTNodeMouseTrackAndClickAdapter) this.clone();
            clone.visualASTNode = visualASTNode;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Cloning failed");
        }
    }

    public void setVisualASTNode(VisualASTNode visualASTNode) {
        this.visualASTNode = visualASTNode;
    }

    @Override
    public final void mouseEnter(MouseEvent e) {
        mouseEnter(e, visualASTNode);
    }

    @Override
    public final void mouseExit(MouseEvent e) {
        mouseExit(e, visualASTNode);
    }

    @Override
    public final void mouseHover(MouseEvent e) {
        mouseHover(e, visualASTNode);
    }

    @Override
    public final void mouseDoubleClick(MouseEvent e) {
        mouseDoubleClick(e, visualASTNode);
    }

    @Override
    public final void mouseDown(MouseEvent e) {
        mouseDown(e, visualASTNode);
    }

    @Override
    public final void mouseUp(MouseEvent e) {
        mouseUp(e, visualASTNode);
    }

    public void mouseEnter(MouseEvent e, VisualASTNode node) {}

    public void mouseExit(MouseEvent e, VisualASTNode node) {}

    public void mouseHover(MouseEvent e, VisualASTNode node) {}

    public void mouseDoubleClick(MouseEvent e, VisualASTNode node) {}

    public void mouseDown(MouseEvent e, VisualASTNode node) {}

    public void mouseUp(MouseEvent e, VisualASTNode node) {}

}
