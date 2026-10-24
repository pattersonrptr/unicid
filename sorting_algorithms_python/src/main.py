'''
Created on 22 de dez de 2016

@author: patterson
'''

import random
import time

from sort.sort import bubble, insertion, selection, quick, merge, shell, heap, \
    counting, radix, bucket, gnome, comb, cocktail


ALGORITHMS = [
    ('Bubble', bubble), ('Insertion', insertion), ('Selection', selection), ('Quick', quick),
    ('Merge', merge), ('Shell', shell), ('Heap', heap), ('Counting', counting), ('Radix', radix),
    ('Bucket', bucket), ('Gnome', gnome), ('Comb', comb), ('Cocktail', cocktail),
]


def read_int(prompt):
    while True:
        try:
            return int(input(prompt))
        except ValueError:
            print('Please type an integer.')


def test_algorithm(op, vet):
    name, sort = ALGORITHMS[op]
    print('\nSorting by', name, 'method...')

    start = time.perf_counter()
    result = sort(vet)
    elapsed = time.perf_counter() - start

    print('\nOrdered array:')
    print(result)
    print('\nTime spent sorting: %.6f s' % elapsed)


def test_all_algorithms(vet):
    for op in range(len(ALGORITHMS)):
        test_algorithm(op, vet)
        input('\nPress [Enter] to continue!')


def main():

    while True:
        print('\n\n\tSorting Algorithms\n\n')

        a_length = read_int('Size of the array to be sorted: ')
        minimum = read_int('Smallest possible number: ')
        maximum = read_int('Largest possible number: ')

        vet = [random.randint(minimum, maximum) for _ in range(a_length)]

        print('\nOriginal array:')
        print(vet)

        print('\nChoose a Sort Algorithm:')

        for i, (name, _) in enumerate(ALGORITHMS):
            print(i, ' - ', name)
        print(len(ALGORITHMS), ' - ', 'All')

        op = read_int('')

        if op == len(ALGORITHMS):
            test_all_algorithms(vet)
        elif 0 <= op < len(ALGORITHMS):
            test_algorithm(op, vet)
        else:
            print('Invalid option.')


if __name__ == '__main__':
    try:
        main()
    except (EOFError, KeyboardInterrupt):  # Ctrl+D / Ctrl+C
        print('\nBye!')
