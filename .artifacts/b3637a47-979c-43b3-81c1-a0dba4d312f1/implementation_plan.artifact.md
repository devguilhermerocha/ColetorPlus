# Corrigir Erros no EnderecoDao

O `EnderecoDao` apresenta erros de compilação porque as consultas SQL (Room) referenciam colunas e tabelas com nomes incorretos, divergindo das classes de modelo (`Endereco` e `ProdutoEndereco`).

## Problemas Identificados
1.  **Coluna Inexistente:** A classe `Endereco` usa o campo `descricao`, mas o DAO tenta ordenar e filtrar por `rua`.
2.  **Tabela Incorreta:** A tabela de junção está definida como `produto_endereco_ref` na entidade, mas o DAO tenta deletar da tabela `produto_endereco`.

## Propostas de Mudanças

### Data Layer

#### [MODIFY] [EnderecoDao.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/dao/EnderecoDao.java)
- Atualizar todas as ocorrências de `rua` para `descricao` nas queries SQL.
- Corrigir o nome da tabela no método `desenderecarProduto` de `produto_endereco` para `produto_endereco_ref`.

## Plano de Verificação

### Testes de Compilação
- Executar o comando `./gradlew :app:assembleDebug` para garantir que o Room consiga gerar as implementações do DAO sem erros.

### Verificação Manual
- Validar se a busca por ruas e o vínculo de produtos continuam funcionando após a correção dos nomes.
