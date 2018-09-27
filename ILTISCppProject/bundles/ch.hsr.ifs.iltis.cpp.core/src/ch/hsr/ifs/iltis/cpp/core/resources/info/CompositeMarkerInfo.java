package ch.hsr.ifs.iltis.cpp.core.resources.info;

import java.util.Map;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

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
}
