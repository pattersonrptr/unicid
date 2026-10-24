# unicid-ciencia-da-computacao

Coursework from the **Computer Science** degree (Ciência da Computação) at **UNICID — Universidade Cidade de São Paulo**, 2016–2018.

## Projects

| Folder | Description | Language | Course |
|---|---|---|---|
| [`facial_recog_tcc/`](https://github.com/pattersonrptr/facial_recog_tcc) | **Undergraduate thesis.** Passwordless login with face recognition: FaceNet embeddings and one-shot learning with siamese networks | Python | Final thesis (TCC) |
| [`PLP-JavaCC-Compiller/`](PLP-JavaCC-Compiller/) | Complete compiler for the X++ language, built with JavaCC, down to JVM bytecode, with end-to-end tests | Java | Programming Languages Design |
| [`process_scheduler/`](process_scheduler/) | CPU scheduling simulator with 5 policies (FCFS, SJF, SRTF, RR, priority with aging), metrics, tests and a Swing interface | Java | Operating Systems |
| [`sorting_algorithms_python/`](sorting_algorithms_python/) | 15 sorting algorithms organized by category, with tests and a benchmark | Python | Data Structures · Analysis and Complexity of Algorithms |
| [`graph_theory_python/`](graph_theory_python/) | Graphs with adjacency lists and matrices: BFS, DFS, Dijkstra, connectivity, diameter, with tests | Python | Graph Theory |
| [`mealy-and-moore-machines/`](mealy-and-moore-machines/) | Mealy and Moore machine simulators | C | Formal Languages and Automata |
| [`toys_catalog/`](toys_catalog/) | Toy store catalog with storefront and administration: Servlets + JSP + JDBC, with stored procedures and a trigger in MySQL/MariaDB | Java | Web Applications |

Each folder has its own README.

## History

Each folder used to be a separate repository and was brought here with `git subtree`, keeping its original commits. `toys_catalog/` holds the 2017 version, before the Spring Boot rewrite.

The thesis stays in its own repository and is included here as a **git submodule**: the `facial_recog_tcc` folder points to a commit of [pattersonrptr/facial_recog_tcc](https://github.com/pattersonrptr/facial_recog_tcc). To clone with it:

```bash
git clone --recurse-submodules https://github.com/pattersonrptr/unicid-ciencia-da-computacao.git
```
