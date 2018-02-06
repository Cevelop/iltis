package ch.hsr.ifs.iltis.cpp.codan.marker;

import org.eclipse.cdt.codan.ui.ICodanMarkerResolution;


/**
 * The interface common to all labeled-marker resolutions
 *
 * @author tstauber
 */
public interface ILabeledMarkerResolution extends ICodanMarkerResolution {

   @Override
   /** 
    * Returns the label for this marker
    */
   public String getLabel();

   /**
    * Sets the label for this marker
    * @param labelText
    */
   public void setLabel(final String labelText);

}
