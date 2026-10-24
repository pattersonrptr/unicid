"""
Times every algorithm on the same arrays and prints a Markdown table.

Random arrays for each size, plus an already sorted array of the largest size,
which shows which algorithms are adaptive.

Usage: python benchmark.py [size ...]    (default sizes: 100 1000 5000)
"""

import random
import sys
import time

from main import ALGORITHMS


def best_time(sort, data, repeat=3):
    best = float('inf')
    for _ in range(repeat):
        start = time.perf_counter()
        sort(data)
        best = min(best, time.perf_counter() - start)
    return best


if __name__ == '__main__':
    sizes = [int(n) for n in sys.argv[1:]] or [100, 1000, 5000]

    random.seed(42)
    arrays = [('n = %d' % n, [random.randint(0, 10 * n) for _ in range(n)]) for n in sizes]
    largest = max(sizes)
    arrays.append(('n = %d, sorted' % largest, list(range(largest))))

    print('| Algorithm | Category | ' + ' | '.join(label for label, _ in arrays) + ' |')
    print('|---|---|' + '---:|' * len(arrays))

    for name, sort, category in ALGORITHMS:
        times = [best_time(sort, data) * 1000 for _, data in arrays]
        print('| %s | %s | ' % (name, category) + ' | '.join('%.2f ms' % t for t in times) + ' |')
