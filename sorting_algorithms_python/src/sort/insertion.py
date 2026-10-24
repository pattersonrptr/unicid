"""
Insertion sorts: insert each element into an already sorted part of the list.
"""

def insertion(nlist):
    nlist = list(nlist)
    size = len(nlist) 
    
    if size <= 1:
        return nlist

    for i in range(size):
        key = nlist[i]
        j = i - 1

        while j >= 0 and key < nlist[j]:
            nlist[j + 1] = nlist[j]
            j -= 1
        nlist[j + 1] = key
        
    return nlist


def shell(nlist):
    nlist = list(nlist)
    size = len(nlist)
    
    if size < 2:
        return nlist
  
    gap = size // 2
    
    while gap > 0:
        for i in range(gap, size):
            val = nlist[i]
            j = i
            
            while j >= gap and nlist[j - gap] > val:
                nlist[j] = nlist[j - gap]
                j -= gap
            nlist[j] = val
            
        gap //= 2
        
    return nlist
