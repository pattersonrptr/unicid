import random
import unittest

from src import sort as s

ALL = [s.bubble, s.insertion, s.selection, s.quick, s.merge, s.shell, s.heap,
       s.counting, s.radix, s.bucket, s.gnome, s.comb, s.cocktail, s.timsort, s.introsort]

# Algorithms whose implementation keeps equal keys in their original order.
STABLE = [s.bubble, s.insertion, s.quick, s.merge, s.gnome, s.cocktail, s.timsort]

EDGE_CASES = {
    'empty': [],
    'single': [7],
    'two unsorted': [2, 1],
    'two sorted': [1, 2],
    'sorted': list(range(20)),
    'reversed': list(range(20, 0, -1)),
    'duplicates': [3, 1, 3, 1, 2, 2, 3],
    'all equal': [5] * 10,
    'negatives': [3, -1, -20, 7, 0, -5],
    'only negatives': [-3, -10, -1],
}


class Item:
    """Compares only by key, so stability can be observed through `tag`."""

    def __init__(self, key, tag):
        self.key, self.tag = key, tag

    def __lt__(self, other):
        return self.key < other.key

    def __gt__(self, other):
        return self.key > other.key

    def __eq__(self, other):
        return self.key == other.key


class TestSortsAgainstSorted(unittest.TestCase):

    def test_edge_cases(self):
        for sort in ALL:
            for name, data in EDGE_CASES.items():
                with self.subTest(algorithm=sort.__name__, case=name):
                    self.assertEqual(sort(data), sorted(data))

    def test_random_arrays(self):
        rng = random.Random(2017)
        arrays = [[rng.randint(-1000, 1000) for _ in range(rng.randint(0, 80))] for _ in range(200)]
        for sort in ALL:
            with self.subTest(algorithm=sort.__name__):
                for data in arrays:
                    self.assertEqual(sort(data), sorted(data))

    def test_input_is_not_modified(self):
        for sort in ALL:
            with self.subTest(algorithm=sort.__name__):
                data = [3, 1, 2]
                sort(data)
                self.assertEqual(data, [3, 1, 2])


class TestStability(unittest.TestCase):

    def test_stable_algorithms_keep_order_of_equal_keys(self):
        items = [Item(k, i) for i, k in enumerate([2, 1, 2, 1, 3, 2, 1])]
        expected = [x.tag for x in sorted(items, key=lambda x: x.key)]
        for sort in STABLE:
            with self.subTest(algorithm=sort.__name__):
                self.assertEqual([x.tag for x in sort(items)], expected)


class TestHybrids(unittest.TestCase):

    def test_large_random_arrays(self):
        # Larger than Timsort's min_run and Introsort's insertion threshold, so every code path runs.
        rng = random.Random(42)
        for size in (33, 64, 65, 500, 2000):
            data = [rng.randint(-100, 100) for _ in range(size)]
            for sort in (s.timsort, s.introsort):
                with self.subTest(algorithm=sort.__name__, size=size):
                    self.assertEqual(sort(data), sorted(data))

    def test_timsort_merges_natural_runs(self):
        data = list(range(500)) + list(range(300, 0, -1)) + list(range(200)) + [7] * 100
        self.assertEqual(s.timsort(data), sorted(data))

    def test_timsort_stable_across_merges(self):
        rng = random.Random(7)
        items = [Item(rng.randint(0, 9), i) for i in range(1000)]
        expected = [x.tag for x in sorted(items, key=lambda x: x.key)]
        self.assertEqual([x.tag for x in s.timsort(items)], expected)

    def test_introsort_falls_back_to_heapsort(self):
        # All-equal input makes every Lomuto partition maximally unbalanced,
        # so the depth limit is reached and heapsort takes over.
        data = [5] * 3000 + [1, 9]
        self.assertEqual(s.introsort(data), sorted(data))

    def test_introsort_on_sorted_and_reversed(self):
        for data in (list(range(5000)), list(range(5000, 0, -1))):
            self.assertEqual(s.introsort(data), sorted(data))


class TestCategories(unittest.TestCase):

    def test_every_algorithm_has_exactly_one_category(self):
        categorized = [sort for sorts in s.CATEGORIES.values() for sort in sorts]
        self.assertEqual(sorted(f.__name__ for f in categorized), sorted(f.__name__ for f in ALL))


class TestRegressions(unittest.TestCase):

    def test_bubble_sorts_two_elements(self):
        self.assertEqual(s.bubble([2, 1]), [1, 2])

    def test_cocktail_always_returns_the_list(self):
        self.assertEqual(s.cocktail([13, 47, 7]), [7, 13, 47])

    def test_radix_with_negatives(self):
        self.assertEqual(s.radix([3, -1, -20, 7, 0, -5]), [-20, -5, -1, 0, 3, 7])

    def test_radix_with_large_integers(self):
        data = [10 ** 17 + 1, 10 ** 17, 1]
        self.assertEqual(s.radix(data), [1, 10 ** 17, 10 ** 17 + 1])

    def test_quick_on_large_sorted_input(self):
        data = list(range(5000))
        self.assertEqual(s.quick(data), data)


if __name__ == '__main__':
    unittest.main()
