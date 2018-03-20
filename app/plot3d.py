from mpl_toolkits.mplot3d import axes3d
import matplotlib.pyplot as plt
import numpy as np


fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

# Grab some test data.
num_of_points = 50
X = np.random.rand(num_of_points) * 10
Y = np.random.rand(num_of_points) * 10
Z = np.random.rand(num_of_points) * 10

# Plot a basic wireframe.
ax.scatter(X, Y, Z, )
ax.set_xlabel('Parameter 1')
ax.set_ylabel('Parameter 2')
ax.set_zlabel('Output')

ax.grid(True)
ax.set_xlim([0, 10])
ax.set_ylim([0, 10])
ax.set_zlim([0, 10])

plt.show()