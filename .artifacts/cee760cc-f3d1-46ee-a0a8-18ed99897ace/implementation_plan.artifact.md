# Plan: Redefine Dashboard Alerts (Expiration & Zero Stock)

Refactor the "Recent Alerts" section in the Admin Dashboard to specifically track products expiring within one week and products with zero stock, prioritizing expirations.

## Proposed Changes

### 1. Data Models & DAOs

#### [MODIFY] [ValidadeDao.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/dao/ValidadeDao.java)
- Add a new query `getValidadesVencendoComProduto(long limitDate)` using a JOIN to retrieve expiration data along with product names and EANs.

#### [MODIFY] [ProdutoDao.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/dao/ProdutoDao.java)
- Add a new query `getProdutosSemEstoque()` to fetch all products where `quantidadeTotal <= 0`.

#### [NEW] `DashboardAlerta.java`
- A simple model class to unify both alert types.
- Fields: `titulo`, `subtitulo`, `tipo` (VENCIMENTO, ESTOQUE), `prioridade`.

### 2. UI Components

#### [NEW] `AlertaDashboardAdapter.java`
- A dedicated adapter for the dashboard alerts.
- **Visual Design**:
    - Expiration alerts will be highlighted in **Orange/Red** with a clock/warning icon.
    - Zero stock alerts will be highlighted in **Dark Gray/Red** with an empty icon.
- Uses a simplified version of the audit item layout.

### 3. Dashboard Logic

#### [MODIFY] [AdminDashboardFragment.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/ui/admin/AdminDashboardFragment.java)
- Update `carregarDados()`:
    1. Fetch products expiring in 7 days.
    2. Fetch products with 0 stock.
    3. Map both to `DashboardAlerta` objects.
    4. Sort the list: Expiration alerts first, then Zero Stock alerts.
    5. Update the `rvAlertasAdmin` with the new adapter.

## Verification Plan

### Manual Verification
1. **Expiration Alert**: Register a product with an expiration date 3 days from now. Verify it appears at the top of the dashboard.
2. **Zero Stock Alert**: Adjust a product's stock to 0. Verify it appears in the alerts list below expiration alerts.
3. **Priority Test**: Have both conditions active. Ensure the expiration alert is always listed above the zero stock alert.
4. **Empty State**: Clear all stock and dates. Verify the list handles no alerts gracefully.
