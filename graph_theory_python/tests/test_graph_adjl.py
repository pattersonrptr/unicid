import math
import unittest

from src.main.graph.graph_adjl import Graph, Vertex


def build(edges, directed=False, extra_vertices=''):
    """Builds a graph from 'UV' or ('U', 'V', weight) items."""
    g = Graph(is_directed=directed)
    for e in edges:
        u, v = e[0], e[1]
        g.add_vertex(u)
        g.add_vertex(v)
    for v in extra_vertices:
        g.add_vertex(v)
    for e in edges:
        weight = e[2] if len(e) > 2 else 1
        g.add_edge(e[0], e[1], weight)
    return g


class TestBuilding(unittest.TestCase):

    def test_add_vertex_accepts_str_int_and_vertex(self):
        g = Graph()
        self.assertTrue(g.add_vertex('A'))
        self.assertTrue(g.add_vertex(1))
        self.assertTrue(g.add_vertex(Vertex('B')))
        self.assertEqual(sorted(g.vertices), ['1', 'A', 'B'])

    def test_add_vertex_rejects_duplicates(self):
        g = Graph()
        g.add_vertex('A')
        self.assertFalse(g.add_vertex('A'))

    def test_add_edge_requires_existing_vertices(self):
        g = Graph()
        g.add_vertex('A')
        self.assertFalse(g.add_edge('A', 'Z'))

    def test_undirected_edge_is_symmetric(self):
        g = build(['AB'])
        self.assertIn('B', g['A'].neighbors)
        self.assertIn('A', g['B'].neighbors)

    def test_directed_edge_has_direction(self):
        g = build(['AB'], directed=True)
        self.assertIn('B', g['A'].out_neighbors)
        self.assertIn('A', g['B'].in_neighbors)
        self.assertNotIn('A', g['B'].out_neighbors)

    def test_parallel_edges_keep_every_weight(self):
        g = build([('A', 'B', 3), ('A', 'B', 7)])
        self.assertEqual(g['A'].neighbors['B'], [3, 7])

    def test_rm_edge_and_rm_vertex(self):
        g = build(['AB', 'BC'])
        g.rm_edge('A', 'B')
        self.assertNotIn('B', g['A'].neighbors)
        g.rm_vertex('C')
        self.assertNotIn('C', g)
        self.assertNotIn('C', g['B'].neighbors)


class TestDegrees(unittest.TestCase):

    def test_degree_undirected(self):
        g = build(['AB', 'AC', 'AD'])
        self.assertEqual(g.degree('A'), 3)
        self.assertEqual(g.degree('B'), 1)

    def test_loop_counts_twice(self):
        g = build(['AA'])
        self.assertEqual(g.degree('A'), 2)

    def test_degree_directed_is_in_plus_out(self):
        g = build(['AB', 'CA'], directed=True)
        self.assertEqual(g.degree('A'), 2)

    def test_min_max_and_sequence(self):
        g = build(['AB', 'AC', 'AD', 'BC'])
        self.assertEqual(g.min_degree, 1)
        self.assertEqual(g.max_degree, 3)
        self.assertEqual(g.degree_sequence, (3, 2, 2, 1))

    def test_handshake_lemma(self):
        g = build(['AB', 'BC', 'CD', 'DA', 'AC'])
        self.assertEqual(sum(g.degree_sequence), 2 * 5)


class TestClassification(unittest.TestCase):

    def test_isolated_vertices(self):
        g = build(['AB'], extra_vertices='C')
        self.assertEqual(g.isolated_vertices, ['C'])

    def test_leaves(self):
        g = build(['AB', 'AC', 'AD'])
        self.assertEqual(sorted(g.leaves), ['B', 'C', 'D'])

    def test_sources_and_sinks(self):
        g = build(['AB', 'BC'], directed=True)
        self.assertEqual(list(g.sources), ['A'])
        self.assertEqual(list(g.sinks), ['C'])

    def test_pendants_point_to_their_neighbor(self):
        g = build(['AB', 'AC', 'AD'])
        self.assertEqual(sorted(g.pendants), [('B', 'A'), ('C', 'A'), ('D', 'A')])


class TestConnectivity(unittest.TestCase):

    def test_connected(self):
        self.assertTrue(build(['AB', 'BC']).is_connected())

    def test_disconnected(self):
        self.assertFalse(build(['AB'], extra_vertices='C').is_connected())

    def test_no_state_leaks_between_calls(self):
        # Regression: a mutable default argument made a disconnected graph
        # look connected after a previous call on another graph.
        self.assertTrue(build(['AB']).is_connected())
        self.assertFalse(build([], extra_vertices='AB').is_connected())

    def test_directed_uses_weak_connectivity(self):
        # A -> B <- C: no directed path from A to C, but weakly connected.
        self.assertTrue(build(['AB', 'CB'], directed=True).is_connected())


