# User-Rating-prediction
Prediction algorithm for a user based on a one dimensional space.
Download all .java files and run main.
Update Main.java to change which user and movie rating will be determined. 
Update matrix.txt or any text file for RecSys to read and predict user rating.

Matrix.txt must be in the format of

x y <br>
float_1 1 4 6 3 <br>
. <br>
. <br>
float_n 5 0 3 2<br><br>

where x is the number of users, y is the number of movies. Float_1...float_n are the values per each user used to hash and used as determining what the given user will rate the movie. Integer values preceeding after float_n are the movie ratings where 0 means not rated.
