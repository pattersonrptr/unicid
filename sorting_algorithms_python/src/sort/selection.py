"""
Selection sorts: repeatedly select the smallest (or largest) element and move it into place.
"""

def selection(nlist):
    nlist = list(nlist)
    size = len(nlist)
    
    if size < 1:
        return nlist
    
    for i in range(size - 1):
        lower = i
        for j in range(i + 1, size):
            if nlist[j] < nlist[lower]:
                lower = j

        nlist[i], nlist[lower] = nlist[lower], nlist[i]
        
    return nlist


def heap(nlist):
    nlist = list(nlist)
    size = len(nlist)
    
    if size < 2:
        return nlist
    
    for start in range((size - 2) // 2, -1, -1):
        _max_heap(nlist, start, size - 1)
        
    for end in range(size - 1, 0, -1):  
        nlist[end], nlist[0] = nlist[0], nlist[end]
        _max_heap(nlist, 0, end - 1)
        
    return nlist


def _max_heap(nlist, root, end):
    while True:
        child = root * 2 + 1
        
        if child > end:
            break
        if child + 1 <= end and nlist[child] < nlist[child + 1]:
            child += 1
        if nlist[root] < nlist[child]:
            nlist[root], nlist[child] = nlist[child], nlist[root]
            root = child
        else:
            break
