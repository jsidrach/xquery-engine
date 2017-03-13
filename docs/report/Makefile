TEXC := xelatex
TEXC_OPTS += -shell-escape
CLEAN = *.aux *.log *.nav *.out *.snm *.toc *.vrb *.bak latexmkrc

.PHONY: report

all: report clean

report:
	$(TEXC) $(TEXC_OPTS) report.tex
	$(TEXC) $(TEXC_OPTS) report.tex

clean:
	rm -f $(CLEAN)
