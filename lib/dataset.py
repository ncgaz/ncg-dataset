import copy
import csv
import json
import subprocess
from collections import defaultdict
from io import StringIO
from tempfile import NamedTemporaryFile

from fields import VALID_FIELDS

JSONLD_CHUNK_SIZE = 3000

CONTEXT = {
  "@context": {
    "@base": "http://n2t.net/ark:/39333/ncg/place/",

    "ncgaz": "http://n2t.net/ark:/39333/ncg/",
    "ncp": "ncgaz:place/",
    "nct": "ncgaz:type#",
    "ncv": "ncgaz:vocab#",
    "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
    "skos": "http://www.w3.org/2004/02/skos/core#",

    "id": "@id",
    "records": {
      "@container": "@index",
      "@id": "rdfs:member"
    },
    "label": "skos:label",
    "description": "skos:note",
    "county": "ncv:county",
    "feature_type": {
      "@type": "@id",
      "@id": "@type"
    },

    "wikidata_id": {
        "@type": "@id",
        "@id": "rdfs:seeAlso",
    },

    "viaf_id": {
        "@type": "@id",
        "@id": "rdfs:seeAlso",
    },

    "loc_id": {
        "@type": "@id",
        "@id": "rdfs:seeAlso",
    },

    "worldcat_id": {
        "@type": "@id",
        "@id": "rdfs:seeAlso",
    },
  },
  "id": "ncgaz:dataset"
}

TURTLE_PREAMBLE = """
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix ncgaz: <http://n2t.net/ark:/39333/ncg/> .
@prefix ncp: <http://n2t.net/ark:/39333/ncg/place/> .
@prefix nct: <http://n2t.net/ark:/39333/ncg/type#> .
@prefix ncv: <http://n2t.net/ark:/39333/ncg/vocab#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix skos: <http://www.w3.org/2004/02/skos/core#> .
"""


class Dataset:
    def __init__(self, dataset):
        self.dataset = copy.deepcopy(dataset)

        self._items_by_name = defaultdict(list)
        for record in self.dataset.values():
            if 'label' in record:
                self._items_by_name[record['label']].append(record)

    def get_record(self, record_id):
        return self.dataset.get(record_id)

    def search(self, term, feature_type=None):
        # First search by ID
        matches = self._items_by_name.get(term, [])

        if feature_type:
            matches = [
                m for m in matches
                if m['feature_type'] == feature_type
            ]

        return matches

    def as_jsonld(self):
        ret = copy.deepcopy(CONTEXT)
        ret['records'] = self.dataset
        return ret

    def as_turtle(self):
        return self._as_rdf(True)

    def as_ntriples(self):
        return self._as_rdf(False)

    def _as_rdf(self, turtle=False):
        records = list(self.dataset.values())
        chunked_records = [
            records[i:i + JSONLD_CHUNK_SIZE]
            for i in range(0, len(records), JSONLD_CHUNK_SIZE)
        ]

        with NamedTemporaryFile('a+') as rdf_fp:
            if turtle:
                rdf_fp.write(TURTLE_PREAMBLE)
                rdf_fp.flush()
            for chunk in chunked_records:
                chunked_jsonld = copy.deepcopy(CONTEXT)
                chunked_jsonld['records'] = {
                    record['id']: record
                    for record in chunk
                }
                with NamedTemporaryFile('w+') as fp:
                    json.dump(chunked_jsonld, fp)
                    fp.seek(0)
                    subprocess.run(
                        ['riot', '-syntax=jsonld', fp.name],
                        stdout=rdf_fp
                    )

            rdf_fp.seek(0)
            dialect = 'turtle' if turtle else 'ntriples'
            result = subprocess.run(
                ['rapper', '-i', dialect, '-o', dialect, rdf_fp.name],
                capture_output=True,
                encoding='utf-8')
        return result.stdout

    def as_csv(self):
        records = list(self.dataset.values())
        output = StringIO()
        writer = csv.writer(output, dialect=csv.unix_dialect)
        field_names = [Field.name for Field in VALID_FIELDS]
        writer.writerow(field_names)
        for record in records:
            row = [
                Field(record.get(Field.name, None)).to_csv()
                for Field in VALID_FIELDS
            ]
            writer.writerow(row)
        return output.getvalue().strip().rstrip()
