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

* `contributions/` - A directory containing a history of all changes
  to the dataset

* `lib/` - support files for maintaining the dataset

* `Makefile` - The makefile that automates maintaining the
  dataset. Running `make` on its own will generate the following:

  - `dataset.ttl`, `dataset.nt`, `dataset.csv`, `dataset.json` - The
    latest version of the dataset in Turtle, N-Triples, CSV, and JSON
    formats

  - `updates/` - A directory containing all changes to the dataset in
    Turtle or SPARQL UPDATE format

  - `versions/` - A directory containing each version of the full
    dataset in Turtle format

  - `diffs/` - A directory containing, for each version of the
    dataset, the changes from (triples added to and removed from) the
    previous version


## Creating a new version of the dataset

New versions of the dataset are generated from files in the
`contributions/` directory. To generate a new version, do the
following:

1. Create a new directory under the `contributions/` directory. The
   directory should be named according to the following pattern:

    `[date]-[tag]-[ADD | REPLACE | UPDATE]`
    
    `date` should be the date of the version in `YYYY-MM-DD` format.
    
    `tag` should be a short hyphenated description of the changes,
    e.g. `city-founding-dates`.
    
    `ADD` means the changes only add new data. 
    
    `REPLACE` means that the changes should replace existing
    data. (More specifically: `REPLACE` means that if a triple `[ s p
    o ]` is among the changes, then any existing triples that share
    the same subject `s` and predicate `p` will be removed before that
    triple is added.)
    
    `UPDATE` means that the changes are the result of a [SPARQL
    UPDATE](https://www.w3.org/TR/sparql11-update/) operation, which could remove and add arbitrary
    triples.
    
    For example, a set of changes added to the dataset on June 4, 2021
    that add founding dates to cities in the gazetteer might be named
    `2021-06-04-city-founding-dates-ADD`.

1. In this new directory, create a `metadata.json` file that describes
   the changes and who made them, e.g.:
   
   ```json
   {
     "description": "Adds founding dates for 72 cities",
     "authors": [
       "Ryan Shaw"
     ]
   }
   ```

1. If the new contribution is an `ADD` or `REPLACE`, the new data can
   be provided as a Turtle file or as a CSV file.
   
    1. Put the Turtle or CSV file in the same directory as the
       `metadata.json` file. Name it `data.ttl` or `data.csv`. For
       example:

       `contributions/2021-06-04-city-founding-dates-ADD/data.csv`
      
    1. *If* the data file is in CSV format, you must specify how to
       convert it to RDF. In the same directory as the `data.csv` and
       `metadata.json` files, create a file named `construct.rq` with a
       SPARQL CONSTRUCT query for creating RDF from the `data.csv`
       file. See [`contributions/2020-01-22-ncpedia-ADD/construct.rq`](contributions/2020-01-22-ncpedia-ADD/construct.rq)
       for a relatively simple example. Note that these queries are run
       using [TARQL](https://tarql.github.io); see the TARQL documentation for more details
       about how to write SPARQL queries that construct RDF from CSV.
   
       In some cases it may not be possible to construct the RDF you want
       with a single query. In that case, in addition to the initial TARQL
       query you can create additional (regular SPARQL) CONSTRUCT queries
       named `construct1.rq`, `construct2.rq`, etc. Each subsequent query
       will be run on the results of the previous one.

    1. *If* the data file is in CSV format, there may be additional
       CSV files as well. These should be placed in the same directory
       as the `data.csv` file. Their names do not matter but they
       should have the suffix `.csv`. Column names should be unique
       across all the CSV files in a contribution.

1. If the new contribution is an `UPDATE`, instead of a data file you
   should put a file named `update.ru` in the same directory as the
   `metadata.json` file. This file should contain a single SPARQL
   UPDATE operation to be executed on the previous version of the
   dataset.


## Publishing to the web

`make upload` will upload the latest dataset versions to
<https://ncgazetteer.org>.

## TODO

* Validate shapes after compiling the dataset
