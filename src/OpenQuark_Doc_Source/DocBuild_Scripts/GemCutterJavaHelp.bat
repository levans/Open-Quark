@echo off
setlocal

@echo.
@echo Generating: GemCutterHelpFiles.jar
@echo.

set javahelp_tempdir=%javahelp_output_path%\JavaHelp

md "%javahelp_tempdir%\images\GemCutterManual"
md "%javahelp_tempdir%\images\UsingJFitInGemCutter"

pushd %javahelp_tempdir%


set runme=java
set runme=%runme% -Djava.endorsed.dirs=%xalandir%
set runme=%runme% -cp %xalancp%;%resolvercp%
set runme=%runme% org.apache.xalan.xslt.Process
set runme=%runme% %resolverXalanArgs%
set runme=%runme% -in "%xml_path%\GemCutterManual.xml"
set runme=%runme% -xsl "%xsl_javahelp_path%\javahelp-docbook.xsl"

@rem Have to hack or else output goes into %xml_path%
set runme=%runme% -out %javahelp_tempdir%\delete.me
%runme%
del %javahelp_tempdir%\delete.me


ren jhelpset.hs gemcutterhelp.hs

call "%jhindexer_path%\jhindexer" *

copy /Y "%css_path%\*"           .
copy /Y "%manual_images_path%\*" "images\GemCutterManual\"
copy /Y "%jfit_images_path%\*"   "images\UsingJFitInGemCutter\"

popd

start /b /wait jar -cf %javahelp_output_path%\GemCutterHelpFiles.jar -C %javahelp_output_path% JavaHelp\

rd /S /Q %javahelp_tempdir%

endlocal
