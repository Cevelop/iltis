#ifndef ILTIS_H_
#define ILTIS_H_

#include "cute.h"

namespace mockator {
   struct call {
     template<typename ... Param>
     call(std::string const& funSig, Param const& ... params) {
     }

     std::string getTrace() const {
       return "";
     }
   };
   typedef std::vector<call> calls;
}
