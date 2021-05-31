OS := $(shell uname -s)

SPARQL_FUNC_LIB := lib/sparql-functions/build/libs/sparql-functions.jar

TARQL_DL := https://github.com/tarql/tarql/releases/download
TARQL_VERSION := 1.2
TARQL_PATH := tarql-$(TARQL_VERSION)
TARQL := tools/$(TARQL_PATH)/bin/tarql

JENA_DL := https://downloads.apache.org/jena/binaries
JENA_VERSION := 4.0.0
JENA_PATH := apache-jena-$(JENA_VERSION)
ARQ := tools/$(JENA_PATH)/bin/arq
RIOT := tools/$(JENA_PATH)/bin/riot

UPDATES := \
	$(shell ls -d contributions/*-{ADD,REPLACE} \
	| awk '{sub(/contributions/, "updates"); print $$0 ".ttl"}') \
	$(shell ls -d contributions/*-UPDATE 2> /dev/null | \
	awk '{sub(/contributions/, "updates"); print $$0 ".ru"}')

METADATA := $(shell ls contributions | awk '{print "contributions/" $$0 "/metadata.json"}')

UPLOAD_WORKING_DIR := ncg

UPLOAD_FILES = dataset.nt \
	       dataset.ttl \
	       dataset.csv \
	       types.ttl \
	       vocab.ttl

.PHONY: all
all: dataset.nt dataset.ttl dataset.csv types.ttl

.PHONY: upload
upload:
	mkdir -p $(UPLOAD_WORKING_DIR)
	rsync -av --progress $(UPLOAD_FILES) ncgazetteer.org:/var/www/ncgazetteer.org/ncg
	rm -rf $(UPLOAD_WORKING_DIR)

.PHONY: clean
clean:
	./lib/gradlew -q -p lib clean
	rm -rf updates diffs dataset.* types.ttl \
	$(UPLOAD_WORKING_DIR)

.PHONY: superclean
superclean: clean
	rm -rf tools

.PHONY: check_metadata
check_metadata:
	$(foreach m,$(METADATA),\
	$(if $(shell test -f $(m) && echo -n ok),,$(error Missing $(m))))

$(TARQL):
	mkdir -p tools
	curl -L $(TARQL_DL)/v$(TARQL_VERSION)/$(TARQL_PATH).tar.gz \
	| tar zxvf - -C tools

$(ARQ):
	mkdir -p tools
	curl -L $(JENA_DL)/$(JENA_PATH).tar.gz \
	| tar zxvf - -C tools

$(SPARQL_FUNC_LIB):
	./lib/gradlew -q -p lib :sparql-functions:build

# sparql updates
updates/%.ru: contributions/%/update.ru
	cp -f $< $@

# ttl updates
updates/%.ttl: contributions/%/data.ttl
	cp -f $< $@

# csv updates
updates/%.ttl: \
contributions/%/construct.rq \
contributions/%/data.csv \
$(SPARQL_FUNC_LIB) \
| $(TARQL) $(ARQ)
	mkdir -p updates
	JENA_HOME=tools/$(JENA_PATH) \
	CLASSPATH_PREFIX=$(SPARQL_FUNC_LIB) \
	./lib/construct-update.sh contributions/$* $(TARQL) $(ARQ) \
	> $@

dataset.nt: $(UPDATES) check_metadata
	./lib/gradlew -q -p lib \
	:compile-dataset:run --args "updates diffs versions" \
	| sort > $@

dataset.ttl: dataset.nt
	JENA_HOME=tools/$(JENA_PATH) \
	$(RIOT) --formatted=TTL $< \
	> $@

dataset.csv: dataset.nt queries/csv.rq
	JENA_HOME=tools/$(JENA_PATH) \
	$(ARQ) --data=$< --query=$(word 2,$^) --results=CSV \
	> $@

types.ttl: dataset.nt queries/types.rq
	JENA_HOME=tools/$(JENA_PATH) \
	$(ARQ) --data=$< --query=$(word 2,$^) --results=TTL \
	> $@
