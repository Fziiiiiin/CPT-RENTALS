# CPT rentals - Event Rentals CRM

**CPT rentals** is a comprehensive event rentals infrastructure management and customer relationship management (CRM) mobile application built for **CPT Events & Infrastructure** using native Android with **Jetpack Compose** and **Material Design 3**.

---

## 🌟 Key Features

### 1. Role-Based Staff Authentication
- **Admin Access (`admin` / `admin123`)**:
  - Full system control.
  - Add new master inventory items to categories.
  - Modify rental unit prices in-place.
  - Delete master stock items.
  - Delete any invoice records.
- **Warehouse Incharge Access (`warehouse` / `ware123`)**:
  - Operational dispatch & stock tracking.
  - View live stock across warehouse and on-site locations.
  - Generate invoices with automated stock deduction.
  - Log daily operational ledger expenses.
  - Restricted from editing master catalog prices or deleting bills with payments attached.
- **1-Tap Demo Quick-Fill**: Easy role switching between Admin and Warehouse on the login screen.

---

### 2. Live Inventory Management
- **Category Accordions**: Organized under *Tents & Structures*, *Furniture*, *Lighting*, *Power & Cooling*, *Audio / Visual*, and *Staging & Flooring*.
- **Real-Time Search**: Instant search filtering by item name or category.
- **Stock Equilibrium Metrics**:
  - **Total Stock**: Master quantity tracked.
  - **In Warehouse**: Live units ready for rental (Emerald status badge when available).
  - **On-Site Deployed**: Live units currently installed at event venues.
  - **Damaged / Missing**: Defective units under repair or flagged.
- **Master Item Management**: Admin modal form to add new products with custom initial quantity and rental unit price.

---

### 3. Rental Quoting & Invoicing with WhatsApp Sharing
- **Client Details**: Client name and WhatsApp contact number.
- **Dynamic Item Selector**: Real-time warehouse availability check per rental line item.
- **Damage & Penalty Records**: Add penalty lines for damaged or missing equipment.
- **Automated Stock Deduction**: Moves rented quantities from `Warehouse` to `On-Site` automatically upon booking.
- **Financial Balances**: Live computation of Subtotal, Damage Subtotal, Grand Total, Advances Paid, Previous Dues Cleared, and Balance Due.
- **One-Tap WhatsApp Share**: Automatically formats an invoice breakdown and opens WhatsApp with the client's phone number pre-filled.

---

### 4. Daily Closing Sheet & Cash Reconciliation
- **Cash Flow Summary**:
  - **Inflow**: Advances and dues collected today.
  - **Outflow**: Daily expenses and equipment purchases.
  - **Net Cash in Hand**: Live cash drawer balance including opening float (₹5,000).
- **Daily Expense Logging**: Fast entry for operational costs categorized by *Transport Fuel*, *Crew Food & Tea*, *Maintenance*, *Loading Labor*, or *Office Supplies*.
- **Live Stock Audit**: Real-time balance check (`Total == Warehouse + Site + Damaged`) displaying an audit shield badge.
- **Copy & Broadcast**: Copy the full daily summary report to the Android clipboard or broadcast via Android's native share sheet.

---

## 📱 Default Demo Credentials

| Role | Username | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **System Admin** | `admin` | `admin123` | Full access, master catalog, price updates, invoice management |
| **Warehouse Incharge** | `warehouse` | `ware123` | Operations, live stock viewing, bill dispatch, expense logging |

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0
- **UI Toolkit**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Reactive State**: Kotlin Coroutines & `StateFlow`
- **Design Tokens**: Custom CPT Midnight Indigo (`#1E1B4B`) & Royal Purple (`#7C3AED`) palette
- **Adaptive Icon**: Custom adaptive launcher icon with vector silhouette
- **Edge-to-Edge**: `enableEdgeToEdge()` with system navigation and status bar WindowInsets
- **Testing**: Robolectric JVM unit tests for critical user journeys

---

## 📂 Project Structure

```
app/src/main/java/com/example/
├── MainActivity.kt                 # Single Activity entry point with edge-to-edge support
├── model/
│   └── Models.kt                   # Domain entities (InventoryItem, Invoice, UserSession, etc.)
├── data/
│   └── CptRepository.kt            # Persistence layer with reactive StateFlows
├── viewmodel/
│   └── CptViewModel.kt             # UI state, form management, and WhatsApp share helpers
├── ui/
│   ├── components/
│   │   ├── CptTopBar.kt            # Midnight Indigo top bar with user role & notifications
│   │   ├── CptBottomNav.kt         # M3 4-tab bottom navigation bar
│   │   └── CptDialogs.kt           # Add Stock and Notification alert dialogs
│   ├── screens/
│   │   ├── LoginScreen.kt          # Staff authentication with 1-tap demo pills
│   │   ├── DashboardScreen.kt      # Live cash, inventory summary, and quick actions
│   │   ├── InventoryScreen.kt      # Categorized stock catalog with in-place price editing
│   │   ├── BillingScreen.kt        # Quotation generator with WhatsApp integration
│   │   └── ClosingScreen.kt        # Daily closing sheet & ledger reconciliation
│   └── theme/
│       ├── Color.kt                # Signature CPT color palette tokens
│       ├── Theme.kt                # MaterialTheme configuration
│       └── Type.kt                 # Typography definitions
```

---

## 🚀 Building & Testing

### Compile APK
```bash
gradle assembleDebug
```

### Run Unit Tests
```bash
gradle :app:testDebugUnitTest
```

---

## 📄 License
Internal Staff Application for CPT Events & Infrastructure.
