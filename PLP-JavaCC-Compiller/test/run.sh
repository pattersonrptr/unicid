#!/usr/bin/env bash
# Testes de regressão do compilador X++.
#
# 1. Compila todos os programas .x (exemplos do livro, testes de 2017 e test/programs)
#    e compara as mensagens do compilador com test/expected/<nome>.out.
# 2. Se a variável JASMIN apontar para o jasmin.jar (Jasmin 2.4), monta os programas
#    completos, executa-os na JVM com uma entrada fixa e compara a saída com
#    test/expected/<nome>.run.
#
# Uso: test/run.sh            (só a etapa 1)
#      JASMIN=/caminho/jasmin.jar test/run.sh
#      test/run.sh --update   (regrava os arquivos esperados)
set -uo pipefail
cd "$(dirname "$0")/.."

./build.sh > /dev/null || exit 1
CLASSES="$PWD/build/classes"
SEP=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";"; CLASSES="$(cygpath -w "$CLASSES")";; esac
JAVA="java -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"
UPDATE=0; [ "${1:-}" = "--update" ] && UPDATE=1

work="build/test"
rm -rf "$work"; mkdir -p "$work"
ok=0; falhas=0

confere() { # confere <nome> <arquivo esperado> <saída obtida>
	if [ $UPDATE = 1 ]; then
		cp "$3" "$2"
	elif diff -u "$2" "$3" > "$work/diff.txt"; then
		ok=$((ok + 1)); echo "  ok    $1"
	else
		falhas=$((falhas + 1)); echo "  FALHA $1"; sed 's/^/        /' "$work/diff.txt"
	fi
}

echo "Compilação:"
for src in exemplos/*.x testes/*.x test/programs/*.x; do
	nome=$(basename "$src" .x)
	cp "$src" "$work/"
	# as duas primeiras linhas são o banner e o nome do arquivo
	(cd "$work" && $JAVA -cp "$CLASSES" parser.langX "$nome.x" 2>&1 | tail -n +3 | tr -d '\r') > "$work/$nome.out"
	confere "$nome.x" "test/expected/$nome.out" "$work/$nome.out"
done

if [ -n "${JASMIN:-}" ]; then
	echo "Execução (Jasmin: $JASMIN):"
	(cd "$work" && java -jar "$JASMIN" *.jas > jasmin.log 2>&1) || { echo "  FALHA ao montar os .jas"; cat "$work/jasmin.log"; exit 1; }
	# programa (classe principal) e entrada
	while read -r classe entrada; do
		printf "$entrada" | (cd "$work" && $JAVA -cp ".$SEP$CLASSES" "$classe" 2>&1 | tr -d '\r') > "$work/$classe.run"
		confere "$classe (execução)" "test/expected/$classe.run" "$work/$classe.run"
	done <<'PROGRAMAS'
bubblesort 5\n42\n7\n19\n3\n25\n
matriz 2\n2\n1\n2\n3\n4\n
arrays 
bintree 15\n3\n1990\n1\n1\n2000\n20\n7\n1985\n5\n5\n1995\n1\n1\n2000\n9\n9\n1999\n2\n2\n2002\n28\n2\n1980\n10\n10\n2010\n3\n3\n1993\n12\n12\n2012\n
PROGRAMAS
else
	echo "Execução: pulada (defina JASMIN=/caminho/jasmin.jar para montar e executar)"
fi

[ $UPDATE = 1 ] && { echo "Arquivos esperados atualizados."; exit 0; }
echo
echo "$ok testes, $falhas falhas"
[ $falhas = 0 ]
