import csv
reader = csv.reader(open("similar__unique_song.csv"))
reader1 = csv.reader(open("popular.csv"))
f = open("combined.csv", "w")
writer = csv.writer(f)
title="title"

for row in reader:
    if row != 'title':
        writer.writerow(row)
for row in reader1:
    if row != title:
        writer.writerow(row)
f.close()