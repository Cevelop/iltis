package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.example.examplecodantest;

import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.resources.info.annotations.MessageInfoArgument;


public class ExampleInfo extends MarkerInfo<ExampleInfo> {

   @MessageInfoArgument(0)
   public String name;

   public ExampleInfo() {}

   public ExampleInfo(String name) {
      this.name = name;
   }
}
