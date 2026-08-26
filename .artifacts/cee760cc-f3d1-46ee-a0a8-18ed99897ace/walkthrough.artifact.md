# Walkthrough - Standardized Adjustment Screen with Mandatory Batch Selection

I have completed the standardization of the **Ajuste / Avaria** screen, ensuring it matches the visual identity of the rest of the application and enforcing stricter inventory management rules.

## Changes Made

### UI Standardization
- **Unified Search Bar**: Refactored the top search and scanner section to be identical to the **Consulta** screen. It now uses a gray background (`#F5F5F5`), proper padding, and consistent component heights (`52dp`).
- **Card Consistency**: Updated the product information card to match the elevation (`3dp`) and styling used in the **Consulta** results, providing a cohesive feel throughout the app.

### Strict Inventory Logic
- **Mandatory Batch Selection**: Registering an adjustment now strictly requires selecting a **Data do Lote (Validade)** from the dropdown.
- **Validation Feedbacks**:
    - If a user tries to confirm without selecting a batch, they receive a clear warning: "Selecione o lote primeiro!".
    - The hold-to-confirm mechanism (3 seconds) remains active and only proceeds if a valid batch is selected.
- **Data Integrity**: Subtractions are now explicitly tied to the selected lot, ensuring that expiration tracking remains accurate even during adjustments.

## Verification Results

### Automated Tests
- [x] **Gradle Build**: Successfully compiled. All view IDs and logic updates are verified.

### Manual Verification Recommended
1. **Visual Check**: Verify that the top bar in "Ajuste" and "Consulta" looks the same.
2. **Strictness Test**: Scan a product, enter a quantity, and hold the button for 3s **without selecting a batch**. Verify that no adjustment is made and the warning toast appears.
3. **Correct Flow**: Select a batch, hold for 3s, and verify the successful subtraction from that specific lot.
