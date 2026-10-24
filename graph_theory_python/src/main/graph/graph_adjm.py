"""
Adjacency matrix
pro: rápida para grafos densos
pro: mais fácil para arestas com peso
con: mais espaço
"""


class Vertex:
    def __init__(self, name):
        self.name = name


class Graph:
    vertices = dict()
    edges = list()
    edges_indices = dict()

    def add_vertex(self, vertex):
        if isinstance(vertex, Vertex) and vertex.name not in self.vertices:
            self.vertices[vertex.name] = vertex
            for row in self.edges:
                row.append(float('inf'))
            self.edges.append([float('inf')] * (len(self.edges) + 1))
            self.edges_indices[vertex.name] = len(self.edges_indices)
            return True
        else:
            return False

    def add_edge(self, u, v, weight=float('inf')):
        if u in self.vertices and v in self.vertices:
            self.edges[self.edges_indices[u]][self.edges_indices[v]] = weight
            self.edges[self.edges_indices[v]][self.edges_indices[u]] = weight
            return True
        else:
            return False

    def __str__(self):

        matrix = '  '

        for v in sorted(self.edges_indices):
            matrix += v + ' '

        matrix += '\n'

        for v, i in sorted(self.edges_indices.items()):
            matrix += v + ' '

            for j in range(len(self.edges)):
                e = self.edges[i][j]
                matrix += (e if str(e) != 'inf' else '∞') + ' '

            matrix += '\n'

        return matrix
