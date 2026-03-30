# Síntese Técnica - Projeto 1º GQ
## Artigo: Rank-Balanced Trees (Haeupler, Sen e Tarjan)
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
## Estrutura clássica avaliada

A estrutura clássica analisada é a **Árvore de Busca Binária Balanceada por Rank**. O artigo decompõe o funcionamento das AVLs e Rubro-Negras sob uma ótica unificada: cada nó **x** possui um rank inteiro **r(x)**, e o balanceamento é definido pela diferença de rank entre um nó pai (**p**) e seu filho (**c**), denotada por: 

**d(p, c) = r(p) - r(c)**

* **Na AVL clássica:** A regra é estrita. A diferença de rank **d(p, c)** deve ser sempre 1 ou 2, com a restrição adicional de que um nó não pode ter duas diferenças de rank de tamanho 2 (o que o artigo chama de nó 2,2). Isso força a árvore a ser extremamente densa, mas exige reestruturação constante para manter essa invariante após qualquer remoção.

* **Na Rubro-Negra clássica:** Os ranks são análogos às cores. As regras permitem diferenças de rank 0 (nó vermelho) ou 1 (nó preto), com restrições que impedem dois 0s seguidos. Isso dá flexibilidade, mas resulta em uma árvore potencialmente mais alta e menos eficiente para buscas puras.
## Contribuição principal / Ideia de solução

A solução proposta são as **WAVL Trees (Weak AVL Trees)**. A inovação central dos autores foi permitir a existência do **"nó 2,2"** durante as operações de deleção, algo que é estritamente proibido nas AVLs clássicas.

### Entendendo o "Nó 2,2"
Na estrutura de ranks, descrevemos um nó pela diferença de rank entre ele e os seus filhos (chamado de nó **i, j**).
* **Nó 1,1 ou 1,2:** São os estados padrão de uma árvore equilibrada.
* **Nó 2,2:** É um nó onde **ambos os filhos têm o rank 2 níveis abaixo do pai**. 

Enquanto a AVL clássica considera o nó 2,2 um erro que exige rotações imediatas para ser "consertado", a WAVL aceita este estado como um equilíbrio "fraco" (weak), mas suficiente.

### Funcionamento do Mecanismo:

* **Inserção "Forte":** Durante a inserção, a WAVL comporta-se exatamente como uma AVL. Se um nó tenta tornar-se 2,2, a árvore realiza rotações. Isso garante que a altura se mantenha excelente (**1.44 log n**).

* **Deleção "Fraca" (Weak):** Ao remover um nó, se a árvore detetar uma violação grave (diferença de rank 3), ela realiza um *demote* (redução do rank do pai). Se essa redução resultar num **nó 2,2**, a WAVL permite que a estrutura fique assim e interrompe o processo.

* **Interrupção da Propagação:** O nó 2,2 atua como um **"amortecedor"**. Na AVL, o desequilíbrio propagaria rotações em cascata até à raiz. Na WAVL, o nó 2,2 "absorve" o impacto da deleção e encerra o rebalanceamento ali mesmo.

**Resultado Técnico:** Esta flexibilidade permite que a WAVL tenha um custo de rebalanceamento constante (**O(1) amortizado**), unindo a velocidade de atualização das Rubro-Negras com a excelente altura de busca das AVLs.
## Contraexemplo: AVL vs. WAVL na Deleção

### Cenário de Inserção (Preparando a árvore)
Imagine a inserção da seguinte sequência de nós em ambas as árvores: 
`[50, 25, 75, 10, 30, 60, 80, 5, 15, 27, 35]`

Isso resultará em uma árvore com altura 4, onde o lado esquerdo é mais denso que o direito, mas ainda dentro dos limites de equilíbrio de uma AVL.

### O Problema (Operação de deleção)
O teste consiste em **deletar o nó 80** (um nó folha na extremidade direita).

#### 1. A Falha de Eficiência na AVL (O Caso Clássico)
Ao remover o nó **80**:
* O nó **75** perde altura (reduz de 1 para 0).
* O nó **50 (raiz)** detecta um desequilíbrio: sua subárvore esquerda tem altura 3 e a direita agora tem altura 1 (diferença de 2).
* **Ação:** A AVL é forçada a realizar uma rotação à direita no nó 50 para reestabelecer o equilíbrio.
* **O Problema:** Em árvores de grande escala, essa rotação no nível da raiz pode alterar o fator de equilíbrio de nós superiores (caso esta árvore fosse parte de uma estrutura maior), propagando o custo de rebalanceamento por todo o caminho até o topo (**Theta(log n)** operações).

#### 2. A Eficiência da WAVL (A Solução do Artigo)
Ao remover o nó **80** na WAVL:
* O nó **75** identifica que seu rank não atende mais às regras estritas, tornando-se um "nó 2,2" (visto anteriormente como um amortecedor).
* **Ação:** Em vez de disparar uma rotação imediata que afetaria a estrutura global, a WAVL executa apenas um **Rank-Demote** (redução do valor do rank do nó 75 e, se necessário, do 50).
* **O ponto chave:** A regra da WAVL é "fraca" (*Weak*). Ela permite que o nó 50 permaneça em uma configuração que a AVL consideraria intolerável, desde que os ranks respeitem as novas paridades permitidas pelo artigo.
* **Resultado:** O rebalanceamento é interrompido quase instantaneamente sem a necessidade de rotações em cascata. O esforço de atualização permanece constante (**O(1) amortizado**).
## Implementação
## Conclusão / Crítica Final

A contribuição das **WAVL Trees** prova que o equilíbrio estrito das AVLs é desnecessário para garantir uma busca eficiente. O artigo demonstra que, ao "relaxar" as regras de rank durante a deleção, é possível reduzir o custo de rebalanceamento de **Theta(log n)** (caso da AVL clássica) para **O(1) amortizado**, sem degradar a altura da árvore para os níveis de uma Rubro-Negra.

Enquanto a AVL mantém um fator de equilíbrio rígido que força sucessivas rotações, a WAVL utiliza a "folga" nos ranks (permitindo o **nó 2,2**) para interromper a propagação de reestruturação precocemente. Isso resolve o dilema clássico entre performance de busca (AVL) e performance de atualização (Rubro-Negra), provando que uma estrutura pode ser ótima em ambas as frentes.

## Opinião Técnica

A implementação da WAVL é uma evolução lógica das BSTs balanceadas. A principal vantagem não é apenas a altura menor que a de uma Rubro-Negra, mas o fato de que a lógica de inserção permanece idêntica à da AVL, facilitando a migração de sistemas que já utilizam essa estrutura, mas sofrem com o *overhead* de deleção. O artigo é bem-sucedido ao unificar essas propriedades sob a métrica de **ranks**, simplificando o que antes eram tratadas como regras de coloração ou altura independentes.
