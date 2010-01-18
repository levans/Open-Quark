@if exist javacp.bat (
 @call javacp org.openquark.samples.bam.MonitorApp %1 %2 %3 %4 %5 %6 %7 %8 %9
 @goto :EOF
)

@if exist ..\..\javacp.bat (
 @rem popd doesn't work after the "call"
 @cd ..\.. 
 @call javacp org.openquark.samples.bam.MonitorApp %1 %2 %3 %4 %5 %6 %7 %8 %9
 @cd samples/bam
 @goto :EOF
)

@echo Could not find javacp.bat
