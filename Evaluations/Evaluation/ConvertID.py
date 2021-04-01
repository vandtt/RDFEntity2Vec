import gensim, logging, os, sys, gzip

# with is like your try .. finally block in this case
with open('city_500_.txt', 'r') as file:
    # read a list of lines into data
    data = file.readlines()

with open('dictionary_city.txt', 'r') as file:
    # read a list of lines into data
    dics = file.readlines()

print("data: ",len(data))
print("dics ", len(dics))
j=0
for dic in dics:
    #print("dic: ", dic)
    i = 0
    for line in data:
        #print("line: ", line)
        if(line.split(" ")[0] == dic.split(" ")[1]):
            arr = line.split(" ")
            #print("arr ", arr)
            arr[0] = dic.split(" ")[0].rstrip("\n")
            #print("join ", ' '.join(arr))

            line = ' '.join(arr)
            #print("new line: ", line)

            data[i] = line
            #print("new data line: ", data[i])
            #print("matched")
            break
        i=i+1
        #print("i: ",i)
    j=j+1
    print("j: ", j)


with open('city_500_out.txt', 'w') as file:
    file.writelines( data )





#line = data[1].split(" ")
#print(line[0])
#line[0] = "change"
#print(line[0])

#newline = "change " + data[1].split(" ")[1]

#data[1] =newline

# and write everything back
#with open('ex_out.txt', 'w') as file:
#    file.writelines( data )