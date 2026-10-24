# unicid-ciencia-da-computacao

Coursework from the **Computer Science** degree (Ciência da Computação) at **UNICID — Universidade Cidade de São Paulo**, 2014–2018.

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

## License

Everything in this repository is under the [MIT License](LICENSE), with these exceptions:

- **`PLP-JavaCC-Compiller/`** follows Márcio Delamaro's book *Como Construir um Compilador Utilizando Ferramentas Java* (Novatec, 2004). The support files, the grammar and code generator built on them, and the examples in `exemplos/` derive from the book and remain under the author's and publisher's copyright; they are not covered by the MIT License. Only my own additions are: the X++ test programs in `testes/`, the regression tests in `test/` and `build.sh`. See the [compiler's README](PLP-JavaCC-Compiller/README.md#license).
- **Third-party libraries bundled in `toys_catalog/`** keep their own licenses: Bootstrap 3 and jQuery (MIT), jQuery bsValidate (MIT), highlight.js (BSD-3-Clause), JSTL (`jstl.jar`, `standard.jar`; Apache-2.0) and MySQL Connector/J (`mysql-connector-java-5.1.15-bin.jar`; GPL-2.0 with the FOSS License Exception).
- **`facial_recog_tcc/`** is a submodule pointing to [its own repository](https://github.com/pattersonrptr/facial_recog_tcc), which is under Apache-2.0.

