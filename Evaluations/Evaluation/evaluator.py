import pandas as pd;
import numpy as np;
import data_manager;
import model;
import sys;

# parameters

# input file names
# the expected label for classification should be named "label"; for regressions should be called "rating"
# the id should be named "DBpedia_URI15"


# vectors_file = 'data/cities_sg_200_4.txt'
# gold_file = 'data/CitiesCompleteDataset15.tsv'
#
#
# # the size of the vectors used
# vec_size = 200
#
# # classification=0;regression=1
# # in case of regression, neg_mean_squared_error is used; to calculate RMSE simply calculate the root
# task = 0
# # model option: NB, SVM, KNN, LR, M5
# modelName = "NB"
#
# # data manager
# data = data_manager.data_manager.readData(vectors_file, gold_file, vec_size, task)
# data = data.sample(frac=1).reset_index(drop=True)
#
# # initialize the model
# model = model.Model(task, modelName)
# # train and print score
# model.train(data)

#vectors_file = 'data/data_van/model_embedding_mutag_random_4_250_latent.txt'
vectors_file = sys.argv[1]
#vectors_file = 'data/data_van/model_embedding_aifb_random.txt'
vectors_file = 'data/data_van/latent_mutag_500_out.txt'

#gold_file ='data/data_van/aifb_completeDataset.tsv'
gold_file = 'data/data_van/mutag_completeDataset.tsv'
#gold_file = 'data/data_van/CompleteDataset15_City.tsv'
#gold_file = 'data/data_van/CompleteDataset15_Movie.tsv'

# the size of the vectors used
#vec_size = 500
vec_size = int(sys.argv[2])

# classification=0;regression=1
# in case of regression, neg_mean_squared_error is used; to calculate RMSE simply calculate the root
#task = 0
task = int(sys.argv[3])
# model option: NB, SVM, KNN, LR, M5
#modelName = "KNN"
modelName = sys.argv[4]

# data manager
data = data_manager.data_manager.readDataMutag(vectors_file, gold_file, vec_size, task)
#data= data_manager.data_manager.readDataAifb(vectors_file, gold_file, vec_size, task)
#data = data_manager.data_manager.readDataGeneral(vectors_file,gold_file,vec_size,task,'DBpedia_URI','label', 'id')
data = data.sample(frac=1).reset_index(drop=True)
print(data)

# initialize the model
model = model.Model(task, modelName)
# train and print score
model.train(data)

