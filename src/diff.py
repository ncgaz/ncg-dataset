#!/usr/bin/env python3

import argparse
import json
from jsonpointer import JsonPointer

from fields import VALID_FIELDS


parser = argparse.ArgumentParser(description='Diff the gazetteer dataset')
parser.add_argument('source_dataset')
parser.add_argument('target_dataset')


def make_patch(old, new):
    patch = []
    for item_id, record in new.items():
        old_record = old.get(item_id, None)

        if old_record is None:
            patch.append({
                "op": "add",
                "path": JsonPointer.from_parts(['records', item_id]).path,
                "value": record
            })
            continue

        # This would be the case if the record were filled in from a sparse
        # version of the dataset
        if record == old_record:
            continue

        for Field in VALID_FIELDS:
            old_value = old_record.get(Field.name, None)
            new_value = record.get(Field.name, None)

            if Field.equals(old_value, new_value):
                continue

            pointer = JsonPointer.from_parts(['records', item_id, Field.name])

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


if __name__ == '__main__':
    args = parser.parse_args()

    with open(args.target_dataset, 'r') as target_fd:
        target = json.load(target_fd)['records']

    with open(args.source_dataset, 'r') as source_fd:
        source = json.load(source_fd)['records']

    patch = make_patch(source, target)
    print(json.dumps(patch))
