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

# the root folder for execution
ROOTDIR="`dirname "$0"`"

# the folder where the target jars are found.
# this can be switched between debug and release (if release .jars are present).
CARJARDIR=$ROOTDIR/bin/java/debug

JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/boson.default.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/cal.explibs.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/cal.explibs.test.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/cal.libraries.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/cal.libraries.test.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/cal.libraries.bobj.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/cal.libraries.bobj.test.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/cal.platform.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/cal.platform.test.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/cal.samples.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/cal.samples.test.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/cal.benchmark.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/cal.benchmark.test.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/calcebridge.test.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/everything.default.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/gemcutter.default.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/ice.default.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/isl.default.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/isl.test.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/monitor.default.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/ocb.default.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/ocb.test.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/oel.default.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/olapdrivers.default.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/olapdrivers.test.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/olapquery.default.car.jar
JAVACP_PATH=$JAVACP_PATH:$CARJARDIR/olapquery.test.car.jar
