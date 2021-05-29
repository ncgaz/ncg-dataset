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

UPDATES := $(shell ls contributions | awk '{print "updates/" $$0 ".ttl"}')

UPLOAD_WORKING_DIR := ncg

UPLOAD_FILES = dataset.ttl \
	       dataset.csv \
	       types.ttl \
	       vocab.ttl

.PHONY: all
all: dataset.ttl dataset.csv types.ttl

.PHONY: upload
upload:
	mkdir -p $(UPLOAD_WORKING_DIR)
	rsync -av --progress $(UPLOAD_FILES) ncgazetteer.org:/var/www/ncgazetteer.org/ncg
	rm -rf $(UPLOAD_WORKING_DIR)

.PHONY: clean
clean:
	./lib/gradlew -q -p lib clean
	rm -rf updates diffs dataset.ttl dataset.csv types.ttl \
	$(UPLOAD_WORKING_DIR)

.PHONY: superclean
superclean: clean
	rm -rf tools

$(TARQL):
	mkdir -p tools
	curl -L $(TARQL_DL)/v$(TARQL_VERSION)/$(TARQL_PATH).tar.gz \
	| tar xvf - -C tools

$(ARQ):
	mkdir -p tools
	curl -L $(JENA_DL)/$(JENA_PATH).tar.gz \
	| tar xvf - -C tools

$(SPARQL_FUNC_LIB):
	./lib/gradlew -q -p lib :sparql-functions:build

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

dataset.ttl: $(UPDATES)
	./lib/gradlew -q -p lib \
	:compile-dataset:run --args "updates diffs versions" \
	> $@

types.ttl: dataset.ttl queries/types.rq
	JENA_HOME=tools/$(JENA_PATH) \
	$(ARQ) --data=$< --query=$(word 2,$^) --results=TTL \
	> $@

dataset.csv: dataset.ttl queries/csv.rq
	JENA_HOME=tools/$(JENA_PATH) \
	$(ARQ) --data=$< --query=$(word 2,$^) --results=CSV \
	> $@
