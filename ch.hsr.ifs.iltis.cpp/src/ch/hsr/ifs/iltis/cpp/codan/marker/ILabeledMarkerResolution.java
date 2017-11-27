package ch.hsr.ifs.iltis.cpp.codan.marker;

import org.eclipse.cdt.codan.ui.ICodanMarkerResolution;


/**
 * The interface common for all labeled-marker resolutions
 *
 * @author tstauber
 */
public interface ILabeledMarkerResolution extends ICodanMarkerResolution {

   @Override
   public String getLabel();

   public void setLabel(final String labelText);

}
