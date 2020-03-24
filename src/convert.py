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


def get_id(record):
    if 'id' in record:
        return record['id']
    elif 'ID' in record:
        return record['ID']
    else:
        raise ValueError('Record has no ID')


def to_canonical(dataset_fp, input_format):
    canonical = {}

    if input_format == 'csv':
        reader = csv.DictReader(dataset_fp)
        records = [row for row in reader]

        for record in records:
            canonical[get_id(record)] = {}

        for Field in VALID_FIELDS:
            dataset = Dataset(canonical)
            for record in records:
                field = Field.from_csv(record, dataset)
                if field.value is not None:
                    canonical[get_id(record)][field.name] = field.value

    if input_format == 'jsonld':
        canonical = json.load(dataset_fp)['records']

    return canonical


def convert(dataset_fd, input_format, output_format):
    canonical = to_canonical(dataset_fd, input_format)
    dataset = Dataset(canonical)

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

    with open(args.dataset) as dataset_fd:
        output = convert(dataset_fd, input_format, output_format)

    print(output)
