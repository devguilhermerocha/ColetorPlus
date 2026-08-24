# Plano de Implementação - Remoção de Usuários

Este plano detalha a implementação da funcionalidade de exclusão de usuários na tela de Gestão de Equipe, utilizando o ícone `remover.png`.

## Propostas de Mudanças

### [Banco de Dados]

#### [MODIFY] [UsuarioDao.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/dao/UsuarioDao.java)
- Adicionar o método `@Delete void deletar(Usuario usuario)` para permitir a remoção de registros da tabela de usuários.

### [UI / Layout]

#### [MODIFY] [item_usuario.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/item_usuario.xml)
- Adicionar um `ImageButton` no lado direito do card.
- Usar a imagem `@drawable/remover` como ícone.
- Configurar o tamanho (ex: 40dp) e remover o fundo padrão para um visual mais limpo.

### [Lógica de Negócio]

#### [MODIFY] [AdminUserManagementFragment.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/ui/admin/AdminUserManagementFragment.java)
- **Interface de Callback**: Criar uma interface (ou usar um listener) no `UsuarioAdapter` para notificar o fragmento sobre o clique de remoção.
- **Diálogo de Confirmação**: Implementar um `AlertDialog` que pergunta "Deseja realmente remover o usuário [Nome]?" antes de prosseguir.
- **Ação de Deletar**: Executar a remoção no banco de dados via `AppDatabase` em uma thread secundária.
- **Atualização da Lista**: Chamar `carregarUsuarios()` após a remoção bem-sucedida.

## Plano de Verificação

### Verificação Manual
1. Abrir a aba **Equipe**.
2. Localizar o botão de remover (ícone "-") no card de um usuário.
3. Clicar no botão e verificar se o diálogo de confirmação aparece.
4. Confirmar a exclusão e verificar se o usuário some da lista imediatamente.
5. Tentar logar com o usuário removido para garantir que ele não tem mais acesso.

> [!WARNING]
> Impediremos a remoção do usuário logado atualmente (ou do `admin` principal) para evitar que o administrador se "tranque" fora do sistema.
