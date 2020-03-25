VENV := ./venv
VENV_BIN := $(VENV)/bin
PY3 := $(VENV_BIN)/python3

VERSIONS := $(shell jq -r '.[].filename' versions.json)
VERSION_DIRS = $(addprefix diffs/,$(notdir $(VERSIONS)))
VERSION_DEPS := $(addsuffix /Makefile,$(VERSION_DIRS))
VERSION_DATASETS := $(addsuffix /normalized.json,$(VERSION_DIRS))
VERSION_PATCHES := $(addsuffix /patch.jsonpatch,$(VERSION_DIRS))

.PHONY: all
all: dataset.json dataset.ttl dataset.csv types.ttl $(VERSION_PATCHES)

clean:
	rm -rf diffs dataset.json dataset.ttl dataset.csv types.ttl

dataset.json: $(VERSION_DATASETS)
	cp $(lastword $^) $@

dataset.ttl: dataset.json
	$(PY3) src/convert.py -i jsonld -o turtle $< > $@

dataset.csv: dataset.json
	$(PY3) src/convert.py -i jsonld -o csv $< > $@

types.ttl: dataset.ttl
	arq --data=dataset.ttl --query=./queries/nctypes.sparql > $@
	rapper -q -i turtle -o turtle $@ | sponge $@

diffs/%/Makefile: versions/%
	mkdir -p $(dir $@)
	./generate_dependency.py $< > $@

ifneq ($(MAKECMDGOALS),clean)
include $(VERSION_DEPS)
endif
