# Manter Efeito Laser apenas no Leitor de Código de Barras

O objetivo é diferenciar a interface do scanner: o leitor de QR Code (Rua/Endereço) permanecerá sem o quadrado e a linha, enquanto o leitor de Código de Barras (Produto) voltará a exibir esses elementos visuais.

## Mudanças Propostas

### UI/Scanner

#### [MODIFY] [ScannerHelper.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/ui/scanner/ScannerHelper.java)
- Adicionar um parâmetro extra `"HIDE_VIEWFINDER"` nas configurações do scanner.
- Definir como `true` para leitura de Endereço (QR Code).
- Definir como `false` para leitura de Produto (Código de Barras).

#### [MODIFY] [CaptureActivityPortrait.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/ui/scanner/CaptureActivityPortrait.java)
- Ler o parâmetro extra do `Intent`.
- Esconder o `ViewfinderView` condicionalmente, apenas se o parâmetro `"HIDE_VIEWFINDER"` for verdadeiro.

## Plano de Verificação

### Testes Manuais
- **Leitura de Rua (QR Code):** Verificar se a tela continua limpa (sem quadrado/linha).
- **Leitura de Produto (EAN):** Verificar se o quadrado e a linha laser (vermelha) voltaram a aparecer.
- Confirmar se ambos os leitores continuam processando os códigos corretamente.

## Perguntas Abertas
- Nenhuma no momento. A lógica de usar uma flag no Intent permite que a mesma atividade se comporte de formas diferentes conforme a necessidade.
