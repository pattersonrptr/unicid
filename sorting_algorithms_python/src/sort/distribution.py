"""
Distribution sorts: place elements by value or digit instead of comparing them.
"""

import math

from .insertion import insertion


def counting(alist):
    nlist = list(alist)
    size = len(nlist)
    
    if size < 2:
        return nlist
    
    m = min(nlist) 
    k = max(nlist) - m

    counter = [0] * ( k + 1 )

    for i in nlist:
        counter[i - m] += 1
        
    ndx = 0;
    for i in range( len( counter ) ):
        while 0 < counter[i]:
            nlist[ndx] = i + m 
            ndx += 1
            counter[i] -= 1
            
    return nlist


def radix(aList):
    nlist = list(aList)
    size = len(nlist)

    if size < 2:
        return nlist

    # LSD radix works on non-negative digits: sort the magnitudes of the negatives
    # separately and put them, reversed, before the non-negatives.
    negatives = [-x for x in nlist if x < 0]
    if negatives:
        non_negatives = [x for x in nlist if x >= 0]
        return [-x for x in reversed(radix(negatives))] + radix(non_negatives)

    RADIX = 10
    maxLength = False
    tmp, placement = -1, 1
 
    while not maxLength:
        maxLength = True
        buckets = [list() for i in range(RADIX)]
 
        for i in nlist:
            tmp = i // placement    # integer division: float division loses precision on large ints
            buckets[tmp % RADIX].append(i)
            if maxLength and tmp > 0:
                maxLength = False
 
        a = 0
        for b in range( RADIX ):
            buck = buckets[b]
            for i in buck:
                nlist[a] = i
                a += 1
 
        placement *= RADIX
        
    return nlist


def bucket(aList, bucketSize=5):
    nlist = list(aList)
    size = len(nlist)
    
    if size < 2:
        return nlist

    minValue = min(nlist)
    maxValue = max(nlist)

    bucketCount = math.floor((maxValue - minValue) / bucketSize) + 1
    buckets = []
    
    for i in range(0, bucketCount):
        buckets.append([])

    for i in range(0, size):
        buckets[math.floor((nlist[i] - minValue) / bucketSize)].append(nlist[i])

    nlist = []
    for i in range(0, len(buckets)):
        buckets[i] = insertion(buckets[i])
        for j in range(0, len(buckets[i])):
            nlist.append(buckets[i][j])

    return nlist
