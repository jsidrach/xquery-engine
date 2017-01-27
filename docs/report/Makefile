TEXC := xelatex
TEXC_OPTS += -shell-escape
CLEAN = *.aux *.log *.nav *.out *.snm *.toc *.vrb latexmkrc

.PHONY: presentation

all: presentation clean

presentation:
	$(TEXC) $(TEXC_OPTS) presentation.tex
	$(TEXC) $(TEXC_OPTS) presentation.tex

clean:
	rm -f $(CLEAN)
