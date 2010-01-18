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

@echo off
setlocal

@rem Delegate to set-javacp-base.bat for setting up the basic classpath (without adding in either CAL resources or Car-jars)
call set-javacp-base

@rem Do the same with set-javacp-extended.bat if it exists.
if exist set-javacp-extended.bat (
call set-javacp-extended
)

@rem Uncomment the following line to run with Car-jars.
@rem set carjar=foo

@rem If we are to run with Car-jars, delegate to set-javacp-carjar.bat for adding the Car-jars to the classpath.
@rem Otherwise, add an entry for the debug folder, in order to make the test CAL scripts available.

if defined carjar (
call set-javacp-carjar
) else (
set JAVACP_PATH=bin/cal/release;bin/cal/debug;%JAVACP_PATH%
)

if not defined QUARK_JAVACMD (
set QUARK_JAVACMD=java
)

@echo on
@%QUARK_JAVACMD% -Xmx256m %QUARK_VMARGS% -cp %JAVACP_PATH% %*

@popd
@endlocal