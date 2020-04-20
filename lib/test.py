import unittest
from io import StringIO

from diff import make_patch
from convert import to_canonical


class TestPatch(unittest.TestCase):
    def test_simple_change(self):
        patch = make_patch(
            {'A': {}},
            {'A': {'label': 'A'}},
        )

        self.assertEqual(
            [{"op": "add", "path": "/records/A/label", "value": "A"}],
            patch,
            'Should generate patch from adding a field')

        patch = make_patch(
            {'A': {'label': 'A'}},
            {'A': {}},
        )

        self.assertEqual(
            [{"op": "delete", "path": "/records/A/label"}],
            patch,
            'Should generate patch from removing a field')

        patch = make_patch(
            {'A': {'label': 'A'}},
            {'A': {'label': 'B'}},
        )

        self.assertEqual(
            [{"op": "replace", "path": "/records/A/label", "value": "B"}],
            patch,
            'Should generate patch from adding a field')

    def test_complicated_change(self):
        patch = make_patch(
            {
                'A': {
                    'counties': [
                        {'id': 'A'}
                    ]
                },
            },
            {
                'A': {
                    'counties': [
                        {'id': 'A'}
                    ]
                },
            },
        )

        self.assertEqual(
            [],
            patch,
            'Should not generate a patch for fields with an .equals method')

        patch = make_patch(
            {
                'A': {
                    'county': [
                        {'id': 'A'},
                    ],
                },
            },
            {
                'A': {
                    'county': [
                        {'id': 'A'},
                        {'id': 'B'},
                    ],
                },
            },
        )

        self.assertEqual(
            [{
                "op": "replace",
                "path": "/records/A/county",
                "value": [
                    {"id": "A"},
                    {"id": "B"},
                ],
            }],
            patch,
            'Should not generate a patch for fields with an .equals method')


class TestCanonicalize(unittest.TestCase):
    def test_sparse(self):
        # An empty dataset
        dataset_fp = StringIO()

        source_dataset = {
            'A': {}
        }

        output = to_canonical(dataset_fp, source_dataset, None, 'csv')

        self.assertEqual(
            output, source_dataset,
            'should fill in missing records when a source dataset is given')

    def test_only_fields(self):
        dataset_fp = StringIO('id,label\nA,"a new label"')
        source_dataset = {
            'A': {
                'id': 'A',
                'label': 'old label',
                'description': 'a description',
            }
        }

        output = to_canonical(dataset_fp, source_dataset, None, 'csv')
        self.assertEqual(
            output, {
                'A': {
                    'id': 'A',
                    'label': 'a new label',
                    'county': [],
                }
            }, 'should fill in all fields when not explicitly listed')

        dataset_fp.seek(0)

        output = to_canonical(dataset_fp, source_dataset, ['label'], 'csv')
        self.assertEqual(
            output, {
                'A': {
                    'id': 'A',
                    'label': 'a new label',
                    'description': 'a description',
                }
            }, 'should only fill in certain fields when explicitly listed')


if __name__ == '__main__':
    unittest.main()
