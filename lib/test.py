import unittest

from diff import make_patch


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


if __name__ == '__main__':
    unittest.main()
