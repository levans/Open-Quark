@echo off

@rem
@rem We have to jump through hoops to get fop to use the resolver.
@rem

@echo.
@echo Generating Eclipse Help
@echo.

md %eclipsehelp_output_path%

pushd %javahelp_tempdir%

@rem use xalan to generate html output.
set runme=java
set runme=%runme% -Djava.endorsed.dirs=%xalandir%
set runme=%runme% -Dorg.apache.xerces.xni.parser.XMLParserConfiguration=org.apache.xerces.parsers.XIncludeParserConfiguration
set runme=%runme% -cp %xalancp%;%resolvercp%
set runme=%runme% org.apache.xalan.xslt.Process
set runme=%runme% %resolverXalanArgs%
set runme=%runme% -in "%xml_path%\EclipseHelpSet.xml"
set runme=%runme% -xsl "%xsl_eclipse_path%\eclipse-docbook.xsl"

@rem Have to hack or else output goes into %xml_path%
set runme=%runme% -out %eclipsehelp_output_path%\delete.me
%runme%
del %eclipsehelp_output_path%\delete.me


:end
endlocal

