package ch.hsr.ifs.iltis.cpp.codan.marker;

import org.eclipse.cdt.codan.ui.ICodanMarkerResolution;


/**
 * @author tstauber
 */
public interface ILabeledMarkerResolution extends ICodanMarkerResolution {

   @Override
   public String getLabel();

   public void setLabel(final String labelText);

}
