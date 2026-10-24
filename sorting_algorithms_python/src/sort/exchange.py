"""
Exchange sorts: compare pairs of elements and swap the ones out of order.
"""

def bubble(nlist):
    nlist = list(nlist)
    size = len(nlist) - 1

    if size < 1:
        return nlist

    swapped = True
    
    for i in range(size):
        if not swapped:
            break

        swapped = False

        for j in range(size - i):
            if nlist[j] > nlist[j + 1]:
                nlist[j + 1], nlist[j] = nlist[j], nlist[j + 1]
                swapped = True
                
    return nlist


def cocktail(aList):
    nlist = list(aList)
    size = len(nlist)
    
    if size < 2:
        return nlist
    
    swapped = True
    i = 0
    j = size - 1
    
    while i < j and swapped:
        for k in range(i, j):
            if nlist[k] > nlist[k + 1]:
                nlist[k], nlist[k + 1] = nlist[k + 1], nlist[k]
                swapped = True
        j -= 1
        
        if swapped:
            swapped = False
            for k in range(j, i, -1):
                if nlist[k] < nlist[k - 1]:
                    nlist[k], nlist[k - 1] = nlist[k - 1], nlist[k]
                    swapped = True
        
        i += 1

    return nlist


def gnome(aList):
    nlist = list(aList)
    size = len(nlist)
    
    if size < 2:
        return nlist
    
    pivot = 0
    nlist_length = len(nlist) 
    while pivot < nlist_length - 1:
        if nlist[pivot] > nlist[pivot + 1]:
            nlist[pivot + 1], nlist[pivot] = nlist[pivot], nlist[pivot + 1]
            if pivot > 0:
                pivot -= 2
        pivot += 1
        
    return nlist


def comb(aList):
    nlist = list(aList)
    size = len(nlist)
    
    if size < 2:
        return nlist
    
    gap = size
    swaps = True
    
    while gap > 1 or swaps:
        gap = max(1, int(gap / 1.25)) # Minimun gap is 1
        swaps = False
        
        for i in range(size - gap):
            j = i + gap
    
            if nlist[i] > nlist[j]:
                nlist[i], nlist[j] = nlist[j], nlist[i]
                swaps = True
 
    return nlist


def quick(nlist):
    nlist = list(nlist)
    size = len(nlist)
    
    if size <= 1:
        return nlist
    
    # Middle pivot: avoids the O(n²) worst case (and the recursion limit) on already sorted input.
    # Three-way partition keeps equal keys in their original order, so the sort is stable.
    pivot = nlist[size // 2]
    lr = [x for x in nlist if x < pivot]    # lower
    eq = [x for x in nlist if x == pivot]   # equal
    gt = [x for x in nlist if x > pivot]    # greater

    return quick(lr) + eq + quick(gt)
