package ch.hsr.ifs.iltis.cpp.core.resources.info;

import java.util.Map;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;


public final class CompositeMarkerInfo extends MarkerInfo<CompositeMarkerInfo> {

   public MutableList<MarkerInfo<?>> infos = Lists.mutable.empty();

   public CompositeMarkerInfo() {}

   /**
    * Copy constructor.
    * <p>
    * This constructor does not add a CompositeInfo but
    * instead creates a copy of it.
    * 
    * @param info
    *        The CompositieInfo to be copied from.
    */
   public CompositeMarkerInfo(CompositeMarkerInfo info) {
      super(info);
      infos = info.infos.clone();
   }

   @Override
   public CompositeMarkerInfo copy() {
      return new CompositeMarkerInfo(this);
   }

   @Override
   public Map<String, String> toMap() {
      Map<String, String> markerInfoData = super.toMap();
      markerInfoData.putAll(InfoConverter.convert(infos.toList()));
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
