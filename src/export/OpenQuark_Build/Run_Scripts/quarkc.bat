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

@rem This script runs the Open Quark Standalone Jar Tool
@rem The arguments to this script are:
@rem [-o] <workspaceFileName> [-verbose] (-main <functionName> <mainClassName> | -lib <moduleName> <libClassScope> <libClassName>)+ <outputJarName> [-src <outputSrcZipName>]
@rem where:
@rem -o is the optional flag for turning on the CAL-based optimizer
@rem <workspaceFileName> is the name of the workspace declaration file (just the name, no paths are accepted).
@rem     This file should be in a "Workspace Declarations" subdirectory of a directory on the classpath.
@rem -verbose is the optional flag to dump out more information during the operation
@rem -main specifies a CAL application to be included with the standalone JAR.
@rem   <functionName> is the fully-qualified name of the CAL function that is the main entry point for the application.
@rem   <mainClassName> is the fully-qualified name of the Java main class to be generated.
@rem -lib specifies a CAL library to be included with the standalone JAR.
@rem   <moduleName> is the fully-qualified name of the CAL library module.
@rem   <libClassScope> is the scope of the generated library class. Can be one of: public, protected, package, or private.
@rem   <libClassName> is the fully-qualified name of the Java main class to be generated.
@rem <outputJarName> is the name of the output JAR file (can specify a path).
@rem -src <outputSrcZipName> (optional) specifies the name of the output zip file containing source files for the generated classes.

@echo off
setlocal

set VMARGS=-Dorg.openquark.cal.machine.lecc.non_interruptible

if "%1" == "-o" (
set VMARGS=%VMARGS% -Dorg.openquark.cal.optimizer_level=1
shift
)

set QUARK_CP=.;%QUARK_CP%
set QUARK_VMARGS=%VMARGS% %QUARK_VMARGS%

@javacp org.openquark.cal.services.StandaloneJarTool %1 %2 %3 %4 %5 %6 %7 %8 %9

@endlocal
