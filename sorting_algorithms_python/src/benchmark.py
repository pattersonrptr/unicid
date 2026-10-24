"""
Times every algorithm on the same random arrays and prints a Markdown table.

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
    arrays = {n: [random.randint(0, 10 * n) for _ in range(n)] for n in sizes}

    print('| Algorithm | ' + ' | '.join('n = %d' % n for n in sizes) + ' |')
    print('|---|' + '---:|' * len(sizes))

    for name, sort in ALGORITHMS:
        times = [best_time(sort, arrays[n]) * 1000 for n in sizes]
        print('| %s | ' % name + ' | '.join('%.2f ms' % t for t in times) + ' |')
