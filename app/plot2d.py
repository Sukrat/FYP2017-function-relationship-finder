from mpl_toolkits.mplot3d import axes3d
import matplotlib.pyplot as plt
import numpy as np


fig = plt.figure()
ax = fig.add_subplot(111)

# Grab some test data.
num_of_points = 20
X = np.random.rand(num_of_points) * 10
Y = np.random.rand(num_of_points) * 10

# Plot a basic wireframe.
ax.scatter(X, Y, )
ax.set_xlabel('Parameter 1')
ax.set_ylabel('Output')

ax.grid(True)
ax.set_xlim([0, 10])
ax.set_ylim([0, 10])

plt.show()