import scipy
import os


class MySentences_Normal(object):
	def __init__(self, fname):
		self.fname = fname

	def __iter__(self):
        try:
            for line in open(os.path(self.fname)):
                line = line.rstrip('\n')
                words = line.split(" ")
                yield words
        except Exception:
            print("Failed reading file:")
            print(self.fname)

#sentences = MySentences('WalksData') # a memory-friendly iterator
sentences = list(MySentences_Normal('latent'))


vectors_file = 'data/data_van/model_embedding_aifb_random_7_3_latent.txt'

scipy.stats.friedmanchisquare()
