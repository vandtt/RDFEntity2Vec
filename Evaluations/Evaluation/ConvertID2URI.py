import gensim, logging, os, sys, gzip

# with is like your try .. finally block in this case
with open('latent_movie_500_6.txt', 'r') as file:
    # read a list of lines into data
    data = file.readlines()

with open('dictionary_movie.nt.txt', 'r') as file:
    # read a list of lines into data
    dics = file.readlines()

print("data: ",len(data))
print("dics ", len(dics))

# create a map to dics
keyArr =[len(dics)]
valArr = [len(dics)]

for dic in dics:
    valArr.append(dic.split(" ")[0])
    keyArr.append(dic.split(" ")[1].rstrip("\n"))

map_dic = dict(zip(keyArr, valArr))

i = 0
for line in data:
    if(i!=0):
        id = line.split(" ")[0]
        arr = line.split(" ")
        arr[0] = map_dic.get(id).rstrip("\n")
        line = ' '.join(arr)
        data[i] = line

    i=i+1

with open('latent_movie_500_out.txt', 'w') as file:
    file.writelines( data )