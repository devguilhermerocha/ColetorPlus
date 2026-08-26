# Walkthrough - Uppercase Data Standardization (UPPER)

I have implemented a comprehensive standardization to ensure that all critical application data (Product Names, EANs, and Address Descriptions) are handled exclusively in **UPPERCASE**. This eliminates search failures caused by case sensitivity issues, such as the one encountered with "Rua Teste".

## Changes Made

### Model-Level Enforcement
- **`Produto.java`**: Updated the constructor and setters (`setNome`, `setCodigoEan`) to automatically convert incoming strings to uppercase.
- **`Endereco.java`**: Standardized the street description to always be stored in uppercase through its constructor and `setDescricao` method.

### UI Input & Search Standardization
I have updated all relevant fragments to ensure that any input from the keyboard or barcode scanner is trimmed and converted to uppercase before it is used for database queries:
- **`EntradaFragment.java`**: Standardized both product EANs and street codes.
- **`ConsultaProdutoFragment.java`**: Standardized the general search term.
- **`AjusteFragment.java`**: Standardized product lookup by EAN.
- **`AdminProductManagementFragment.java`**: Ensured new or edited products are saved with uppercase names and EANs.
- **`AdminInventoryFragment.java`**: Standardized street searches and creation.

## Verification Results

### Automated Tests
- [x] **Gradle Build**: Successfully compiled the project. All Room entity logic and fragment bindings are verified.

### Manual Verification Recommended
1. **The "Rua Teste" Fix**: Go to the **Entrada** screen and type "rua teste" (all lowercase). Verify it now successfully identifies the location as "RUA TESTE".
2. **Data Entry**: Create a new product in the Admin area using lowercase letters. Verify it is displayed in uppercase in the catalog.
3. **Query Consistency**: Search for a product using mixed case (e.g., "DeTeRgEnTe"). Verify it finds the record correctly.
