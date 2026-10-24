import unittest

from src.main.graph.graph_adjm import Graph, Vertex


class TestAdjacencyMatrix(unittest.TestCase):

    def test_edge_is_stored_symmetrically(self):
        g = Graph()
        for name in 'AB':
            g.add_vertex(Vertex(name))
        g.add_edge('A', 'B', 3)
        a, b = g.edges_indices['A'], g.edges_indices['B']
        self.assertEqual(g.edges[a][b], 3)
        self.assertEqual(g.edges[b][a], 3)

    def test_print_with_weights(self):
        # Regression: str(graph) raised TypeError for numeric weights.
        g = Graph()
        for name in 'BA':
            g.add_vertex(Vertex(name))
        g.add_edge('A', 'B', 3)
        self.assertEqual(str(g), '  A B \nA ∞ 3 \nB 3 ∞ \n')

    def test_instances_do_not_share_state(self):
        # Regression: vertices and edges were class attributes.
        g1, g2 = Graph(), Graph()
        g1.add_vertex(Vertex('A'))
        self.assertEqual(g2.vertices, {})


if __name__ == '__main__':
    unittest.main()
