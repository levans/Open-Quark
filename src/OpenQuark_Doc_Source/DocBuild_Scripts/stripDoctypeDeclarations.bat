@rem This script removes extra doctype declarations from the html and xml files
@rem produced by the docbook stylesheet transform for Eclipse help.

@rem The only way to specify a doctype when chunking in Xalan is to set it on the 
@rem stylesheet output (top-level) element.  This sets the doctype for all output
@rem meaning, both html and xml files.

@rem Issue 1: Xalan has a bug in the chunker where the declaration is duplicated
@rem  http://issues.apache.org/bugzilla/show_bug.cgi?id=23817
@rem  http://issues.apache.org/jira/browse/XALANJ-1685

@rem Issue 2: the xml files should not have any doctype declaration.

@echo off

time /t
pushd CAL_Eclipse_Help


@rem Determine whether sed is on the path
@rem If not, print an error message and exit.

@rem Invocation of sed which hopefully does nothing.
sed -e '' nul

if %errorlevel% GTR 0 (
  echo.
  echo ERROR: this script requires sed ^(cygwin^) to be installed.
  goto :end
)


@rem Try to analyze whether this script has already been run.
@rem This analyzes the first token on each line of plugin.xml, seeing if it is the token <!DOCTYPE
@rem If it finds such a token, then it assumes the script has not been run.
@rem This is obvious a bit of a hack.

for /F "delims= " %%i in (plugin.xml) do (
  if %%i==^<!DOCTYPE (
    goto :notYetRun
  )
)
@rem Haven't found the doctype declaration.  Must have already been run.
goto :alreadyRun

:notYetRun

@rem delete first line of all .html files
@rem fix for a bug in Xalan where two doctype declarations are output
pushd content

for /R %%I in (.) do (
  pushd %%~fI
  
  @rem echo the current folder.
  cd

  for %%J in (*.html) do (
    @rem rename the file to (filename).bak
    mv %%J %%J.bak
    
    @rem create a copy of the file to (filename) with the first line removed.
    sed 1d %%J.bak > %%J
  )
  popd
)

popd

@rem delete first two lines of the .xml files.
@rem these will also get the doctype declarations.
for %%J in (plugin.xml,toc.xml) do (
  @rem rename the file to (filename).bak
  mv %%J %%J.bak
  
  @rem create a copy of the file to (filename) with the first line removed.
  sed '1,2 d' %%J.bak > %%J
)

@rem delete old files..
del /s *.html.bak > nul
del plugin.xml.bak > nul
del toc.xml.bak > nul

goto :end

:alreadyRun
@echo This script has already been run!
@echo Not doing anything.


:end
popd
time /t
