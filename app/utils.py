import re
import numpy as np

def take_input(question, regex, error_msg):
    i = input(question)
    while True:
        if(re.fullmatch(regex, i)):
            return i
        i = input(error_msg)


def group_by(sorted_arr, index, delta):
    i = 0
    rows = len(sorted_arr)
    group = []
    a = []
    while i < rows:
        arr = tuple(sorted_arr[i])
        temp = [arr]
        a = arr[1:index] + arr[index+1:]
        while i + 1 < rows:
            i += 1
            arr = tuple(sorted_arr[i])
            b = arr[1:index] + arr[index+1:]
            if np.allclose(a, b, rtol=0, atol=delta):
                temp.append(arr)
            else:
                i -= 1
                break
        group.append(temp)
        i += 1
    return group
