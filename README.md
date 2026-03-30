## Explicação do artigo / Problema estudado

O artigo investiga o equilíbrio fundamental entre o **custo de busca** e o **custo de atualização** (inserção/deleção) em Árvores Binárias de Busca (BSTs). O problema central reside na manutenção da altura da árvore: quanto menor a altura, mais rápida é a busca, porém mais caro é o processo de manter essa estrutura após alterações.

Tradicionalmente, a computação se dividia em dois paradigmas de "Rank-Balanced Trees":

* **Árvores AVL (Equilíbrio Rígido):** Utilizam um fator de equilíbrio estrito onde a diferença de altura entre as subárvores de qualquer nó é no máximo 1.
    * **Limitação Identificada:** Embora garantam a melhor altura da categoria (aproximadamente 1.44 log n), as deleções são ineficientes. Uma única remoção pode causar um "efeito cascata" de rebalanceamentos e rotações que se propagam da folha até a raiz da árvore (Theta(log n) no pior caso). Em sistemas com muitas atualizações, esse overhead é custoso.
* **Árvores Rubro-Negras (Equilíbrio Relaxado):** Utilizam regras baseadas em "cores" (que o artigo traduz matematicamente como ranks). Elas permitem um desequilíbrio maior, resultando em uma altura de até 2 log n.
    * **Vantagem/Limitação:** A grande vantagem é que o rebalanceamento é eficiente (custo amortizado constante O(1)), mas a árvore é "mais alta", o que torna a busca (operação mais comum em bancos de dados) ligeiramente mais lenta que na AVL.

### O Problema Estudado

Os autores identificaram que não existia uma estrutura que unificasse essas duas vantagens de forma dinâmica. A pergunta que motivou a pesquisa foi: *"É possível projetar uma estrutura que se comporte como uma AVL em cenários de inserção (mantendo altura baixa), mas que, ao sofrer deleções, não exija o custo de rebalanceamento excessivo das AVLs?"*

O artigo propõe as **WAVL (Weak AVL Trees)** para preencher essa lacuna. A ideia principal é usar uma hierarquia de **ranks** (números inteiros) para cada nó, definindo regras sobre as diferenças de rank entre pais e filhos. O diferencial está em permitir que, durante a deleção, a árvore "relaxe" as restrições de rank (criando gaps maiores), o que interrompe a propagação de rotações, mantendo a eficiência das Rubro-Negras sem perder a boa altura das AVLs.
