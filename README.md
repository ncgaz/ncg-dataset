# North Carolina Gazetteer - Dataset

This contains all the data files and scripts required to maintain the [North Carolina Gazetteer](https://ncgazetteer.org) dataset

# Requirements

To run the various programs, you will need the following installed and available in your `$PATH`:

  * Python3 (3.7+)

  * jq

  * GNU Make

  * Redland RDF utils

  * Apache Jena (`arq`)

# Contents

* `versions/` - A directory containing a history of all the files and all the edits to the dataset

* `versions.json` - A listing of all the versions in `versions/`, listed in order with some descriptive information

* `lib/` - Python files for maintaining the dataset

  - `lib/convert.py` is a CLI program for converting the dataset between various data formats

  - `lib/diff.py` is a CLI program for generating differences between two versions of a dataset, represented by a JSON Patch patch between two canonical forms of a dataset.

* `Makefile` - The makefile that automates maintaining the dataset. Running `make` on its own will generate the following:

  - `dataset.json`, `dataset.ttl`, `dataset.csv` - The latest version of the dataset in a variety of formats

  - `diffs/` - A folder containing, for each version, the canonicalized dataset for that version, and a JSON Patch patch representing the difference from the previous version

The makefile also contains a couple other commands:

* `make upload` - Upload the latest dataset versions to <https://ncgazetteer.org>

* `make reconcile` - Set up an OpenRefine reconciliation server to reconcile against labels in the dataset, using OKFN's [reconcile-csv](https://github.com/okfn/reconcile-csv) program

* `make test` - Run the tests from `lib/test.py`

# Adding a new version

To add a new version, do the following:

  1. Copy the data file of the new version to the `versions/` directory. The format of the dataset will be guessed from the extension. Currently, only CSV and JSON files can represent new versions of the dataset.

  2. Add an entry to the end of `versions.json` with the following fields:

    - `filename` - The name of the file you added to `versions/`

    - `date` - The ISO 8601 date associated with the new version

    - `description` - A textual description of the changes contained in the new version

    - `authors` - An array of strings representing the author(s) of the new version

    - `sparse` (optional) - A boolean indicating whether this version contains *every* entry of the gazetteer, or only a subset. If sparse is **true**, only records contained in this version will be considered for generating diffs between versions. (If you do not mark a version as sparse, and it only contains a subset of entries, then the missing entries will be assumed to be deleted).

    - `fields` (optional) - An array of fields that should be considered for diffing. Without this field, **all** fields will be included in the diff.
