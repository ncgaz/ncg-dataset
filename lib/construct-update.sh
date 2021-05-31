#!/usr/bin/env bash

# Run a series of CONSTRUCT queries on CSV data.
# The first query must be a TARQL query; subsequent queries are ordinary SPARQL queries.
#
# $1 directory containing CSV data and CONSTRUCT queries (at least one)
# $2 TARQL command to run
# $3 ARQ command to run

DIR=$1
TARQL_CMD=$2
ARQ_CMD=$3

QUERIES=("$DIR"/construct*.rq)
N_QUERIES=${#QUERIES[@]}

if [ "$N_QUERIES" -eq 0 ]; then
    echo "No CONSTRUCT queries found"
    exit 1
fi

run_tarql() {
    $TARQL_CMD -e utf8 --dedup 100 "${QUERIES[0]}" "$DIR/data.csv"
}

run_arq() {
    $ARQ_CMD --data="$DIR/data.ttl" --query="$1" --results=TTL
}

# Filter TARQL-specific namespaces out of output
filter() {
    grep -E -v '@prefix f: +<java:org.ncgazetteer.> .' \
        | grep -E -v '@prefix apf: +<http://jena.apache.org/ARQ/property#> .'
}

if [ "$N_QUERIES" -eq 1 ]; then
    run_tarql | filter
else
    run_tarql | filter > "$DIR/data.ttl"
    COUNT=1
    for q in "${QUERIES[@]:1}"; do
        if [ "$((++COUNT))" -eq "$N_QUERIES" ]; then
            run_arq "$q"
        else
            run_arq "$q" > "$DIR/next-data.ttl"
            mv "$DIR/next-data.ttl" "$DIR/data.ttl"
        fi
    done
fi

rm -f "$DIR"/*data.ttl
