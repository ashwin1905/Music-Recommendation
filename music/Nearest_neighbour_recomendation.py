import numpy as np
import pandas
import csv
import Nearest_neighbour_artist_model
from sklearn.neighbors import NearestNeighbors

wide_artist_data_sparse=Nearest_neighbour_artist_model.load_sparse_csr('lastfm_sparse_artist_matrix.npz')


model_knn = NearestNeighbors(metric='cosine', algorithm='brute')
model_knn.fit(wide_artist_data_sparse)

query_index = np.random.choice(wide_artist_data_sparse.shape[0])
distances, indices = model_knn.kneighbors(
    Nearest_neighbour_artist_model.wide_artist_data.iloc[query_index, :].values.reshape(1, -1), n_neighbors = 20)

for i in range(0, len(distances.flatten())):
    if i == 0:
        abc = 'Recommendations for {0}:\n'.format(Nearest_neighbour_artist_model.wide_artist_data.index[query_index])
        with open('artist.csv', 'w', newline='') as file:
            writer = csv.writer(file)
            writer.writerow([abc])
    else:
        abc = '{0}'.format(Nearest_neighbour_artist_model.wide_artist_data.index[indices.flatten()[i]])
        with open('artist.csv', 'a', newline='') as file:
            writer = csv.writer(file)
            writer.writerow([abc])