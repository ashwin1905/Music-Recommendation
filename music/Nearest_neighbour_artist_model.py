import pandas as pd
import numpy as np
from scipy.sparse import csr_matrix

user_data = pd.read_table('usersha1-artmbid-artname-plays.tsv',
                          header=None, nrows=2e7,
                          names=['users', 'musicbrainz-artist-id', 'artist-name', 'plays'],
                          usecols=['users', 'artist-name', 'plays'])

user_profiles = pd.read_table('usersha1-profile.tsv',
                              header=None,
                              names=['users', 'gender', 'age', 'country', 'signup'],
                              usecols=['users', 'country'])

user_data.head()

user_profiles.head()

if user_data['artist-name'].isnull().sum() > 0:
    user_data = user_data.dropna(axis=0, subset=['artist-name'])

artist_plays = (
user_data.groupby(by=['artist-name'])['plays'].sum().reset_index().rename(columns={'plays': 'total_artist_plays'})[
    ['artist-name', 'total_artist_plays']])

artist_plays.head()

user_data_with_artist_plays = user_data.merge(artist_plays, left_on='artist-name', right_on='artist-name', how='left')

user_data_with_artist_plays.head()

popularity_threshold = 40000
user_data_popular_artists = user_data_with_artist_plays.query('total_artist_plays >= @popularity_threshold')
user_data_popular_artists.head()

combined = user_data_popular_artists.merge(user_profiles, left_on='users', right_on='users', how='left')
usa_data = combined.query('country == \'United States\'')
usa_data.head()

if not usa_data[usa_data.duplicated(['users', 'artist-name'])].empty:
    initial_rows = usa_data.shape[0]

    usa_data = usa_data.drop_duplicates(['users', 'artist-name'])
    current_rows = usa_data.shape[0]

wide_artist_data = usa_data.pivot(index='artist-name', columns='users', values='plays').fillna(0)
wide_artist_data_sparse = csr_matrix(wide_artist_data.values)


def save_sparse_csr(filename, array):
    np.savez(filename, data=array.data, indices=array.indices, indptr=array.indptr, shape=array.shape)


def load_sparse_csr(filename):
    loader = np.load(filename)
    return csr_matrix((loader['data'], loader['indices'], loader['indptr']), shape=loader['shape'])


save_sparse_csr('lastfm_sparse_artist_matrix.npz', wide_artist_data_sparse)
