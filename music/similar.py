import pandas
from sklearn.model_selection import train_test_split
import Similar_Recommenders
import os

triplets_file = '10000.txt'
songs_metadata_file = 'song_data.csv'

song_df_1 = pandas.read_table(triplets_file, header=None)
song_df_1.columns = ['user_id', 'song_id', 'listen_count']

song_df_2 = pandas.read_csv(songs_metadata_file)

song_df = pandas.merge(song_df_1, song_df_2.drop_duplicates(['song_id']), on="song_id", how="left")

song_grouped = song_df.groupby(['song_id']).agg({'listen_count': 'count'}).reset_index()
grouped_sum = song_grouped['listen_count'].sum()
song_grouped['percentage'] = song_grouped['listen_count'].div(grouped_sum) * 100
song_grouped.sort_values(['listen_count', 'song_id'], ascending=[0, 1])

users = song_df['user_id'].unique()
songs = song_df['song_id'].unique()

train_data, test_data = train_test_split(song_df, test_size=0.20, random_state=0)

is_model = Similar_Recommenders.item_similarity_recommender_py()
is_model.create(train_data, 'user_id', 'title')

user_id = (users[2])
user_items = is_model.get_user_items(user_id)

is_model.recommend(user_id)

i = 0
while i < len(user_items):
    print(user_items[i])
    song = is_model.get_similar_items(user_items[i])
    df = pandas.DataFrame(song)
    hdr = False if os.path.isfile('similar.csv') else True
    df.to_csv('similar.csv', mode='a', header=hdr)
    i = i + 1

csv = 'similar.csv'
record = pandas.read_csv(csv)
songs = pandas.unique(record['song'])
df = pandas.DataFrame(songs)
df.to_csv('similar__unique_song.csv', header='songs', index=False)