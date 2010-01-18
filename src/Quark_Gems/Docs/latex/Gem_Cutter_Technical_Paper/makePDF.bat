rem building the Gem Cutter tech paper from TeX sources
setlocal
set texpath="D:\MiKTeX 2.5\miktex\bin"
%texpath%\pdflatex gemcutter-techpaper
%texpath%\bibtex gemcutter-techpaper
%texpath%\pdflatex gemcutter-techpaper
%texpath%\pdflatex gemcutter-techpaper
endlocal
pause