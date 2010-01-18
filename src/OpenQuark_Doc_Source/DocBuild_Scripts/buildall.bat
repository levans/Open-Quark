@echo off
setlocal

@rem Note that all environment variables here are slashified using backslashes according to dos convention.
@rem To convert to forward slashes, use char conversion, eg. %varname:\=/%

@rem
@rem Source path root.
@rem A bit of hacking to determine the absolute pathname.
@rem
set build_src_dir=..\..\..\..
pushd %build_src_dir%
set build_src_dir=%cd%
popd


@rem
@rem Output paths.
@rem
set pdf_output_path=%build_src_dir%\Main\Research\OpenQuark\docs
set javahelp_output_path=%build_src_dir%\Main\Research\Quark_Gems\Help
set eclipsehelp_output_path=%cd%\CAL_Eclipse_Help


@rem
@rem Source paths.
@rem
set docsource_path=%build_src_dir%\Main\Research\OpenQuark_Doc_Source

set xml_path=%docsource_path%\xml
set manual_images_path=%xml_path%\images\GemCutterManual
set jfit_images_path=%xml_path%\images\UsingJFitInGemCutter

set custom_xsl_path=%docsource_path%\xsl\custom
set xsl_fo_path=%custom_xsl_path%\fo
set xsl_javahelp_path=%custom_xsl_path%\javahelp
set xsl_eclipse_path=%custom_xsl_path%\eclipse

set css_path=%docsource_path%\css


@rem
@rem Paths to tools.
@rem
set external_dir=%build_src_dir%\Main\PI\External
set fop_path=%external_dir%\Apache\java\fop\0.93\fop-0.93
set FOP_HYPHENATION_PATH=%external_dir%\SourceForge\OFFO\offo-hyphenation\1.0\fop-hyph.jar
set jhindexer_path=%external_dir%\Sun\JavaHelp\2.0_02\jh2.0\javahelp\bin
set docbook_xsl_path=%external_dir%\SourceForge\DocBook\docbook-xsl\1.71.1\docbook-xsl-1.71.1
call :subConfigureXalan
call :subConfigureResolver


@rem
@rem Build the outputs.
@rem
call buildCALEclipseHelp.bat
@rem call EffectiveCALPDF.bat
call GemCutterPDF.bat
call GemCutterJavaHelp.bat
call JFitPDF.bat
call GeneratingDocBookDocumentationPDF.bat
call CALForHaskellProgrammersPDF.bat
call UsingCALWithEclipsePDF.bat
call CALUsersGuidePDF.bat
call UsingQuarkWithStandaloneJARs.bat

goto :end

@rem
@rem @@@ END @@@
@rem 



:subConfigureResolver
@rem subroutine to set:
@rem   resolvercp - resolver classpath
@rem   resolverXalanArgs - args to Xalan to configure the resolver

set resolvercp=%external_dir%\Apache\java\xerces\xerces-2_6_2\resolver.jar;%cd%
set resolvercp=%resolvercp:\=/%

set resolverXalanArgs=-ENTITYRESOLVER org.apache.xml.resolver.tools.CatalogResolver -URIRESOLVER org.apache.xml.resolver.tools.CatalogResolver

goto :eof


:subConfigureXalan
@rem subroutine to set:
@rem    xalancp - xalan classpath (plus xerces, etc.)
@rem    xalandir - folder containing the .jars in xalancp

set xalandir=%fop_path%\lib

set xalancp=%xalandir%\xalan-2.7.0.jar;%xalandir%\xercesImpl-2.7.1.jar;%xalandir%\xml-apis-1.3.02.jar
set xalancp=%xalancp%;%docbook_xsl_path%\extensions\xalan27\dist\xalan27.jar
set xalancp=%xalancp:\=/%

goto :eof




:end
endlocal
