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

# this file allows one to execute a java class with all the jars available on the classpath

# the root folder for execution
ROOTDIR="`dirname "$0"`"

# add the bin folder to the path, so that dll's there can be found.
PATH=$ROOTDIR/bin:$PATH

# the folder where the target jars are found.
# this can be switched between debug and release (if release .jars are present).
BINDIR=$ROOTDIR/bin/java/release

# the folder where resources are found.
LIBDIR=$ROOTDIR/lib

# the folder where resources are found.
BAMSAMPLEDIR=$ROOTDIR/samples/bam
CALSAMPLESDIR=$ROOTDIR/samples/simple


# Set the run path.
JAVACP_PATH=$QUARK_CP:$ROOTDIR:$LIBDIR

JAVACP_PATH=$JAVACP_PATH:$BINDIR/calUtilities.jar
JAVACP_PATH=$JAVACP_PATH:$BINDIR/calRuntime.jar
JAVACP_PATH=$JAVACP_PATH:$BINDIR/calPlatform.jar
JAVACP_PATH=$JAVACP_PATH:$BINDIR/calPlatform_test.jar
JAVACP_PATH=$JAVACP_PATH:$BINDIR/calLibraries.jar
JAVACP_PATH=$JAVACP_PATH:$BINDIR/calLibraries_test.jar
JAVACP_PATH=$JAVACP_PATH:$BINDIR/quarkGems.jar
JAVACP_PATH=$JAVACP_PATH:$BINDIR/quarkGems_test.jar
JAVACP_PATH=$JAVACP_PATH:$BAMSAMPLEDIR
JAVACP_PATH=$JAVACP_PATH:$CALSAMPLESDIR
JAVACP_PATH=$JAVACP_PATH:$BINDIR/bamSample.jar
JAVACP_PATH=$JAVACP_PATH:$BINDIR/calBenchmarks.jar
JAVACP_PATH=$JAVACP_PATH:$BINDIR/calBenchmarks_test.jar
JAVACP_PATH=$JAVACP_PATH:$BINDIR/calSamples.jar
JAVACP_PATH=$JAVACP_PATH:$BINDIR/calSamples_test.jar

JAVACP_PATH=$JAVACP_PATH:$LIBDIR/Resources/GemCutterHelpFiles.jar
JAVACP_PATH=$JAVACP_PATH:$LIBDIR/Resources/External/java/asm-all-3.0.jar
JAVACP_PATH=$JAVACP_PATH:$LIBDIR/Resources/External/java/commons-collections-3.1.jar
JAVACP_PATH=$JAVACP_PATH:$LIBDIR/Resources/External/java/icu4j.jar
JAVACP_PATH=$JAVACP_PATH:$LIBDIR/Resources/External/java/junit.jar
JAVACP_PATH=$JAVACP_PATH:$LIBDIR/Resources/External/java/log4j.jar

JAVACP_PATH=$JAVACP_PATH:$LIBDIR/Resources/External/java/xercesImpl.jar
JAVACP_PATH=$JAVACP_PATH:$LIBDIR/Resources/External/java/xmlParserAPIs.jar

JAVACP_PATH=$JAVACP_PATH:$LIBDIR/Resources/External/java/antlr.jar
JAVACP_PATH=$JAVACP_PATH:$LIBDIR/Resources/External/Sun/JavaHelp/2.0_02/jh.jar
