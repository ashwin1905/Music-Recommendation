import pandas
from sklearn.model_selection import train_test_split
import random

import popular_recommendation_model

triplets_file = '10000.txt'
songs_metadata_file = 'song_data.csv'

song_df_1 = pandas.read_table(triplets_file,header=None)
song_df_1.columns = ['user_id', 'song_id', 'listen_count']

song_df_2 = pandas.read_csv(songs_metadata_file)

song_df = pandas.merge(song_df_1, song_df_2.drop_duplicates(['song_id']), on="song_id", how="left")

song_grouped = song_df.groupby(['song_id']).agg({'listen_count': 'count'}).reset_index()
grouped_sum = song_grouped['listen_count'].sum()
song_grouped['percentage'] = song_grouped['listen_count'].div(grouped_sum)*100
song_grouped.sort_values(['listen_count', 'song_id'], ascending = [0,1])

users = song_df['user_id'].unique()
songs = song_df['song_id'].unique()

train_data, test_data = train_test_split(song_df, test_size = 0.20, random_state=0)

pm = popular_recommendation_model.popularity_recommender_py()
pm.create(train_data, 'user_id', 'song_id', 'title')
user_id = random.choice(users)
final_result = pm.recommend(user_id)
df = pandas.DataFrame(final_result)
"""csv_data = df.to_csv(index=False)"""
df.to_csv('popular.csv', index=False)




