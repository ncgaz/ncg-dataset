#!/usr/bin/env bash
set -eo pipefail

# Create a new, empty contribution directory.

read -r -e -p 'Your name: ' author
read -r -e -p 'Description of contribution: ' description

today=$(date "+%Y-%m-%d")

read -r -e -p 'Date of contribution: ' -i "$today" date

samedate=$(find contributions -type d -name "$date*" | wc -l)
if ((samedate > 0)); then
    if ((samedate == 1)); then
        old=$(find contributions -type d -name "$date*")
        new="contributions/$date-1-$(echo "$old" | cut -c 26-)"
        mv "$old" "$new"
    fi
    date="$date-$((samedate + 1))"
fi

read -r -e -p 'Tag (e.g. city founding dates): ' input
lowercase_input="${input,,}"
tag="${lowercase_input// /-}"

read -r -e -p 'Operation (add, replace, update): ' op
if [[ $op = a* ]]; then
    operation=ADD
elif [[ $op = r* ]]; then
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
    read -r -e -p 'Data format (csv, ttl): ' f
    if [[ $f = c* ]]; then
        format=csv
        touch "${dir}/construct.rq"
    else
        format=ttl
    fi
    touch "${dir}/data.${format}"
fi

if [ "$(builtin type -P tree)" ]; then
    tree "$dir"
elif [ "$(builtin type -P exa)" ]; then
    exa --tree "$dir"
fi
