TEX=$(which latex)
MKINDEX=$(which makeindex)
BIB=$(which biber)
DOXY=$(which doxygen)
RM="/bin/rm"
MV="/bin/mv"
CP="/bin/cp"
MAKE="/usr/bin/make"

all: pdf

docs: clean
	@doxygen
	echo "# This file is to instruct Jekyll to serve pages with file names beginning with the underscore ('_') character." > docs/.nojekyll

pdf: docs
	$(MV) docs/latex docs/tex
#	$(CP) TeXmakefile docs/tex/Makefile
	$(MAKE) -C docs/tex pdf
	$(MV) docs/tex/refman.pdf docs/tex/manual.pdf

clean:
	$(RM) -rf docs
