# Arrumar o EnderecoDao e Consistência de Dados

O objetivo é corrigir inconsistências no `EnderecoDao` em relação aos nomes das tabelas e garantir que os tipos de dados das chaves estrangeiras coincidam com as chaves primárias (especialmente `Produto.id` que é `long`).

## User Review Required

> [!IMPORTANT]
> Identificamos que `Produto.id` é do tipo `long`, mas várias referências em DAOs e outras entidades (como `ProdutoEndereco` e `Validade`) usam `int`. Corrigiremos isso para evitar erros de compilação do Room e inconsistência de dados.

## Proposed Changes

### Data Models

#### [MODIFY] [ProdutoEndereco.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/model/ProdutoEndereco.java)
- Alterar o tipo de `produtoId` de `int` para `long`.

#### [MODIFY] [Validade.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/model/Validade.java)
- Alterar o tipo de `produtoId` de `int` para `long`.

#### [MODIFY] [ProdutoValidade.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/model/ProdutoValidade.java)
- Alterar o tipo de `produtoId` de `int` para `long`.

### DAOs

#### [MODIFY] [EnderecoDao.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/dao/EnderecoDao.java)
- Corrigir o nome da tabela `produto` para `produtos`.
- Corrigir o nome da tabela `produto_endereco` para `produto_endereco_ref`.
- Alterar o tipo do parâmetro `produtoId` de `int` para `long` no método `desenderecarProduto`.

#### [MODIFY] [ValidadeDao.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/dao/ValidadeDao.java)
- Alterar o tipo do parâmetro `produtoId` de `int` para `long` no método `buscarPorProduto`.

## Verification Plan

### Automated Tests
- Executar `gradle_build` para validar o processamento das anotações do Room e a compilação.
