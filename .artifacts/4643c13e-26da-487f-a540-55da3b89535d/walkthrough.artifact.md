# Walkthrough - Ajuste Visual de Ícones e Scanner de Endereços

Realizei os ajustes visuais para garantir que os ícones de scanner estejam proporcionais às barras de busca e atualizei a iconografia da gestão de endereços.

## Alterações Realizadas

### Gestão de Endereços (Admin)
- **Troca de Ícone**: O botão de scanner na tela de endereços agora utiliza o ícone de **QR Code** (`@drawable/qrcode`), que é o padrão para identificação de locais no projeto.
- **Ajuste de Proporção**: Configurei o `padding="8dp"` e `scaleType="fitCenter"` no `ImageButton` para que o ícone fique centralizado e com tamanho harmonioso em relação à barra de busca de 48dp.
- **Detalhe Visual**: Adicionei um ícone de QR Code decorativo dentro do card de detalhes da rua selecionada, reforçando a identidade visual da funcionalidade.

### Consulta de Produtos (Geral)
- **Sincronização de Altura**: Ajustei a altura da barra de busca e do botão de scanner para `52dp`, garantindo que ambos fiquem perfeitamente alinhados.
- **Respiro Visual**: Aumentei o padding do ícone para evitar que ele toque as bordas, mantendo uma aparência moderna e limpa.

### Gestão de Produtos (Admin)
- **Consistência**: Mantive os ícones do `TextInputLayout` com as cores do tema, garantindo que a escala automática do Material Design mantenha a legibilidade.

## Como Verificar

1. Acesse **Gestão de Endereços** no Admin:
   - Observe o novo ícone de QR Code na barra de busca.
   - Selecione uma rua e veja o ícone azul de QR Code no card de detalhes.
2. Acesse a **Consulta de Produtos** (tela inicial do usuário):
   - Note que o botão de scanner agora tem a mesma altura da barra de texto e o ícone está bem centralizado.

> [!TIP]
> O uso do `scaleType="fitCenter"` com `padding` adequado garante que os ícones não percam a definição em telas com diferentes densidades de pixels.
