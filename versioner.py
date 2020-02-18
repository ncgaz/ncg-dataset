#!/usr/bin/env python3

import argparse
import csv
import json
from jsonpointer import JsonPointer
from os import path
from collections import namedtuple


parser = argparse.ArgumentParser(description='Diff the gazetteer dataset')
parser.add_argument('dataset')
parser.add_argument('-f', '--format', dest='input_format')
parser.add_argument('-s', '--source-dataset')
parser.add_argument('-o', '--output-directory')
parser.add_argument('--allow-sparse', default=True)

FieldSpec = namedtuple(
    'FieldSpec',
    ['csv_names', 'required'],
    defaults=[False])


VALID_FIELDS = {
    'id': FieldSpec(
        csv_names=['id', 'ID'],
        required=True
    ),
    'label': FieldSpec(
        csv_names=['label', 'Label'],
        required=True
    ),
    'description': FieldSpec(
        csv_names=['description', 'Description'],
        required=True
    ),
    'feature_type': FieldSpec(
        csv_names=['Feature type', 'feature type'],
    ),
}


def to_canonical(dataset_file, input_format):
    canonical = {}

    if input_format == 'csv':
        with open(dataset_file, newline='') as csvfile:
            reader = csv.DictReader(csvfile)
            for row in reader:
                record = {}
                for field_name, field_spec in VALID_FIELDS.items():
                    val = None
                    for name in field_spec.csv_names:
                        if name in row:
                            val = row[name]
                            break
                    else:
                        if field_spec.required:
                            raise ValueError('Missing field: ', field_name)
                    record[field_name] = val
                canonical[record['id']] = record

    return canonical


def make_patch(new, old):
    patch = []
    for item_id, record in new.items():
        old_record = old.get(item_id, None)

        if old_record is None:
            patch.append({
                "op": "add",
                "path": JsonPointer.from_parts([item_id]).path,
                "value": record
            })
            continue

        if record == old_record:
            continue

        for field_name in VALID_FIELDS:
            old_value = old_record.get(field_name, None)
            new_value = record.get(field_name, None)

            if old_value == new_value:
                continue
            pointer = JsonPointer.from_parts([item_id, field_name])

            if new_value is not None and old_value is None:
                patch.append({
                    "op": "add",
                    "path": pointer.path,
                    "value": new_value
                })
            elif new_value is None and old_value is not None:
                patch.append({
                    "op": "delete",
                    "path": pointer.path,
                })
            else:
                patch.append({
                    "op": "replace",
                    "path": pointer.path,
                    "value": new_value,
                })
    return patch


def diff(dataset, source_dataset, output_directory, input_format,
         allow_sparse):
    as_canonical = to_canonical(dataset, input_format)
    out_dataset = json.loads(json.dumps(as_canonical))

    if source_dataset:
        with open(source_dataset, 'r') as source_fd:
            source = json.load(source_fd)
        if allow_sparse:
            for record_id, record in source.items():
                if record_id not in out_dataset:
                    out_dataset[record_id] = record

        patch = make_patch(out_dataset, source)

    if output_directory and source_dataset:
        with open(path.join(output_directory, 'patch.jsonpatch'), 'w') as fd:
            json.dump(patch, fd)

    print(json.dumps(out_dataset))


if __name__ == '__main__':
    args = parser.parse_args()
    diff(**vars(args))
