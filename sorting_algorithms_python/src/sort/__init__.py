"""
Sorting algorithms grouped by category (Knuth's classification).

Every function receives an iterable and returns a new sorted list; the input is never modified.
"""

from .exchange import bubble, cocktail, gnome, comb, quick
from .selection import selection, heap
from .insertion import insertion, shell
from .merging import merge
from .distribution import counting, radix, bucket
from .hybrid import timsort, introsort

CATEGORIES = {
    'Exchange': [bubble, cocktail, gnome, comb, quick],
    'Selection': [selection, heap],
    'Insertion': [insertion, shell],
    'Merging': [merge],
    'Distribution': [counting, radix, bucket],
    'Hybrid': [timsort, introsort],
}
