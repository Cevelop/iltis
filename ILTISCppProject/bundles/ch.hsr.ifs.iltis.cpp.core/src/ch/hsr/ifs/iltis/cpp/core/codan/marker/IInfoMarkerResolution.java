package ch.hsr.ifs.iltis.cpp.core.codan.marker;

import org.eclipse.cdt.codan.ui.ICodanMarkerResolution;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;


/**
 * The interface common to all labeled-marker resolutions
 *
 * @author tstauber
 */
public interface IInfoMarkerResolution<T extends MarkerInfo<T>> extends ICodanMarkerResolution {

   /**
    * 
    */
   public T getInfo();

   /**
    * 
    * @param info
    */
   public void configure(final T info);

}
