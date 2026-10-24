#!/usr/bin/env bash
# Compila o compilador X++ sem alterar os fontes originais.
#
# Os fontes de 2017 misturam codificações: os arquivos que vieram do livro estão em
# ISO-8859-1 e os editados no Eclipse (Linux) estão em UTF-8. O javac aceita uma só
# codificação por vez, então os arquivos ISO-8859-1 são convertidos numa cópia em build/.
set -euo pipefail
cd "$(dirname "$0")"

rm -rf build
mkdir -p build/src build/classes

find src -name '*.java' | while read -r f; do
	dest="build/$f"
	mkdir -p "$(dirname "$dest")"
	if iconv -f UTF-8 -t UTF-8 "$f" > /dev/null 2>&1; then
		cp "$f" "$dest"
	else
		iconv -f ISO-8859-1 -t UTF-8 "$f" > "$dest"
	fi
done

javac -encoding UTF-8 -nowarn -d build/classes $(find build/src -name '*.java') 2>&1 | grep -v '^Note:' || true
test -f build/classes/parser/langX.class
echo "OK: build/classes"
