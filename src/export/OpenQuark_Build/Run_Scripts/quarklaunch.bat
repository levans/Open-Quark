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

@rem This script launches a standalone jar built with the Open Quark Standalone Jar Tool (quarkc)
@rem The arguments to this script are:
@rem <jarName> <mainClassName> <args...>
@rem where
@rem <jarName> is the name of the standalone jar. If additional jars are required, add them to the end, separated by ';'
@rem <mainClassName> is the name of the main class in the standalone jar
@rem <args...> are 0 or more arguments to be passed into the main class

@echo off
setlocal

set ROOTDIR=.
set USER_CP="%1"
shift
set VMARGS=-Dorg.openquark.cal.machine.lecc.non_interruptible

set CAL_RUNTIME_CP=%ROOTDIR%\bin\java\release\calRuntime.jar
set CAL_UTILITIES_CP=%ROOTDIR%\bin\java\release\calUtilities.jar;%ROOTDIR%\lib\Resources\External\java\icu4j.jar
set CAL_LIBRARIES_CP=%ROOTDIR%\bin\java\release\calLibraries.jar;%ROOTDIR%\lib\Resources\External\java\log4j.jar

set CAL_BOOT_CP=%CAL_RUNTIME_CP%;%CAL_UTILITIES_CP%;%CAL_LIBRARIES_CP%

if not defined QUARK_JAVACMD (
set QUARK_JAVACMD=java
)

@%QUARK_JAVACMD% -Xmx256m %VMARGS% %QUARK_VMARGS% -cp %USER_CP%;%QUARK_CP% -Xbootclasspath/a:%CAL_BOOT_CP% %1 %2 %3 %4 %5 %6 %7 %8 %9

@endlocal
