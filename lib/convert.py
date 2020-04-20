#!/usr/bin/env python3

import argparse
import csv
import json
import os

from dataset import Dataset
from fields import VALID_FIELDS


parser = argparse.ArgumentParser(
    description='Convert the gazetteer between different formats')

parser.add_argument('dataset')

parser.add_argument('-i', '--input-format', required=False, choices=[
    'csv',
    'jsonld',
    'rdf',
])

parser.add_argument('-o', '--output-format', required=False, choices=[
    'csv',
    'jsonld',
    'turtle',
    'ntriples',
])

parser.add_argument('-s', '--source-dataset')

parser.add_argument('-f', '--fields')


def get_id(record):
    if 'id' in record:
        return record['id']
    elif 'ID' in record:
        return record['ID']
    else:
        raise ValueError('Record has no ID')


def fill_sparse(target_dataset, source_dataset):
    for record_id, record in source_dataset.items():
        if record_id not in target_dataset:
            target_dataset[record_id] = record


def to_canonical(dataset_fp, source_dataset, only_fields, input_format):
    canonical = {}

    if input_format == 'csv':
        reader = csv.DictReader(dataset_fp)
        records = [row for row in reader]

        for record in records:
            canonical[get_id(record)] = {}

        check_fields = False

        # If there is a source dataset, that means that this is a sparse
        # conversion, i.e. it's not starting from an empty state
        if source_dataset:
            if only_fields is not None:
                check_fields = True

            for record_id, record in source_dataset.items():
                # Start from the previous record if this is a sparse
                # conversion, or if we are only changing some fields (which is
                # a special kind of sparse conversion).
                include_previous_record = (
                    check_fields or
                    record_id not in canonical
                )
                if include_previous_record:
                    canonical[record_id] = record

        # Need to iterate fields first instead of records because some fields
        # later on depend on previous fields having been filled in. For
        # example, the `county` field depends on the `label` and `feature_type`
        # fields to already have been filled in.
        for Field in VALID_FIELDS:
            dataset = Dataset(canonical)
            for record in records:
                # If we are only checking certain fields, and this is one of
                # them...
                if check_fields and Field.name in only_fields:
                    field = Field.from_csv(record, dataset)

                    # Delete the value if it is not present (it could be the
                    # case that it was removed compared to the last version
                    if field.value is None:
                        canonical[get_id(record)].pop(field.name, None)

                    # Or else set the new value
                    else:
                        canonical[get_id(record)][field.name] = field.value

                # If we are only checking certain fields, and this is not one
                # of them, continue
                elif check_fields:
                    continue

                # Otherwise, if we are *not* only checking certain fields, add
                # it to the record if it exists
                else:
                    field = Field.from_csv(record, dataset)
                    if field.value is not None:
                        canonical[get_id(record)][field.name] = field.value

    if input_format == 'jsonld':
        canonical = json.load(dataset_fp)['records']

    return canonical


def convert(dataset_fd, source_dataset, only_fields, input_format,
            output_format):
    canonical = to_canonical(dataset_fd, source_dataset, only_fields,
                             input_format)
    sorted_canonical = {}
    for key in sorted(canonical.keys()):
        sorted_canonical[key] = canonical[key]

    dataset = Dataset(sorted_canonical)

    if output_format == 'jsonld':
        return json.dumps(dataset.as_jsonld())
    elif output_format == 'ntriples':
        return dataset.as_ntriples()
    elif output_format == 'turtle':
        return dataset.as_turtle()
    elif output_format == 'csv':
        return dataset.as_csv()


if __name__ == '__main__':
    args = parser.parse_args()

    input_format = args.input_format
    if input_format is None:
        extension = os.path.splitext(args.dataset)[1]
        if extension == '.csv':
            input_format = 'csv'
        elif extension in ['.rdf', '.ttl', '.nquads']:
            input_format = 'turtle'
        elif extension in ['.json', '.jsonld']:
            input_format = 'jsonld'
        else:
            raise Exception('Could not guess input format from filename')

    output_format = args.output_format
    if output_format is None:
        output_format = 'jsonld'

    newline = '' if input_format == 'csv' else None

    source_dataset = None
    if args.source_dataset:
        with open(args.source_dataset) as source_fd:
            source_dataset = json.load(source_fd)['records']

    only_fields = args.fields
    if only_fields is not None:
        only_fields = only_fields.split('|')

    with open(args.dataset) as dataset_fd:
        output = convert(dataset_fd, source_dataset, only_fields,
                         input_format, output_format)

    print(output)
