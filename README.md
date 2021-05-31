# North Carolina Gazetteer - Dataset

This contains all the data files and scripts required to maintain the
[North Carolina Gazetteer](https://ncgazetteer.org) dataset.

## Requirements

To run the various programs, you will need the following installed and
available in your `$PATH`:

  * GNU Make
  
  * Bash

  * a Java Runtime Environment (version 11 or higher)

## Contents

* `contributions/` - A directory containing a history of all additions
  and changes to the dataset in CSV format

* `lib/` - support files for maintaining the dataset

* `Makefile` - The makefile that automates maintaining the
  dataset. Running `make` on its own will generate the following:

  - `dataset.ttl`, `dataset.csv` - The latest version of the dataset
    in Turtle and CSV formats

  - `updates/` - A directory containing all additions and changes to
    the dataset in Turtle format

  - `versions/` - A directory containing, for each version, the full
    Turtle dataset for that version

  - `diffs/` - A directory containing, for each version, the changes
    from (triples added to and removed from) the previous version

## Adding a new version

New versions are generated from CSV files. To add a new version, do
the following:

1. Create a new directory under the `versions/` directory. The
   directory should be named according to the following pattern:

    `[date]-[tag]-[ADD | REPLACE]`
    
    `date` should be the date of the version in `YYYY-MM-DD` format.
    
    `tag` should be a short hyphenated description of the changes,
    e.g. `city-founding-dates`.
    
    `ADD` means the changes only add new data. `REPLACE` means that
    the changes should replace existing data. (More specifically:
    `REPLACE` means that if a triple `[ s p o ]` is among the changes,
    then any existing triples that share the same subject and
    predicate will be removed before that triple is added.)
    
    For example, a set of changes added to the dataset on June 4, 2021
    that add founding dates to cities in the gazetteer might be named
    `2021-06-04-city-founding-dates-ADD`.

1. Put the CSV file under this new directory as `data.csv`, e.g.:

    `contributions/2021-06-04-city-founding-dates-ADD/data.csv`

1. In the same directory as the `data.csv` file, create a
   `metadata.json` file that describes the changes and who made them,
   e.g.:
   
   ```json
   {
     "description": "Adds founding dates for 72 cities",
     "authors": [
       "Ryan Shaw"
     ]
   }
   ```

1. In the same directory as the `data.csv` and `metadata.json` files,
   create a file named `construct.rq` with a SPARQL CONSTRUCT query
   for creating RDF from the `data.csv` file. See
   [`contributions/2020-01-22-ncpedia-ADD/construct.rq`](contributions/2020-01-22-ncpedia-ADD/construct.rq)
   for a relatively simple example. Note that these queries are run
   using [TARQL](https://tarql.github.io); see the TARQL documentation
   for more details about how to write SPARQL queries that construct
   RDF from CSV.
   
   In some cases it may not be possible to construct the RDF you want
   with a single query. In that case, you can create additional
   CONSTRUCT queries names `construct1.rq`, `construct2.rq`, etc. Each
   subsequent query will be run on the results of the previous one.

## Publishing to the web

`make upload` will upload the latest dataset versions to <https://ncgazetteer.org>.

## TODO

* Allow contributions to be added directly in Turtle format without
  the need for a `construct.rq` file.
