#!/usr/bin/env bash
set -eo pipefail

# Create a new, empty contribution directory.

read -r -p 'Your name: ' author
read -r -p 'Description of contribution: ' description

date=$(date "+%Y-%m-%d")

read -r -p 'Tag (e.g. city founding dates): ' input
lowercase_input="${input,,}"
tag="${lowercase_input/ /-}"

read -r -p 'Operation (add, replace, update): ' op
if [[ a = $op* ]]; then
    operation=ADD
elif [[ r = $op* ]]; then
    operation=REPLACE
else
    operation=UPDATE
fi

dir="contributions/${date}-${tag}-${operation}"
mkdir -p "$dir"

jq -n \
    --arg d "$description" \
    --arg a "$author" \
    '{description: $d, authors: $a}' > "${dir}/metadata.json"

if [ $operation = UPDATE ]; then
    touch "${dir}/update.ru"
else
    read -r -p 'Data format (csv, ttl): ' f
    if [[ c = $f* ]]; then
        format=csv
    else
        format=ttl
    fi
    touch "${dir}/data.${format}"
fi

tree "$dir"
