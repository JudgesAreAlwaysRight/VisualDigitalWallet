# -*- coding: utf-8 -*-
import matplotlib.pyplot as plt
from matplotlib.pyplot import MultipleLocator

plt.style.use(['science', 'ieee', 'std-colors'])

x1 = [2, 3, 4]
y1 = [1767.6439, 1828.3715, 2362.4208]
x2 = [3, 4, 5]
y2 = [1535.6257, 1828.3715, 2330.8048]

ax1 = plt.subplot(1, 2, 1)
plt.scatter(x1, y1, marker='.')
plt.plot(x1, y1)
plt.xlabel("k")
plt.ylabel("time(ms)")
# plt.legend()
# plt.show()
# plt.savefig('fig1.pdf', format='pdf', dpi=600)

ax2 = plt.subplot(1, 2, 2)
plt.scatter(x2, y2, marker='.')
plt.plot(x2, y2)
plt.xlabel("n")
plt.ylabel("time(ms)")
# plt.legend()
# plt.show()
plt.savefig('kntime.pdf', format='pdf', dpi=600)
