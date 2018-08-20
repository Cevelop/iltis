package ch.hsr.ifs.iltis.cpp.core.ui.refactoring;

import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;

import ch.hsr.ifs.iltis.core.core.collections.UnifiedMarkerArgumentMap;
import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.core.core.functional.functions.Consumer;
import ch.hsr.ifs.iltis.core.ltk.refactoring.InfoArgument;


public abstract class MarkerInfo<T extends MarkerInfo<T>> implements IInfo {

   public static final String  FILENAME_DEFAULT      = "";
   public static final int     SEVERITY_DEFAULT      = IMarker.SEVERITY_INFO;
   public static final String  MESSAGE_DEFAULT       = "";
   public static final String  LOCATION_DEFAULT      = "";
   public static final int     PRIORITY_DEFAULT      = IMarker.PRIORITY_NORMAL;
   public static final boolean DONE_DEFAULT          = false;
   public static final int     CHAR_START_DEFAULT    = -1;
   public static final int     CHAR_END_DEFAULT      = -1;
   public static final int     LINE_NUMBER_DEFAULT   = -1;
   public static final boolean USER_EDITABLE_DEFAULT = false;
   public static final String  SOURCE_ID_DEFAULT     = "";

   @InfoArgument
   public String  fileName     = FILENAME_DEFAULT;
   @InfoArgument
   public int     severity     = SEVERITY_DEFAULT;
   @InfoArgument
   public String  message      = MESSAGE_DEFAULT;
   @InfoArgument
   public String  location     = LOCATION_DEFAULT;
   @InfoArgument
   public int     priority     = PRIORITY_DEFAULT;
   @InfoArgument
   public boolean done         = DONE_DEFAULT;
   @InfoArgument
   public int     charStart    = CHAR_START_DEFAULT;
   @InfoArgument
   public int     charEnd      = CHAR_END_DEFAULT;
   @InfoArgument
   public int     lineNumber   = LINE_NUMBER_DEFAULT;
   @InfoArgument
   public boolean userEditable = USER_EDITABLE_DEFAULT;
   @InfoArgument
   public String  sourceId     = SOURCE_ID_DEFAULT;

   public boolean usesSelection() {
      return charStart > -1 && charEnd > -1;
   }

   public Optional<ITextSelection> getSelection() {
      return (usesSelection()) ? Optional.of(new TextSelection(charStart, charEnd - charStart)) : Optional.empty();
   }

   public void setSelection(Optional<ITextSelection> selection) {
      charStart = selection.map(s -> s.getOffset()).orElse(-1);
      charEnd = selection.map(s -> s.getOffset() + s.getLength()).orElse(-1);
   }

   @SuppressWarnings("unchecked")
   public T also(Consumer<T> c) {
      c.accept((T) this);
      return (T) this;
   }

   public static <R extends MarkerInfo<R>> R fromCodanProblemMarker(Supplier<R> constructor, IMarker marker) {
      R info = constructor.get();
      InfoConverter.fillFields(info, UnifiedMarkerArgumentMap.fromCodanProblemMarker(marker));
      return info;
   }

   public static <R extends MarkerInfo<R>> R fromMarker(Supplier<R> constructor, IMarker marker) {
      R info = constructor.get();
      try {
         InfoConverter.fillFields(info, marker.getAttributes());
      } catch (CoreException e) {
         ILTISException.wrap(e).rethrowUnchecked();
      }
      return info;
   }
}
