import pandas as pd;
import numpy as np;

class data_manager:
    def __init__(self):
        print("initiated")

    @staticmethod
    def createHeaders(vec_size):
        headers = ['id']
        for i in range(0, vec_size):
            headers.append(i)
        return headers

    @staticmethod
    def readData(vectors_file, gold_file,vec_size, task):
        vectors = pd.read_csv(vectors_file, "\t", names=data_manager.createHeaders(vec_size))

        fields = ['DBpedia_URI15', 'label'] # I guess it classifies based on label
        if task==1:
            fields = ['DBpedia_URI15', 'rating']  # using rating as a standard
        gold = pd.read_csv(gold_file, "\t", usecols=fields, encoding='latin1')
        gold.rename(columns={'DBpedia_URI15': 'id'}, inplace=True)
        if task == 1:
            gold.rename(columns={'rating': 'label'}, inplace=True)
        merged = gold.merge(vectors, on='id', how='inner')

        # write merged file to file, for debuging
        merged.to_csv(gold_file + "_Final_trainingDataset.csv", index=False)
        return merged

    @staticmethod
    def readDataAifb(vectors_file, gold_file,vec_size, task):
        vectors = pd.read_csv(vectors_file, " ", names=data_manager.createHeaders(vec_size))

        fields = ['person', 'label'] # I guess it classifies based on label
        if task==1:
            fields = ['person', 'rating']  # using rating as a standard // # tam thoi bo qua do khong co rating
        gold = pd.read_csv(gold_file, "\t", usecols=fields, encoding='latin1')
        gold.rename(columns={'person': 'id'}, inplace=True)
        if task == 1:
            gold.rename(columns={'rating': 'label'}, inplace=True) # tam thoi bo qua do khong co rating

        #gold.to_csv(gold_file + "_loaded.csv", index=False)
        #vectors.to_csv(vectors_file + "_loaded.csv", index=False)

        merged = gold.merge(vectors, on='id', how='inner') #inner join two csv table: 'Vectors' and 'Gold' on the column 'id'


        #write merged result to file, for debuging
        merged.to_csv(gold_file + "_Final_trainingDataset.csv", index=False)
        return merged

    @staticmethod
    def readDataMutag(vectors_file, gold_file,vec_size, task):
        vectors = pd.read_csv(vectors_file, " ", names=data_manager.createHeaders(vec_size))

        fields = ['bond', 'label'] # I guess it classifies based on label
        if task==1:
            fields = ['bond', 'rating']  # using rating as a standard // # tam thoi bo qua do khong co rating
        gold = pd.read_csv(gold_file, "\t", usecols=fields, encoding='latin1')
        gold.rename(columns={'bond': 'id'}, inplace=True)
        if task == 1:
            gold.rename(columns={'rating': 'label'}, inplace=True) # tam thoi bo qua do khong co rating

        #gold.to_csv(gold_file + "_loaded.csv", index=False)
        #vectors.to_csv(vectors_file + "_loaded.csv", index=False)

        merged = gold.merge(vectors, on='id', how='inner') #inner join two csv table: 'Vectors' and 'Gold' on the column 'id'


        #write merged result to file, for debuging
        merged.to_csv(gold_file + "_Final_trainingDataset.csv", index=False)
        return merged

    @staticmethod
    def readDataGeneral(vectors_file, gold_file,vec_size, task, label1, label2, id ):
        vectors = pd.read_csv(vectors_file, " ", names=data_manager.createHeaders(vec_size))

        fields = [label1, label2] # I guess it classifies based on label2
        if task==1:
            fields = [label1, 'rating']  # using rating as a standard // # tam thoi bo qua do khong co rating
        gold = pd.read_csv(gold_file, "\t", usecols=fields, encoding='latin1')
        gold.rename(columns={label1: id}, inplace=True)
        if task == 1:
            gold.rename(columns={'rating': label2}, inplace=True) # tam thoi bo qua do khong co rating

        #gold.to_csv(gold_file + "_loaded.csv", index=False)
        #vectors.to_csv(vectors_file + "_loaded.csv", index=False)

        merged = gold.merge(vectors, on=id, how='inner') #inner join two csv table: 'Vectors' and 'Gold' on the column 'id'


        #write merged result to file, for debuging
        merged.to_csv(gold_file + "_Final_trainingDataset.csv", index=False)
        return merged
