import sys


class Field:
    required = False

    def __init__(self, value):
        self.value = value

    @classmethod
    def from_csv(cls, record, dataset):
        value = None
        for name in cls.csv_names:
            if name in record:
                value = record[name]
                break
        if hasattr(cls, 'process_csv_value'):
            value = cls.process_csv_value(value, dataset)
        return cls(value)

    @staticmethod
    def equals(a, b):
        return a == b


class IDField(Field):
    name = 'id'
    csv_names = ['id', 'ID']
    required = True


class LabelField(Field):
    name = 'label'
    csv_names = ['label', 'Label']
    required = True


class DescriptionField(Field):
    name = 'description'
    csv_names = ['description', 'Description']
    required = True


class FeatureTypeField(Field):
    name = 'feature_type'
    csv_names = ['feature_type', 'feature type', 'Feature type']

    @staticmethod
    def process_csv_value(text, dataset):
        if text is None:
            return None

        words = text.split()
        if len(words) > 1:
            feature_type_string = ''.join([
                w.capitalize() for w in text.split()])
        else:
            feature_type_string = text

        return 'nct:' + feature_type_string


class CountyField(Field):
    name = 'county'
    csv_names = ['county', 'County']

    @staticmethod
    def equals(a, b):
        if a == b:
            return True

        a_counties = {county['id'] for county in a}
        b_counties = {county['id'] for county in b}

        return a_counties == b_counties

    @staticmethod
    def process_csv_value(text, dataset):
        if not text:
            return []

        ret = []

        for county_string in text.split('|'):
            # First, see if this is an ID of a county
            county = dataset.get_record(county_string)

            # Next, check if it's the name of a county
            if county is None:
                matches = dataset.search(county_string + ' County',
                                         'nct:County')
                if matches:
                    county = matches[0]

            if county is None:
                print(f'Warning: could not find county `{county_string}`',
                      file=sys.stderr)
                continue

            ret.append({
                'id': county['id'],
                'label': county['label'],
            })

        return ret


VALID_FIELDS = [
    IDField,
    LabelField,
    DescriptionField,
    FeatureTypeField,
    CountyField,
]
