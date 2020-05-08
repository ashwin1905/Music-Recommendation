import numpy as np
import pandas as pd
from keras.engine.saving import load_model
from sklearn.preprocessing import LabelEncoder, StandardScaler
import csv

model = load_model('snn.model')

data1 = pd.read_csv('data1.csv')
data1.head()

title = data1.iloc[:, 0]

data1 = data1.drop(['filename'],axis=1)
data1.head()
i = 0

header = 'songnsame genre emotion'
header = header.split()
file = open('predicted_genres.csv', 'w', newline='')
with file:
    writer = csv.writer(file)
    writer.writerow(header)

for row in data1:
    genre_list1 = data1.iloc[:, -1]
    encoder1 = LabelEncoder()
    y1 = encoder1.fit_transform(genre_list1)
    scaler = StandardScaler()
    X1 = scaler.fit_transform(np.array(data1.iloc[:, :-1], dtype = float))
    predictions = model.predict(X1)
    predictions[i].shape
    np.sum(predictions[i])
    result = np.argmax(predictions[i])
    print(np.argmax(predictions[i]), ",", title[i])
    i = i + 1
    if result == 0:
        final_result = "blues"
        emotion="Positive"
    elif result == 1:
        final_result = "classical"
        emotion = "Neutral"
    elif result == 2:
        final_result = "country"
        emotion = "Neutral"
    elif result == 3:
        final_result = "disco"
        emotion = "Positive"
    elif result == 4:
        final_result = "hiphop"
        emotion = "Positive"
    elif result == 5:
        final_result = "jazz"
        emotion="Positive"
    elif result == 6:
        final_result = "metal"
        emotion = "Negative"
    elif result == 7:
        final_result = "pop"
        emotion = "Neutral"
    elif result == 8:
        final_result = "reggae"
        emotion = "Negative"
    elif result == 9:
        final_result = "rock"
        emotion = "Negative"
    print(final_result)
    print(emotion)
    file = open('predicted_genres.csv', 'a', newline='')
    with file:
        to_append = f'{title[i]} {final_result} {emotion}'
        writer = csv.writer(file)
        writer.writerow(to_append.split())



