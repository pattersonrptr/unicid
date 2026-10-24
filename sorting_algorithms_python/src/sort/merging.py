"""
Merge sorts: combine already sorted sequences into a larger sorted one.
"""

def merge(nlist):
    if len(nlist) < 2:
        return list(nlist)

    result, mid = list(), len(nlist) // 2

    l = merge(nlist[:mid])  # left
    r = merge(nlist[mid:])  # right
    i = j = 0

    # Walks both halves by index: list.pop(0) is O(n) and would make the merge step quadratic.
    while i < len(l) and j < len(r):
        if l[i] > r[j]:
            result.append(r[j])
            j += 1
        else:
            result.append(l[i])
            i += 1

    result.extend(l[i:])
    result.extend(r[j:])

    return result
