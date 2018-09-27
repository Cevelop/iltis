package ch.hsr.ifs.iltis.cpp.core.resources.info;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.cdt.codan.internal.core.model.CodanProblemMarker;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.core.core.functional.functions.Consumer;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.InfoArgument;


/**
 * A IInfo optimized to work with IMarker and CodanProblemMarker.
 *
 * @author void
 *
 * @param <InfoType>
 */
@SuppressWarnings("restriction")
public abstract class MarkerInfo<InfoType extends MarkerInfo<InfoType>> implements IInfo<InfoType> {

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
   public String  id           = "";
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


   @Override
   public InfoConverter hook_getInfoConverter() {
      return MarkerInfoConverter.INSTANCE;
   }

   /**
    * Comfort method to test if this MarkerInfo uses a selection.
    *
    * @return {@code true} iff both {@link #charStart} and {@link #charEnd} are greater than {@code -1};
    */
   public boolean usesSelection() {
      return charStart > -1 && charEnd > -1;
   }

   /**
    * Comfort method to get an ITextSelection from the {@link #charStart} and {@link #charEnd} fields.
    *
    * @return An optional containing a text selection or an empty optional if {@link #usesSelection()} returns false.
    */
   public Optional<ITextSelection> getSelection() {
      return usesSelection() ? Optional.of(new TextSelection(charStart, charEnd - charStart)) : Optional.empty();
   }

   /**
    * Comfort method to set the {@link #charStart} and {@link #charEnd} fields from a ITextSelection.
    *
    * @param selection
    *        The ITextSelection.
    */
   public void setSelection(final Optional<ITextSelection> selection) {
      charStart = selection.map(s -> s.getOffset()).orElse(-1);
      charEnd = selection.map(s -> s.getOffset() + s.getLength()).orElse(-1);
   }

   /**
    * Converts this info's {@link InfoArgument} fields to an object array.
    *
    * <pre></pre>
    *
    * <b>This operation will be long running. If used multiple times, this array should be cached, as long as the values in the info did not
    * change.</b>
    *
    * <pre></pre>
    *
    * @return The unified array
    */
   public Object[] toCodanProblemArgsArray() {
      final Field[] fields = getClass().getFields();
      try {
         final MutableList<Field> sortedMessageInfoFields = InfoConverter.getMessageInfoArgFieldsOrdered(fields);
         final MutableList<Field> infoArgumentFields = InfoConverter.getInfoArgumentFields(fields);

         final Object[] array = new String[(sortedMessageInfoFields.size() + infoArgumentFields.size()) * 2];
         int valueIndex = 0;
         int keyIndex = array.length / 2;

         for (final Field f : sortedMessageInfoFields) {
            InfoConverter.validateMessageInfoArgField(f);
            array[keyIndex++] = f.getName();
            array[valueIndex++] = f.get(this);
         }
         for (final Field f : infoArgumentFields) {
            InfoConverter.validateInfoArgField(f);
            array[keyIndex++] = f.getName();
            array[valueIndex++] = IStringifyable.class.isAssignableFrom(f.getType()) ? ((IStringifyable<?>) f.get(this)).stringify() : String.valueOf(
                  f.get(this));
         }
         return array;
      } catch (IllegalArgumentException | IllegalAccessException e) {
         throw ILTISException.wrap(e).rethrowUnchecked();
      }
   }

   /**
    * Creates a new info and loads the fields from the unified array into it.
    *
    * @param constructor
    *        The constructor for the specific info.
    * @param unifiedArray
    *        The unified array
    * @return A new info with the data from the unifiedArray loaded.
    */
   static <R extends IInfo<R>> R fromCodanProblemArgsArray(final Supplier<R> constructor, final Object[] unifiedArray) {
      final MutableMap<String, Object> map = Maps.mutable.empty();
      for (int valueIndex = 0, keyIndex = unifiedArray.length / 2; keyIndex < unifiedArray.length; valueIndex++, keyIndex++) {
         map.put((String) unifiedArray[keyIndex], unifiedArray[valueIndex]);
      }
      return IInfo.fromMap(constructor, map);
   }

   /**
    * Comfort method similar to the Kotlin .also method.
    */
   @SuppressWarnings("unchecked")
   public InfoType also(final Consumer<InfoType> c) {
      c.accept((InfoType) this);
      return (InfoType) this;
   }

   /**
    * Recreates the marker info from a codan problem marker. This requires the codan problem marker attributes to be well-formed.
    *
    * @param constructor
    *        A constructor for a MarkerInfo subclass.
    * @param marker
    *        A marker registered as codan problem marker.
    * @return A new marker info, or null on failure.
    */
   public static <R extends MarkerInfo<R>> R fromCodanProblemMarker(final Supplier<R> constructor, final IMarker marker) {
      return fromCodanProblemArgsArray(constructor, CodanProblemMarker.getProblemArguments(marker)).also(ILTISException.sterilize(i -> {
         i.hook_getInfoConverter().fillFieldsPresentInMap(i, marker.getAttributes());
      }));
   }

   /**
    * Recreates the marker info from an IMarker.
    *
    * @param constructor
    *        A constructor for a MarkerInfo subclass
    * @param marker
    *        A marker
    * @return A new marker info, or null on failure.
    */
   public static <R extends MarkerInfo<R>> R fromMarker(final Supplier<R> constructor, final IMarker marker) {
      try {
         return IInfo.fromMap(constructor, marker.getAttributes());
      } catch (final CoreException e) {
         ILTISException.wrap(e).rethrowUnchecked();
         return null;
      }
   }

}



/**
 * InfoConverter subclass supporting marker infos and composite marker infos in particular
 * 
 * @author tstauber
 *
 */
class MarkerInfoConverter extends InfoConverter {

   public static MarkerInfoConverter INSTANCE = new MarkerInfoConverter();

   private MarkerInfoConverter() {};

   @Override
   protected <R extends IInfo<R>, T> void fillAllFieldsInInfo(final R info, final Map<String, T> map) {
      super.fillAllFieldsInInfo(info, map);
      if (info instanceof CompositeMarkerInfo) {
         ((CompositeMarkerInfo) info).loadInfos(createInfos(map).collectIf(MarkerInfo.class::isInstance, i -> (MarkerInfo<?>) i));
      }
   }

   @SuppressWarnings("unchecked")
   protected static <R extends IInfo<R>, T> MutableList<R> createInfos(final Map<String, T> data) {
      final MutableMap<String, String> infosData = Maps.adapt(data).keyValuesView().select(p -> p.getOne().startsWith(INFO_DATA_PREFIX)).toMap(p -> p
            .getOne().substring(INFO_DATA_PREFIX.length()), p -> (String) p.getTwo());

      return infosData.keyValuesView().collect(p -> MarkerInfo.fromCodanProblemArgsArray(ILTISException.sterilize(() -> {
         String[] infoElement = p.getOne().split(INFO_TYPE_SEPARATOR);
         return (R) Platform.getBundle(infoElement[0]).loadClass(infoElement[1]).newInstance();
      }), p.getTwo().split(INFO_DATA_SEPARATOR))).toList();
   }
}
