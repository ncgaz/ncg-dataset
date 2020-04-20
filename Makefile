VENV := ./venv
VENV_BIN := $(VENV)/bin
PY3 := $(VENV_BIN)/python3

VERSIONS := $(shell jq -r '.[].filename' versions.json)
VERSION_DIRS = $(addprefix diffs/,$(notdir $(VERSIONS)))
VERSION_DEPS := $(addsuffix /Makefile,$(VERSION_DIRS))
VERSION_DATASETS := $(addsuffix /normalized.json,$(VERSION_DIRS))
VERSION_PATCHES := $(addsuffix /patch.jsonpatch,$(VERSION_DIRS))

UPLOAD_WORKING_DIR := ncg

UPLOAD_FILES = dataset.json \
	       dataset.ttl \
	       dataset.csv \
	       types.ttl \
	       vocab.ttl

.PHONY: all
all: venv dataset.json dataset.ttl dataset.csv types.ttl $(VERSION_PATCHES)

.PHONY: reconcile
reconcile: dataset.csv
	java -Xmx2g -jar reconcile-csv-0.1.2.jar $< label id

.PHONY: upload
upload:
	mkdir -p $(UPLOAD_WORKING_DIR)
	rsync -av --progress $(UPLOAD_FILES) ncgazetteer.org:/var/www/ncgazetteer.org/ncg
	rm -rf $(UPLOAD_WORKING_DIR)

.PHONY: clean
clean:
	rm -rf diffs dataset.json dataset.ttl dataset.csv types.ttl $(UPLOAD_WORKING_DIR)

.PHONY: test
test:
	$(PY3) lib/test.py

venv:
	python3 -m venv venv
	./venv/bin/pip install jsonpointer

dataset.json: $(VERSION_DATASETS)
	cp $(lastword $^) $@

dataset.ttl: dataset.json
	$(PY3) lib/convert.py -i jsonld -o turtle $< > $@

dataset.csv: dataset.json
	$(PY3) lib/convert.py -i jsonld -o csv $< > $@

types.ttl: dataset.ttl
	arq --data=dataset.ttl --query=./queries/nctypes.sparql > $@
	rapper -q -i turtle -o turtle $@ > $@.tmp
	rm $@ && mv $@.tmp $@

diffs/%/Makefile: versions/%
	mkdir -p $(dir $@)
	./generate_dependency.py $< > $@

ifneq ($(MAKECMDGOALS),clean)
include $(VERSION_DEPS)
endif
