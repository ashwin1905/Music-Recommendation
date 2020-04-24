import pandas as pd

csv = 'similar.csv'
record = pd.read_csv(csv)
songs = pd.unique(record['song'])
df = pd.DataFrame(songs, columns=["songs"])
df.to_csv('similar__unique_song.csv', header=True, index=False)

