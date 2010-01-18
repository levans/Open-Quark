#!/bin/sh
cd "`dirname "$0"`"

if test -f ./javacp.sh
then
   ./javacp.sh org.openquark.samples.bam.MonitorApp $*
elif test -f ../../javacp.sh
then
   ../../javacp.sh org.openquark.samples.bam.MonitorApp $*
else
   echo "Could not find javacp.sh"
fi

