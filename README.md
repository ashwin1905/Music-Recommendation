# Music-Recommendation


1)	MUSIC:

This file contains 2 parts. 
Before running any code please get the dataset from the below file and paste it inside the folder. Please make sure you only paste everything inside the dataset folder.
https://studentuml-my.sharepoint.com/:f:/g/personal/ashwin_nair_student_uml_edu/Eufr2w5gl6ZFvyl5-gMi4ocBNsLiG31S11h7J_VfAT_Zlg?e=XwIDtL

NOTE: You can run.py to run all the python sequentially and get the data.

First is the collaborative filtering, there are 6 files in total which is used 
a)	Popular.py and popular_recommendation_model.py which gives us the top 10 popular songs
b)	Similar.py and Similar_Recommenders.py which uses cooccurrence matrix to get the similar songs based on history.
c)	Nearest_neighbour_artist_model.py and Nearest_neighbour_recomendation.py which gives us the similar artist that the user may like to listen to.
Second part is retrieving mfcc features and running CNN to get the model
a)	Mfcc1.py: Gets the features of GITZAN dataset and train the model using Sequential Neural Network.
b)	Mfcc_predict.py: Uses the model and predicts the genre for the new songs and this is the file predicted_genre.csv that is used in the next part

2)	MUSEEEGCLASSIFIER 2:

This file contains the android app. 
Before you can run the app you have to export the rf.model and predicted_genre.csv that you got from the above python codes to your phone external memory, not on any folder.
This app contains the emotion detection using the trained model. Then we use the emotion detected and predicted_genre.csv to predict the songs for the user.

