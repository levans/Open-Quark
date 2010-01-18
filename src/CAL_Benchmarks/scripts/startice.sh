#!/bin/sh

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

#This is the script used to start an instance of ICE to run each benchmark on LINUX system.

# the root folder for execution
ROOTDIR="`dirname "$0"`"

# Delegate to set-javacp-base.sh for setting up the basic classpath (without adding in either CAL resources or Car-jars)
. $ROOTDIR/set-javacp-base.sh

# Do the same with set-javacp-extended.sh if it exists.
if test -f $ROOTDIR/set-javacp-extended.sh
then
   . $ROOTDIR/set-javacp-extended.sh
fi

JAVACP_PATH=$ROOTDIR/bin/cal/release:$ROOTDIR/bin/cal/debug:$JAVACP_PATH

if test -z "$QUARK_JAVACMD"
then
   QUARK_JAVACMD=java
fi

java -version
echo java $vmargs  "-Dorg.openquark.cal.workspace.spec=StandardVault cal.benchmark.test.cws" -cp $JAVACP_PATH $*
java $vmargs "-Dorg.openquark.cal.workspace.spec=StandardVault cal.benchmark.test.cws" -cp $JAVACP_PATH $*
