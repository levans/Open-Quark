TEX_FILE_NO_EXT=gemcutter-techpaper

all:
	pdflatex $(TEX_FILE_NO_EXT)
	bibtex $(TEX_FILE_NO_EXT)
	pdflatex $(TEX_FILE_NO_EXT)
	pdflatex $(TEX_FILE_NO_EXT)

clean:
	rm $(TEX_FILE_NO_EXT).bbl
	rm $(TEX_FILE_NO_EXT).blg
	rm $(TEX_FILE_NO_EXT).aux
	rm $(TEX_FILE_NO_EXT).log
	rm $(TEX_FILE_NO_EXT).pdf

# end of Makefile
