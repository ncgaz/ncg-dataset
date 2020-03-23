import copy
from collections import defaultdict


class Dataset:
    def __init__(self, dataset):
        self.dataset = copy.deepcopy(dataset)

        self._items_by_name = defaultdict(list)
        for record in self.dataset.values():
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
        pass

    def as_turtle(self):
        pass

    def as_csv(self):
        pass
