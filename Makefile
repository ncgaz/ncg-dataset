VENV := ./venv
VENV_BIN := $(VENV)/bin
PY3 := $(VENV_BIN)/python3

VERSIONS := $(shell find ./versions -type f | sort -V)
VERSION_DIFFS := $(addprefix diffs/,$(basename $(notdir $(VERSIONS))))

TMP_DATASET := diffs/dataset.tmp.json

.PHONY: all
all: dataset.json

clean:
	rm -rf $(TMP_DATASET) diffs

diffs/%: versions/%.csv
	@echo Building $@ from $<
	@mkdir -p $@
	@echo {} > $(TMP_DATASET)
	@DIFFS=`echo $(VERSION_DIFFS) | tr ' ' '\n' | sed -e '\|$@|Q'` && \
		for d in $$DIFFS ; do \
			$(VENV_BIN)/jsonpatch -i $(TMP_DATASET) $$d/patch.jsonpatch ; \
		done
	$(PY3) versioner.py $< -f csv -s $(TMP_DATASET) -o $@ > /dev/null
	rm $(TMP_DATASET)
	@touch $@

dataset.json: $(VERSIONS) $(VERSION_DIFFS)
	echo {} > $@
	for d in $(VERSION_DIFFS) ; do \
		$(VENV_BIN)/jsonpatch -i $@ $$d/patch.jsonpatch ; \
	done
