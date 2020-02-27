import csv
import json

with open('dataset.json', 'r') as dataset_fd:
    dataset = json.load(dataset_fd)

with open('dataset.csv', 'w') as fd:
    writer = csv.writer(fd)
    writer.writerow(['id', 'label', 'description', 'feature_type'])
    for record in dataset['records'].values():
        writer.writerow([
            record['id'],
            record['label'],
            record['description'],
            record['feature_type'].replace('nct:', '')
        ])