class TestMetrics(unittest.TestCase):

    def test_density_complete_undirected_is_one(self):
        self.assertEqual(build(['AB', 'AC', 'BC']).density, 1.0)

    def test_density_undirected(self):
        # 2 edges out of 3 possible
        self.assertAlmostEqual(build(['AB', 'BC']).density, 2 / 3)

    def test_density_directed(self):
        # 2 arcs out of 6 possible
        self.assertAlmostEqual(build(['AB', 'BC'], directed=True).density, 2 / 6)

    def test_density_ignores_loops(self):
        self.assertAlmostEqual(build(['AB', 'BC', 'AA']).density, 2 / 3)

    def test_diameter_counts_edges(self):
        self.assertEqual(build(['AB', 'BC', 'CD']).diameter, 3)

    def test_diameter_uses_shortest_paths(self):
        # The cycle A-B-C-D-A has diameter 2, not 3.
        self.assertEqual(build(['AB', 'BC', 'CD', 'DA']).diameter, 2)


class TestSearch(unittest.TestCase):

    def setUp(self):
        #   A - B - D
        #   |   |
        #   C - E
        self.g = build(['AB', 'AC', 'BD', 'BE', 'CE'])

    def test_bfs_finds_target(self):
        visited = self.g.i_bfs('A', 'D')
        self.assertEqual(visited[0], 'A')
        self.assertEqual(visited[-1], 'D')

    def test_recursive_bfs_visits_level_by_level(self):
        # r_bfs expands a whole level at a time, so it returns the level where the target is.
        self.assertEqual(self.g.r_bfs('A', 'D'), ['A', 'B', 'C', 'D', 'E'])

    def test_dfs_finds_target(self):
        for search in (self.g.i_dfs, self.g.r_dfs):
            self.assertIn('D', search('A', 'D'))

    def test_bfs_returns_none_when_unreachable(self):
        g = build(['AB'], extra_vertices='C')
        self.assertIsNone(g.i_bfs('A', 'C'))

    def test_all_paths(self):
        expected = [['A', 'B', 'E'], ['A', 'C', 'E']]
        self.assertEqual(sorted(self.g.bfs_paths('A', 'E')), expected)
        self.assertEqual(sorted(self.g.dfs_paths('A', 'E')), expected)

    def test_bfs_paths_yields_shortest_first(self):
        g = build(['AB', 'BC', 'AC'])
        self.assertEqual(next(g.bfs_paths('A', 'C')), ['A', 'C'])


class TestDijkstra(unittest.TestCase):

    def test_weighted_shortest_path(self):
        g = build([('A', 'B', 1), ('B', 'C', 1), ('A', 'C', 5)])
        self.assertEqual(g.dijkstra('A', 'C'), "Shortest path: ['A', 'B', 'C']\nWeight: 2.0")

    def test_uses_lightest_parallel_edge(self):
        g = build([('A', 'B', 9), ('A', 'B', 2)])
        self.assertTrue(g.dijkstra('A', 'B').endswith('Weight: 2.0'))

    def test_target_without_outgoing_edges(self):
        # Regression: a sink was reported as unreachable.
        g = build([('A', 'B', 4)], directed=True)
        self.assertTrue(g.dijkstra('A', 'B').endswith('Weight: 4.0'))

    def test_start_equals_target(self):
        g = build(['AB'])
        self.assertTrue(g.dijkstra('A', 'A').endswith('Weight: 0'))

    def test_unreachable_target_raises(self):
        g = build(['AB'], extra_vertices='C')
        with self.assertRaisesRegex(Exception, 'unreachable'):
            g.dijkstra('A', 'C')

    def test_unknown_vertex_raises(self):
        g = build(['AB'])
        with self.assertRaises(Exception):
            g.dijkstra('A', 'Z')


class TestTheorems(unittest.TestCase):

    def test_is_degree_sequence(self):
        self.assertTrue(Graph.is_degree_sequence((3, 2, 2, 1)))
        self.assertFalse(Graph.is_degree_sequence((1, 2, 3)))

    def test_erdoes_gallai_graphic_sequences(self):
        for seq in [(3, 3, 3, 3), (2, 2, 2), (3, 2, 2, 1), (0, 0)]:
            self.assertTrue(Graph.erdoes_gallai(seq), seq)

    def test_erdoes_gallai_non_graphic_sequences(self):
        for seq in [(3, 3, 1, 1), (3, 1), (4, 1, 1, 1), (2, 2, 1)]:
            self.assertFalse(Graph.erdoes_gallai(seq), seq)

    def test_erdoes_gallai_accepts_sequence_of_a_real_graph(self):
        g = build(['AB', 'BC', 'CD', 'DA', 'AC'])
        self.assertTrue(Graph.erdoes_gallai(g.degree_sequence))


if __name__ == '__main__':
    unittest.main()
