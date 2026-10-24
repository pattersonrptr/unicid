
from src.main.graph.graph_adjl import Graph


def test():

    graph = Graph('Grafo 1', is_directed=True)    # Cria o Grafo

    for i in range(ord('A'), ord('E') + 1):
        graph.add_vertex(chr(i))

    # edges = ['AB5', 'AC8', 'AD4', 'BE7', 'CF10', 'DE5', 'DF11', 'EG9', 'FG6']
    edges = ['AB5', 'BC5', 'CD5', 'DE5', 'EA5']

    for edge in edges:
        graph.add_edge(edge[0:1], edge[1:2], int(edge[2:]))

    graph.add_vertex('F')
    graph.add_vertex('G')
    graph.add_vertex('H')
    graph.add_vertex('I')
    graph.add_vertex('J')
    graph.add_vertex('K')
    graph.add_edge('A', weight=7)
    graph.add_edge('A', 'G', 10)
    graph.add_edge('A', 'G', 2)
    graph.add_edge('A', 'B', 2)
    graph.add_edge('G', 'A', 4)
    graph.add_edge('H', 'G', 5)
    graph.add_edge('G', 'F', 4)
    graph.add_edge('I', weight=4)
    graph.add_edge('J', 'D', 2)
    graph.add_edge('J', 'C', 2)
    graph.add_edge('C', 'J', 3)
    graph.add_edge('B', 'K', 3)

    print(graph.name + ': \n')
    print(graph)

    print('Degrees:', [(v, graph.degree(v)) for v in graph.vertices])
    print('Min degree:', graph.min_degree)
    print('Max degree:', graph.max_degree)
    print('Degree sequence:', graph.degree_sequence)
    print('Isolated:', graph.isolated_vertices)
    print('Connected:', graph.is_connected())
    print('Is degree sequence:', graph.is_degree_sequence(graph.degree_sequence))
    print('Diameter:', graph.diameter)
    print('Density: %.3f' % graph.density)
    print('Erdős–Gallai:', graph.erdoes_gallai(graph.degree_sequence))

    try:
        print(graph.dijkstra('A', 'E'))
    except Exception as e:
        print('Error:', e)

    print('Leaves:', list(graph.leaves))
    print('Sources:', list(graph.sources))
    print('Sinks:', list(graph.sinks))
    print('Pendants:', list(graph.pendants))


if __name__ == "__main__":
    test()



