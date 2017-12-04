from utils import *
from tkinter import filedialog
import csv
import numpy as np

print("***Welcome to function-relationship-finder***")
st = input("Press enter to select a file...")
file = filedialog.askopenfile()
print("Opening file: {0}".format(file.name))

# file_name = "C:/Users/sukra/Downloads/hwe_mpi.csv"
file_name = file.name
csv_data = np.genfromtxt(file_name, delimiter=",", dtype=None)
sorted_data = np.sort(csv_data, order=csv_data.dtype.names[1:])
colName = input("Enter the column you dont want to group? must be >= 1\n")
delta = input("Enter tolerance when grouping?\n")
group_data = group_by(sorted_data, int(colName), float(delta))

f = open("{0}{1}".format(file_name, ".out.csv"), 'w')
for index, group in enumerate(group_data):
    i = str(index)
    for row in group:
        st = ",".join(map(str, (i,) + row))
        f.write(st)
        f.write("\n")
f.close()
