@rem
@rem Copyright (c) 2007 BUSINESS OBJECTS SOFTWARE LIMITED
@rem All rights reserved.
@rem 
@rem Redistribution and use in source and binary forms, with or without
@rem modification, are permitted provided that the following conditions are met:
@rem 
@rem     * Redistributions of source code must retain the above copyright notice,
@rem       this list of conditions and the following disclaimer.
@rem  
@rem     * Redistributions in binary form must reproduce the above copyright
@rem       notice, this list of conditions and the following disclaimer in the
@rem       documentation and/or other materials provided with the distribution.
@rem  
@rem     * Neither the name of Business Objects nor the names of its contributors
@rem       may be used to endorse or promote products derived from this software
@rem       without specific prior written permission.
@rem  
@rem THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
@rem AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
@rem IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
@rem ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
@rem LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
@rem CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
@rem SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
@rem INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
@rem CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
@rem ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
@rem POSSIBILITY OF SUCH DAMAGE.
@rem

@rem the folder where the target jars are found.
@rem this can be switched between debug and release (if Release .jars are present).
set CARJARDIR=bin/java/debug

set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/boson.default.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/cal.explibs.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/cal.explibs.test.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/cal.libraries.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/cal.libraries.test.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/cal.libraries.bobj.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/cal.libraries.bobj.test.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/cal.platform.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/cal.platform.test.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/cal.samples.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/cal.samples.test.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/cal.benchmark.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/cal.benchmark.test.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/calcebridge.test.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/everything.default.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/gemcutter.default.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/ice.default.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/isl.default.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/isl.test.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/monitor.default.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/ocb.default.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/ocb.test.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/oel.default.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/olapdrivers.default.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/olapdrivers.test.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/olapquery.default.car.jar
set JAVACP_PATH=%JAVACP_PATH%;%CARJARDIR%/olapquery.test.car.jar
