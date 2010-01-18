@echo off

@rem
@rem We have to jump through hoops to get fop to use the resolver.
@rem

@echo.
@echo Generating: %2
@echo.

@rem This script assumes all environment vars have been set (eg. according to buildall.bat)

@rem %~n1 treats %1 as a file name, and chops off the extension.

rem %1 - DocBook xml simple file name.
rem %2 - Output file simple name.
rem %3 - Simple stylesheet name.

setlocal 

set fo_file="%pdf_output_path%\%~n1.fo"

@rem use xalan to generate intermediate fo output.
set runme=java
set runme=%runme% -Djava.endorsed.dirs=%xalandir%
set runme=%runme% -cp %xalancp%;%resolvercp%
set runme=%runme% org.apache.xalan.xslt.Process
set runme=%runme% %resolverXalanArgs%
set runme=%runme% -in "%xml_path%\%~1"
set runme=%runme% -xsl "%xsl_fo_path%\%~3"
set runme=%runme% -out %fo_file%
%runme%

@rem use fop to generate pdf output from fo input.
call %fop_path%\fop.bat -fo %fo_file% -pdf "%pdf_output_path%\%~2"

@rem delete the intermediate fo file.
del %fo_file%


:end
endlocal

