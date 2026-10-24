"""
Hybrid sorts: combine algorithms from different categories, using each where it performs best.
"""

import math

from .insertion import insertion
from .selection import heap


# Timsort ==================================================================

MIN_MERGE = 32


def timsort(alist):
    """
    Simplified Timsort, the algorithm behind Python's sorted() and list.sort().

    1. Splits the list into "runs": stretches that are already ascending, or strictly
       descending (reversed in place).
    2. Short runs are extended to `min_run` elements with binary insertion sort.
    3. Runs go on a stack and are merged following Timsort's invariants, which keep
       the merges balanced.

    Stable and adaptive: an already sorted list is a single run, sorted in O(n).
    Leaves out CPython's "galloping" optimization of the merge step.
    """
    nlist = list(alist)
    size = len(nlist)

    if size < 2:
        return nlist

    min_run = _min_run(size)
    runs = []   # stack of (start, length)
    lo = 0

    while lo < size:
        length = _count_run(nlist, lo, size)

        if length < min_run:
            forced = min(min_run, size - lo)
            _binary_insertion(nlist, lo, lo + forced, lo + length)
            length = forced

        runs.append((lo, length))
        _merge_collapse(nlist, runs)
        lo += length

    while len(runs) > 1:
        _merge_at(nlist, runs, len(runs) - 2)

    return nlist


def _min_run(n):
    """Minimum run length: between MIN_MERGE / 2 and MIN_MERGE, so that n / min_run is close to a power of 2."""
    r = 0
    while n >= MIN_MERGE:
        r |= n & 1
        n >>= 1
    return n + r


def _count_run(nlist, lo, hi):
    """Length of the run starting at lo. A strictly descending run is reversed in place."""
    run_hi = lo + 1

    if run_hi == hi:
        return 1

    if nlist[run_hi] < nlist[lo]:
        # Strictly descending: reversing it cannot reorder equal elements, so stability is kept.
        while run_hi < hi and nlist[run_hi] < nlist[run_hi - 1]:
            run_hi += 1
        nlist[lo:run_hi] = reversed(nlist[lo:run_hi])
    else:
        while run_hi < hi and not nlist[run_hi] < nlist[run_hi - 1]:
            run_hi += 1

    return run_hi - lo


def _binary_insertion(nlist, lo, hi, start):
    """Sorts nlist[lo:hi], knowing nlist[lo:start] is already sorted."""
    for i in range(start, hi):
        pivot = nlist[i]
        left, right = lo, i

        while left < right:
            mid = (left + right) // 2
            if pivot < nlist[mid]:
                right = mid
            else:
                left = mid + 1      # equal keys go after: stable

        nlist[left + 1:i + 1] = nlist[left:i]
        nlist[left] = pivot


def _merge_collapse(nlist, runs):
    """
    Merges runs on top of the stack until the invariants hold for the top three runs X, Y, Z (Z on top):
    len(X) > len(Y) + len(Z) and len(Y) > len(Z).
    """
    while len(runs) > 1:
        n = len(runs) - 2

        if (n > 0 and runs[n - 1][1] <= runs[n][1] + runs[n + 1][1]) or \
                (n > 1 and runs[n - 2][1] <= runs[n - 1][1] + runs[n][1]):
            if runs[n - 1][1] < runs[n + 1][1]:
                n -= 1
        elif runs[n][1] > runs[n + 1][1]:
            break

        _merge_at(nlist, runs, n)


def _merge_at(nlist, runs, i):
    """Merges the adjacent runs i and i + 1 of the stack."""
    (start, len_a), (mid, len_b) = runs[i], runs[i + 1]
    left = nlist[start:mid]
    i_left, j, k = 0, mid, start
    end = mid + len_b

    while i_left < len(left) and j < end:
        if nlist[j] < left[i_left]:
            nlist[k] = nlist[j]
            j += 1
        else:
            nlist[k] = left[i_left]
            i_left += 1
        k += 1

    # Whatever is left of the right run is already in place.
    nlist[k:k + len(left) - i_left] = left[i_left:]

    runs[i] = (start, len_a + len_b)
    del runs[i + 1]


# Introsort ================================================================

INSERTION_THRESHOLD = 16


def introsort(alist):
    """
    Introsort, the algorithm behind C++'s std::sort.

    Starts as an in-place quicksort (median-of-three pivot). When the recursion gets deeper
    than 2·log2(n), a sign that the pivots are bad, the partition is finished with heapsort,
    which guarantees O(n log n) in the worst case. Partitions smaller than INSERTION_THRESHOLD
    are finished with insertion sort, faster than recursion on tiny inputs.

    Not stable.
    """
    nlist = list(alist)
    size = len(nlist)

    if size < 2:
        return nlist

    _introsort(nlist, 0, size - 1, 2 * math.floor(math.log2(size)))

    return nlist


def _introsort(nlist, lo, hi, depth):
    while hi - lo + 1 > INSERTION_THRESHOLD:
        if depth == 0:
            nlist[lo:hi + 1] = heap(nlist[lo:hi + 1])
            return

        depth -= 1
        p = _partition(nlist, lo, hi)

        # Recurse into the smaller side and loop over the larger one: the stack stays O(log n).
        if p - lo < hi - p:
            _introsort(nlist, lo, p - 1, depth)
            lo = p + 1
        else:
            _introsort(nlist, p + 1, hi, depth)
            hi = p - 1

    nlist[lo:hi + 1] = insertion(nlist[lo:hi + 1])


def _partition(nlist, lo, hi):
    """Lomuto partition around the median of nlist[lo], nlist[mid] and nlist[hi]."""
    mid = (lo + hi) // 2

    if nlist[mid] < nlist[lo]:
        nlist[lo], nlist[mid] = nlist[mid], nlist[lo]
    if nlist[hi] < nlist[lo]:
        nlist[lo], nlist[hi] = nlist[hi], nlist[lo]
    if nlist[mid] < nlist[hi]:
        nlist[mid], nlist[hi] = nlist[hi], nlist[mid]   # median now at hi

    pivot = nlist[hi]
    i = lo

    for j in range(lo, hi):
        if nlist[j] < pivot:
            nlist[i], nlist[j] = nlist[j], nlist[i]
            i += 1

    nlist[i], nlist[hi] = nlist[hi], nlist[i]

    return i
