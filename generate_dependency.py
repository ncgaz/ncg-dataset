#!/usr/bin/env python3

import json
import pathlib
import sys


def dataset_from_version(version_path):
    return pathlib.Path('diffs') / version_path.name / 'normalized.json'


def patch_from_version(version_path):
    return pathlib.Path('diffs') / version_path.name / 'patch.jsonpatch'


def run(version_filename):
    with open('versions.json') as version_fd:
        versions = json.load(version_fd)

    version = [v for v in versions if v['filename'] == version_filename]

    if not version:
        sys.stderr.write(f"No version with filename `{version_filename}`\n")
        sys.exit(1)

    path = pathlib.Path(version_filename)

    version = version[0]
    index = versions.index(version)
    prev_version = None
    if index > 0:
        prev_version = versions[index - 1]

    dataset = dataset_from_version(path)
    patch = patch_from_version(path)
    dataset_dep = f'{dataset}: {version_filename}'

    if prev_version:
        prev_path = pathlib.Path(prev_version['filename'])
        prev_dataset = dataset_from_version(prev_path)
        sparse_opts = ''
        sparse_deps = ''
        if version.get('sparse', False):
            sparse_opts = f'-s {prev_dataset}'
            sparse_deps = f'{prev_dataset}'
        dataset_dep = f'''
{dataset}: {version_filename} {sparse_deps}
\t$(PY3) lib/convert.py {sparse_opts} $< > $@ || rm $@

{patch}: {prev_dataset} {dataset}
\t$(PY3) lib/diff.py $< $(word 2,$^) > $@ || rm $@
'''.strip()
    else:
        dataset_dep = f'''
{dataset}: {version_filename}
\t$(PY3) lib/convert.py $< > $@ || rm $@

{patch}: {dataset}
\techo {{\\"records\\":{{}}}} > $@.tmp
\t$(PY3) lib/diff.py $@.tmp $< > $@ || rm $@
\trm $@.tmp
'''.strip()

    print(dataset_dep)


if __name__ == '__main__':
    run(sys.argv[1])
