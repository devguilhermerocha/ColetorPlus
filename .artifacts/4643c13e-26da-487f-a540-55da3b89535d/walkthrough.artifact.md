# Walkthrough - Correção do EnderecoDao e Consistência de Dados

Realizei as correções necessárias no `EnderecoDao` e nos modelos de dados relacionados para garantir que os nomes das tabelas e os tipos das chaves estrangeiras estejam corretos.

## Alterações Realizadas

### Modelos de Dados
- **[ProdutoEndereco.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/model/ProdutoEndereco.java)**: O campo `produtoId` foi alterado de `int` para `long` para coincidir com o ID da entidade `Produto`.
- **[Validade.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/model/Validade.java)**: O campo `produtoId` foi alterado de `int` para `long`.
- **[ProdutoValidade.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/model/ProdutoValidade.java)**: O campo `produtoId` foi alterado de `int` para `long`.

### DAOs
- **[EnderecoDao.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/dao/EnderecoDao.java)**:
    - Corrigido o nome da tabela de `produto` para `produtos`.
    - Corrigido o nome da tabela de `produto_endereco` para `produto_endereco_ref`.
    - Corrigido o nome da coluna de `produtoId` para `id` na tabela `produtos` dentro da query de `buscarProdutosPorEndereco`.
    - Alterado o tipo do parâmetro `produtoId` para `long`.
- **[ValidadeDao.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/dao/ValidadeDao.java)**:
    - Alterado o tipo do parâmetro `produtoId` para `long` no método `buscarPorProduto`.

## Verificação

- A compilação do projeto foi executada com sucesso via Gradle (`app:assembleDebug`), confirmando que o Room processou as anotações corretamente e os tipos estão consistentes.

> [!NOTE]
> Como o tipo da chave primária em `Produto` é `long`, é essencial que todas as referências externas (Foreign Keys) também utilizem `long` para evitar comportamentos inesperados no banco de dados SQLite.
