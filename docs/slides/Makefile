TEXC := xelatex
TEXC_OPTS += -shell-escape
CLEAN = *.aux *.log *.nav *.out *.snm *.toc *.vrb latexmkrc

.PHONY: pdf1

all: pdf1 clean

pdf1:
	$(TEXC) $(TEXC_OPTS) Milestone-1.tex
	$(TEXC) $(TEXC_OPTS) Milestone-1.tex

clean:
	rm -f $(CLEAN)
