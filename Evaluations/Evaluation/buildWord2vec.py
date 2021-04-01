'''
Created on Feb 16, 2016

@author: petar
'''
# import modules; set up logging
import gensim, logging, os, sys, gzip
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s',filename='word2vec.out', level=logging.INFO)

class MySentences(object):
	def __init__(self, dirname):
		self.dirname = dirname

	def __iter__(self):
		for fname in os.listdir(self.dirname):
			try:
				for line in gzip.open(os.path.join(self.dirname, fname), mode='rt'):
					line = line.rstrip('\n')
					words = line.split(" ")

					yield words
			except Exception:
				print("Failed reading file:")
				print(fname)

class MySentences_Normal(object):
	def __init__(self, dirname):
		self.dirname = dirname

	def __iter__(self):
		for fname in os.listdir(self.dirname):
			print('fname', fname)
			try:
				for line in open(os.path.join(self.dirname, fname)):
					line = line.rstrip('\n')
					words = line.split(" ")
					print("words", words)
					yield words
			except Exception:
				print("Failed reading file:")
				print(fname)

sentences = list(MySentences('outputs')) # a memory-friendly iterator


#sentences = list(MySentences_Normal('aifb_5'))

#print('sentences', sentences)
#sg 500
model = gensim.models.Word2Vec(size=500, workers=5, window=5, sg=1, min_count= 10, negative=15, iter=5)
model.build_vocab(sentences)
model.train(sentences, total_examples=len(sentences), epochs=10)

print("total sample:", len(sentences))
#sg/cbow features iterations window negative hops random walks
#model.save('DB2Vec_sg_500_5_5_15_2_500')

#model.save('model_embedding_mutag_random_4__latent')
#model.wv.save_word2vec_format(fname="model_embedding_mutag_random_4__latent.txt", fvocab=None, binary=False)

#model.save('__latent')

fileoutput = "rdf2vec_aifb_5_sg_500.txt"
model.wv.save_word2vec_format(fname=fileoutput, fvocab=None, binary=False)

#*** change id of fileoutput




#open_model = gensim.models.Word2Vec.load('model_embedding_aifb_random_7_3_latent')
#open_model = gensim.models.KeyedVectors.load_word2vec_format('model_embedding_aifb_random_7_3_latent.txt', binary=False)

#print('http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/id1876instance')
#print(open_model.most_similar(positive=['http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/id1876instance'], topn=20))

'''
#sg 200
model1 = gensim.models.Word2Vec(size=200, workers=5, window=5, sg=1, negative=15, iter=5)
model1.reset_from(model)


#cbow 500
model2 = gensim.models.Word2Vec(size=500, workers=5, window=5, sg=0, iter=5,cbow_mean=1, alpha = 0.05)
model2.reset_from(model)


#cbow 200
model3 = gensim.models.Word2Vec(size=200, workers=5, window=5, sg=0, iter=5, cbow_mean=1, alpha = 0.05)
model3.reset_from(model)

del model

model1.train(sentences)
#model1.save('DB2Vec_sg_200_5_5_15_2_500')
model1.save('model_aifb_sg_200')

del model1

model2.train(sentences)
#model2.save('DB2Vec_cbow_500_5_5_2_500')
model2.save('model_aifb_cbow_500')

del model2

model3.train(sentences)
#model3.save('DB2Vec_cbow_200_5_5_2_500')
model3.save('model_aifb_cbow_200')
'''