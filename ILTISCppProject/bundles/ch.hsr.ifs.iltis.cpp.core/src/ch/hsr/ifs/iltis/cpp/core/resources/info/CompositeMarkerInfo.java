package ch.hsr.ifs.iltis.cpp.core.resources.info;

import java.util.Map;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.core.runtime.Platform;

import ch.hsr.ifs.iltis.core.core.exception.ILTISException;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.NonPersistentCopyArgument;


public final class CompositeMarkerInfo extends MarkerInfo<CompositeMarkerInfo> {

   @NonPersistentCopyArgument
   public MutableList<MarkerInfo<?>> infos = Lists.mutable.empty();

   @Override
   public Map<String, String> toMap() {
      Map<String, String> markerInfoData = super.toMap();
      markerInfoData.putAll(InfoConverter.convert(infos));
      return markerInfoData;
   }

   void loadInfos(MutableList<MarkerInfo<?>> infos) {
      this.infos.addAll(infos.collect(this::copyArguments));
   }

   private MarkerInfo<?> copyArguments(MarkerInfo<?> info) {
      return info.also(c -> {
         c.fileName = fileName;
         c.severity = severity;
         c.message = message;
         c.location = location;
         c.priority = priority;
         c.done = done;
         c.charStart = charStart;
         c.charEnd = charEnd;
         c.lineNumber = lineNumber;
         c.userEditable = userEditable;
         c.sourceId = sourceId;
      });
   }

   @Override
   public InfoConverter hook_getInfoConverter() {
      return CompositeMarkerInfoConverter.INSTANCE;
   }

}



/**
 * InfoConverter subclass supporting marker infos and composite marker infos in particular
 * 
 * @author tstauber
 *
 */
class CompositeMarkerInfoConverter extends InfoConverter {

   public static CompositeMarkerInfoConverter INSTANCE = new CompositeMarkerInfoConverter();

   private CompositeMarkerInfoConverter() {};

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
