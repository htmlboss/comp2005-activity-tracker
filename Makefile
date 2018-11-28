RM="/bin/rm"
MV="/bin/mv"
CP="/bin/cp"
MAKE="$(which make)"
SED="/bin/sed"

all: pdf

docs: clean
	@doxygen
	echo "# This file is to instruct Jekyll to serve pages with file names beginning with the underscore ('_') character." > docs/.nojekyll

pdf: docs
	@mv docs/latex docs/tex
	@cp gui/logo.eps docs/tex/
	@sed -i -e 's/a4paper/letterpaper/g' docs/tex/refman.tex
	@sed -i -e 's/\\documentclass\[twoside\]/\\documentclass\[12pt, twoside\]/g' docs/tex/refman.tex
	@sed -i -e 's/\\vspace\*{7cm}/\\vspace\*{3cm}/g' docs/tex/refman.tex
	@sed -i -e 's/\\usepackage\[scaled=\.90\]{helvet}/\\usepackage{mathptmx}/g' docs/tex/refman.tex
	@sed -i -e 's/\\usepackage{courier}/\\usepackage{sourcecodepro}/g' docs/tex/refman.tex
	@sed -i -e 's/\\renewcommand{\\familydefault}{\\sfdefault}/%\\renewcommand{\\familydefault}{\\sfdefault}/g' docs/tex/refman.tex
	@sed -i -e 's/Large Activity Logger/includegraphics[width=3in]{logo}\\par\\vskip 1cm\\Huge Activity Tracker/g' docs/tex/refman.tex
	@make -C docs/tex pdf
	@mv docs/tex/refman.pdf docs/tex/manual.pdf

clean:
	@rm -rf docs
