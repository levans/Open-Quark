#!/bin/sh

#
# Copyright (c) 2007 BUSINESS OBJECTS SOFTWARE LIMITED
# All rights reserved.
# 
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
# 
#     * Redistributions of source code must retain the above copyright notice,
#       this list of conditions and the following disclaimer.
#  
#     * Redistributions in binary form must reproduce the above copyright
#       notice, this list of conditions and the following disclaimer in the
#       documentation and/or other materials provided with the distribution.
#  
#     * Neither the name of Business Objects nor the names of its contributors
#       may be used to endorse or promote products derived from this software
#       without specific prior written permission.
#  
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#

# This script launches a standalone jar built with the Open Quark Standalone Jar Tool (quarkc)
# The arguments to this script are:
# <jarName> <mainClassName> <args...>
# where
# <jarName> is the name of the standalone jar. If additional jars are required, add them to the end, separated by ':'
# <mainClassName> is the name of the main class in the standalone jar
# <args...> are 0 or more arguments to be passed into the main class

ROOTDIR="`dirname "$0"`"
USER_CP=$1
shift
VMARGS="-Dorg.openquark.cal.machine.lecc.non_interruptible"

CAL_RUNTIME_CP=$ROOTDIR/bin/java/release/calRuntime.jar
CAL_UTILITIES_CP=$ROOTDIR/bin/java/release/calUtilities.jar:$ROOTDIR/lib/Resources/External/java/icu4j.jar
CAL_LIBRARIES_CP=$ROOTDIR/bin/java/release/calLibraries.jar:$ROOTDIR/lib/Resources/External/java/log4j.jar

CAL_BOOT_CP=$CAL_RUNTIME_CP:$CAL_UTILITIES_CP:$CAL_LIBRARIES_CP

if test -z "$QUARK_JAVACMD"
then
   QUARK_JAVACMD=java
fi

$QUARK_JAVACMD -Xmx256m $VMARGS $QUARK_VMARGS -cp $USER_CP:$QUARK_CP -Xbootclasspath/a:$CAL_BOOT_CP $*
