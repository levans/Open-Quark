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

@rem this file allows one to execute a java class with all the jars available on the classpath

@rem add the bin folder to the path, so that dll's there can be found.
set PATH=bin;%PATH%

@rem the root folder for execution
set ROOTDIR=.

@rem the folder where the target jars are found.
@rem this can be switched between debug and release (if release .jars are present).
set BINDIR=bin/java/release

@rem the folder where resources are found.
set LIBDIR=lib

@rem the folders where samples are found
set BAMSAMPLEDIR=samples/bam
set CALSAMPLESDIR=samples/simple


@rem Set the run path.
set JAVACP_PATH=%QUARK_CP%;%ROOTDIR%;%LIBDIR%

set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/calUtilities.jar
set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/calRuntime.jar
set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/calPlatform.jar
set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/calPlatform_test.jar
set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/calLibraries.jar
set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/calLibraries_test.jar
set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/quarkGems.jar
set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/quarkGems_test.jar
set JAVACP_PATH=%JAVACP_PATH%;%BAMSAMPLEDIR%
set JAVACP_PATH=%JAVACP_PATH%;%CALSAMPLESDIR%
set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/bamSample.jar
set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/calBenchmarks.jar
set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/calBenchmarks_test.jar
set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/calSamples.jar
set JAVACP_PATH=%JAVACP_PATH%;%BINDIR%/calSamples_test.jar

set JAVACP_PATH=%JAVACP_PATH%;%LIBDIR%/Resources/GemCutterHelpFiles.jar
set JAVACP_PATH=%JAVACP_PATH%;%LIBDIR%/Resources/External/java/asm-all-3.0.jar
set JAVACP_PATH=%JAVACP_PATH%;%LIBDIR%/Resources/External/java/commons-collections-3.1.jar
set JAVACP_PATH=%JAVACP_PATH%;%LIBDIR%/Resources/External/java/icu4j.jar
set JAVACP_PATH=%JAVACP_PATH%;%LIBDIR%/Resources/External/java/junit.jar
set JAVACP_PATH=%JAVACP_PATH%;%LIBDIR%/Resources/External/java/log4j.jar

set JAVACP_PATH=%JAVACP_PATH%;%LIBDIR%/Resources/External/java/xercesImpl.jar
set JAVACP_PATH=%JAVACP_PATH%;%LIBDIR%/Resources/External/java/xmlParserAPIs.jar

set JAVACP_PATH=%JAVACP_PATH%;%LIBDIR%/Resources/External/java/antlr.jar
set JAVACP_PATH=%JAVACP_PATH%;%LIBDIR%/Resources/External/Sun/JavaHelp/2.0_02/jh.jar


