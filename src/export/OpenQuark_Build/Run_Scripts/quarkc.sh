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

# This script runs the Open Quark Standalone Jar Tool
# The arguments to this script are:
# [-o] <workspaceFileName> [-verbose] (-main <functionName> <mainClassName> | -lib <moduleName> <libClassScope> <libClassName>)+ <outputJarName> [-src <outputSrcZipName>]
# where:
# -o is the optional flag for turning on the CAL-based optimizer
# <workspaceFileName> is the name of the workspace declaration file (just the name, no paths are accepted).
#     This file should be in a "Workspace Declarations" subdirectory of a directory on the classpath.
# -verbose is the optional flag to dump out more information during the operation
# -main specifies a CAL application to be included with the standalone JAR.
#   <functionName> is the fully-qualified name of the CAL function that is the main entry point for the application.
#   <mainClassName> is the fully-qualified name of the Java main class to be generated.
# -lib specifies a CAL library to be included with the standalone JAR.
#   <moduleName> is the fully-qualified name of the CAL library module.
#   <libClassScope> is the scope of the generated library class. Can be one of: public, protected, package, or private.
#   <libClassName> is the fully-qualified name of the Java main class to be generated.
# <outputJarName> is the name of the output JAR file (can specify a path).
# -src <outputSrcZipName> (optional) specifies the name of the output zip file containing source files for the generated classes.

ROOTDIR="`dirname "$0"`"
VMARGS="-Dorg.openquark.cal.machine.lecc.non_interruptible"

if test ! -z "$1" -a "$1" = "-o"
then
    VMARGS="$VMARGS -Dorg.openquark.cal.optimizer_level=1"
    shift
fi

QUARK_CP=.:$QUARK_CP QUARK_VMARGS="$VMARGS $QUARK_VMARGS" $ROOTDIR/javacp.sh org.openquark.cal.services.StandaloneJarTool $*
