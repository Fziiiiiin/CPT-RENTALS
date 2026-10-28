import React, { useState } from 'react';
import { 
  Package, 
  Receipt, 
  Wallet, 
  BarChart3, 
  Plus, 
  Trash2, 
  MessageCircle, 
  CheckCircle, 
  AlertTriangle, 
  Check, 
  Copy,
  Lock,
  LogOut,
  User, 
  ShieldCheck,
  Search,
  ArrowRight,
  Bell,
  ChevronDown,
  ChevronRight
} from 'lucide-react';

export default function App() {
  // Authentication State
  const [user, setUser] = useState(null); // { role: 'admin' | 'warehouse', name: string }
  const [loginForm, setLoginForm] = useState({ username: 'admin', password: 'admin123' });
  const [authView, setAuthView] = useState('login'); // 'login' | 'forgot'
  const [forgotEmail, setForgotEmail] = useState('');

  // Navigation State (Mobile app tabs)
  const [activeTab, setActiveTab] = useState('dashboard');
  
  // Notification Modal State
  const [modal, setModal] = useState({ show: false, title: '', message: '', type: 'success' });

  // 1. Inventory State
  const [inventory, setInventory] = useState([
    { id: '1', name: 'Transparent German Tent (20x30)', category: 'Tents & Structures', total_qty: 10, in_warehouse: 6, on_site: 3, damaged: 1, unit_price: 15000 },
    { id: '2', name: 'White Canopy Tent (10x10)', category: 'Tents & Structures', total_qty: 25, in_warehouse: 20, on_site: 4, damaged: 1, unit_price: 2500 },
    { id: '3', name: 'Round Banquet Tables', category: 'Furniture', total_qty: 50, in_warehouse: 34, on_site: 15, damaged: 1, unit_price: 500 },
    { id: '4', name: 'Chiavari Chairs (Gold)', category: 'Furniture', total_qty: 300, in_warehouse: 215, on_site: 80, damaged: 5, unit_price: 100 },
    { id: '5', name: 'Heavy-Duty Generator (62KVA)', category: 'Power & Cooling', total_qty: 4, in_warehouse: 2, on_site: 2, damaged: 0, unit_price: 8000 },
    { id: '6', name: 'Halogen Floodlights (500W)', category: 'Lighting', total_qty: 40, in_warehouse: 28, on_site: 10, damaged: 2, unit_price: 350 }
  ]);

  const [searchQuery, setSearchQuery] = useState('');
  const [collapsedCategories, setCollapsedCategories] = useState({});

  // 2. Invoices State
  const [invoices, setInvoices] = useState([
    {
      id: 'inv-101',
      invoice_number: 'CPT-2026-001',
      client_name: 'Rahul Sharma',
      client_phone: '+919846012345',
      items: [
        { item_id: '1', name: 'Transparent German Tent (20x30)', qty: 3, price: 15000 }
      ],
      damaged_items: [
        { name: 'Chiavari Chairs (Gold)', qty: 2, penalty_price: 500 }
      ],
      subtotal: 45000,
      damage_subtotal: 1000,
      advance_paid: 15000,
      previous_due_collected: 0,
      balance_due: 31000,
      status: 'Active',
      date: new Date().toISOString().split('T')[0]
    }
  ]);

  // Billing Form State
  const [selectedClientName, setSelectedClientName] = useState('');
  const [selectedClientPhone, setSelectedClientPhone] = useState('');
  const [billingItems, setBillingItems] = useState([{ item_id: '', qty: 1, price: 0 }]);
  const [billingDamagedItems, setBillingDamagedItems] = useState([]);
  const [advancePaid, setAdvancePaid] = useState('');
  const [prevDueCollected, setPrevDueCollected] = useState('');

  // 3. Daily Ledger State
  const [transactions, setTransactions] = useState([
    { id: 't1', type: 'EXPENSE', category: 'Transport Fuel', amount: 2400, notes: 'Diesel for delivery truck 1', date: new Date().toISOString().split('T')[0] },
    { id: 't2', type: 'PURCHASE', category: 'Maintenance', amount: 1200, notes: 'Tent rope and stake replacements', date: new Date().toISOString().split('T')[0] }
  ]);

  const [expenseForm, setExpenseForm] = useState({ type: 'EXPENSE', category: 'Transport Fuel', amount: '', notes: '' });

  // Add Stock Modal State
  const [showAddStock, setShowAddStock] = useState(false);
  const [newStock, setNewStock] = useState({ name: '', category: 'Tents & Structures', total_qty: 1, unit_price: 1000 });

  const showAlert = (title, message, type = 'success') => {
    setModal({ show: true, title, message, type });
  };

  // --- Handlers ---

  const handleLogin = (e) => {
    e.preventDefault();
    const u = loginForm.username.trim().toLowerCase();
    const p = loginForm.password.trim();

    if (u === 'admin' && p === 'admin123') {
      setUser({ role: 'admin', name: 'System Admin' });
      showAlert('Welcome Admin', 'Logged in successfully with full system access.');
    } else if (u === 'warehouse' && p === 'ware123') {
      setUser({ role: 'warehouse', name: 'Warehouse Incharge' });
      showAlert('Welcome Warehouse Incharge', 'Logged in successfully with operational permissions.');
    } else {
      showAlert('Login Failed', 'Invalid credentials. Use admin / admin123 or warehouse / ware123', 'error');
    }
  };

  const handleForgotPassword = (e) => {
    e.preventDefault();
    if (!forgotEmail) {
      showAlert('Error', 'Please enter your registered username or email.', 'error');
      return;
    }
    showAlert('Password Reset Sent', `Password reset instructions sent to ${forgotEmail}.`);
    setAuthView('login');
    setForgotEmail('');
  };

  const handleLogout = () => {
    setUser(null);
    setLoginForm({ username: 'admin', password: 'admin123' });
  };

  const handleAddStock = (e) => {
    e.preventDefault();
    if (user?.role === 'warehouse') {
      showAlert('Access Denied', 'Warehouse Incharge is not permitted to add master inventory items.', 'error');
      return;
    }
    if (!newStock.name) return;
    const item = {
      id: Date.now().toString(),
      name: newStock.name,
      category: newStock.category,
      total_qty: Number(newStock.total_qty),
      in_warehouse: Number(newStock.total_qty),
      on_site: 0,
      damaged: 0,
      unit_price: Number(newStock.unit_price)
    };
    setInventory([...inventory, item]);
    setNewStock({ name: '', category: 'Tents & Structures', total_qty: 1, unit_price: 1000 });
    setShowAddStock(false);
    showAlert('Success', 'New stock item successfully added to inventory.');
  };

  const handlePriceChange = (id, newPrice) => {
    if (user?.role === 'warehouse') {
      showAlert('Restricted Action', 'Warehouse Incharge cannot modify fixed rental item prices.', 'error');
      return;
    }
    setInventory(inventory.map(item => item.id === id ? { ...item, unit_price: Number(newPrice) } : item));
  };
  
  const handleDeleteStock = (id) => {
    if (user?.role === 'warehouse') {
      showAlert('Restricted Action', 'Warehouse Incharge cannot delete master inventory items.', 'error');
      return;
    }
    setInventory(inventory.filter(item => item.id !== id));
    showAlert('Success', 'Item deleted from inventory.');
  };

  const handleDeleteInvoice = (inv) => {
    // Warehouse Incharge cannot delete bills unless there are NO payments attached
    if (user?.role === 'warehouse' && (inv.advance_paid > 0 || inv.previous_due_collected > 0)) {
      showAlert('Restricted Action', 'Warehouse Incharge cannot delete bills with financial payments attached.', 'error');
      return;
    }
    
    // Logic to revert stock could be added here, but keeping it simple for UI demo
    setInvoices(invoices.filter(i => i.id !== inv.id));
    showAlert('Invoice Deleted', `Invoice #${inv.invoice_number} successfully removed.`);
  };

  const toggleCategoryCollapse = (category) => {
    setCollapsedCategories(prev => ({
      ...prev,
      [category]: !prev[category]
    }));
  };

  // --- Billing Logic ---
  
  const handleBillingItemChange = (index, field, value) => {
    const updated = [...billingItems];
    if (field === 'item_id') {
      const found = inventory.find(i => i.id === value);
      updated[index].item_id = value;
      updated[index].name = found ? found.name : '';
      updated[index].price = found ? found.unit_price : 0;
    } else {
      updated[index][field] = Number(value);
    }
    setBillingItems(updated);
  };

  const addBillingRow = () => setBillingItems([...billingItems, { item_id: '', qty: 1, price: 0 }]);
  const removeBillingRow = (index) => setBillingItems(billingItems.filter((_, i) => i !== index));

  const addDamagedBillingRow = () => setBillingDamagedItems([...billingDamagedItems, { name: '', qty: 1, penalty_price: 250 }]);
  const handleDamagedRowChange = (index, field, value) => {
    const updated = [...billingDamagedItems];
    updated[index][field] = field === 'qty' || field === 'penalty_price' ? Number(value) : value;
    setBillingDamagedItems(updated);
  };
  const removeDamagedRow = (index) => setBillingDamagedItems(billingDamagedItems.filter((_, i) => i !== index));

  const currentSubtotal = billingItems.reduce((acc, curr) => acc + (curr.qty * curr.price), 0);
  const currentDamageSubtotal = billingDamagedItems.reduce((acc, curr) => acc + (curr.qty * curr.penalty_price), 0);
  const currentGrandTotal = currentSubtotal + currentDamageSubtotal;
  const currentBalanceDue = currentGrandTotal - (Number(advancePaid) || 0);

  const handleCreateBill = (e) => {
    e.preventDefault();
    if (!selectedClientName || !selectedClientPhone || billingItems.length === 0 || !billingItems[0].item_id) {
      showAlert('Error', 'Please fill in client details and select at least one rental item.', 'error');
      return;
    }

    // Verify stock availability
    for (const bi of billingItems) {
      const invItem = inventory.find(i => i.id === bi.item_id);
      if (!invItem || invItem.in_warehouse < bi.qty) {
        showAlert('Stock Error', `Insufficient stock in warehouse for ${invItem ? invItem.name : 'selected item'}. Available: ${invItem ? invItem.in_warehouse : 0}`, 'error');
        return;
      }
    }

    // Deduct from warehouse, add to on_site
    const updatedInventory = inventory.map(inv => {
      const matched = billingItems.find(bi => bi.item_id === inv.id);
      if (matched) {
        return {
          ...inv,
          in_warehouse: inv.in_warehouse - matched.qty,
          on_site: inv.on_site + matched.qty
        };
      }
      return inv;
    });
    setInventory(updatedInventory);

    const newInvoice = {
      id: 'inv-' + Date.now(),
      invoice_number: `CPT-${new Date().getFullYear()}-${String(invoices.length + 1).padStart(3, '0')}`,
      client_name: selectedClientName,
      client_phone: selectedClientPhone,
      items: billingItems.map(bi => ({ ...bi })),
      damaged_items: billingDamagedItems.map(di => ({ ...di })),
      subtotal: currentSubtotal,
      damage_subtotal: currentDamageSubtotal,
      advance_paid: Number(advancePaid) || 0,
      previous_due_collected: Number(prevDueCollected) || 0,
      balance_due: currentBalanceDue,
      status: 'Active',
      date: new Date().toISOString().split('T')[0]
    };

    setInvoices([newInvoice, ...invoices]);

    // Format WhatsApp Message
    let message = `*CPT EVENTS & INFRASTRUCTURE* \n----------------------------------\n*Invoice:* #${newInvoice.invoice_number}\n*Client:* ${newInvoice.client_name}\n*Date:* ${newInvoice.date}\n\n*Rental Items:*` +
      `${newInvoice.items.map(i => `\n- ${i.name} (x${i.qty}): ₹${i.qty * i.price}`).join('')}`;

    if (newInvoice.damaged_items.length > 0) {
      message += `\n\n*Damaged / Penalty Items:*` +
        `${newInvoice.damaged_items.map(d => `\n- ${d.name} (x${d.qty}): ₹${d.qty * d.penalty_price}`).join('')}`;
    }

    message += `\n----------------------------------\n*Rental Subtotal:* ₹${newInvoice.subtotal}\n*Damage Penalties:* ₹${newInvoice.damage_subtotal}\n*Grand Total:* ₹${newInvoice.subtotal + newInvoice.damage_subtotal}\n*Advance Paid:* ₹${newInvoice.advance_paid}\n*Previous Due Collected:* ₹${newInvoice.previous_due_collected}\n*Balance Due:* ₹${newInvoice.balance_due}\n\nThank you for choosing CPT Events! 🙏`;

    const cleanPhone = selectedClientPhone.replace(/\D/g, '');
    const waUrl = `https://wa.me/${cleanPhone}?text=${encodeURIComponent(message)}`;
    window.open(waUrl, '_blank');

    // Reset Form
    setSelectedClientName('');
    setSelectedClientPhone('');
    setBillingItems([{ item_id: '', qty: 1, price: 0 }]);
    setBillingDamagedItems([]);
    setAdvancePaid('');
    setPrevDueCollected('');

    showAlert('Success', 'Bill generated successfully! Warehouse stock updated & WhatsApp share triggered.');
    setActiveTab('dashboard');
  };

  const handleAddTransaction = (e) => {
    e.preventDefault();
    if (!expenseForm.amount || !expenseForm.category) {
      showAlert('Error', 'Please fill in category and amount.', 'error');
      return;
    }
    const newTx = {
      id: 't-' + Date.now(),
      type: expenseForm.type,
      category: expenseForm.category,
      amount: Number(expenseForm.amount),
      notes: expenseForm.notes || 'Daily entry',
      date: new Date().toISOString().split('T')[0]
    };
    setTransactions([newTx, ...transactions]);
    setExpenseForm({ type: 'EXPENSE', category: 'Transport Fuel', amount: '', notes: '' });
    showAlert('Success', 'Daily transaction recorded successfully.');
  };

  // --- Closing Data Aggregation ---
  const todayStr = new Date().toISOString().split('T')[0];
  const todayInvoices = invoices.filter(inv => inv.date === todayStr);
  
  const totalAdvancesToday = todayInvoices.reduce((sum, inv) => sum + inv.advance_paid, 0);
  const totalPrevDueCollectedToday = todayInvoices.reduce((sum, inv) => sum + inv.previous_due_collected, 0);
  const totalCashCollectedToday = totalAdvancesToday + totalPrevDueCollectedToday;

  const todayTransactions = transactions.filter(t => t.date === todayStr);
  const totalExpensesToday = todayTransactions.filter(t => t.type === 'EXPENSE').reduce((sum, t) => sum + t.amount, 0);
  const totalPurchasesToday = todayTransactions.filter(t => t.type === 'PURCHASE').reduce((sum, t) => sum + t.amount, 0);
  const totalOutflowToday = totalExpensesToday + totalPurchasesToday;

  const openingFloat = 5000; // Simulated opening cash drawer
  const netCashInHand = openingFloat + totalCashCollectedToday - totalOutflowToday;

  const stockAuditMismatches = inventory.filter(item => item.total_qty !== (item.in_warehouse + item.on_site + item.damaged));

  const copyClosingSheet = () => {
    const summary = `*CPT EVENTS & INFRASTRUCTURE*\n*DAILY CLOSING SHEET - ${todayStr}*\n----------------------------------\n💰 *CASH FLOW SUMMARY*\n- Opening Float: ₹${openingFloat}\n- Cash Collected Today (Advances): ₹${totalAdvancesToday}\n- Previous Dues Collected: ₹${totalPrevDueCollectedToday}\n- *Total Cash In:* ₹${totalCashCollectedToday}\n- Total Expenses: ₹${totalExpensesToday}\n- Total Purchases: ₹${totalPurchasesToday}\n- *Net Cash in Hand:* ₹${netCashInHand}\n\n📦 *STOCK RECONCILIATION*\n- Total Tracked Items: ${inventory.length} Categories\n- Status: ${stockAuditMismatches.length === 0 ? 'All Stock Balanced ✅' : 'Discrepancy Detected ⚠️'}\n\n----------------------------------\nReport generated via CPT CRM.`;

    navigator.clipboard.writeText(summary).then(() => {
      showAlert('Copied', 'Daily closing sheet copied to clipboard successfully!');
    }).catch(() => {
      showAlert('Warning', 'Could not copy automatically. Check your browser permissions.');
    });
  };

  // --- Prepare Categorized Inventory ---
  const filteredInventory = inventory.filter(item => item.name.toLowerCase().includes(searchQuery.toLowerCase()));
  const categorizedInventory = filteredInventory.reduce((acc, item) => {
    if (!acc[item.category]) acc[item.category] = [];
    acc[item.category].push(item);
    return acc;
  }, {});

  // ==========================================
  // RENDER: LOGIN SCREEN
  // ==========================================
  if (!user) {
    return (
      <div className="min-h-screen bg-[#F8FAFC] text-slate-700 font-sans flex items-center justify-center p-4 selection:bg-[#7C3AED] selection:text-white">
        <div className="w-full max-w-sm bg-white border border-[#E4E7EC] rounded-3xl p-8 shadow-2xl relative overflow-hidden">
          
          <div className="absolute -top-24 -right-24 w-48 h-48 bg-[#7C3AED]/10 rounded-full blur-3xl pointer-events-none"></div>

          <div className="flex flex-col items-center text-center mb-8">
            {/* Logo Image Integration */}
            <div className="w-full max-w-[200px] h-16 flex items-center justify-center mb-4">
               <img src="image.png" alt="Cee Pee Tee Events & Infrastructure" className="max-h-full object-contain" onError={(e) => {
                  e.target.style.display = 'none';
                  e.target.nextSibling.style.display = 'flex';
               }} />
               {/* Fallback if image fails to load */}
               <div className="hidden w-16 h-16 rounded-2xl bg-[#1E1B4B] items-center justify-center text-white font-black shadow-lg text-2xl">
                  CPT
               </div>
            </div>
            <h1 className="text-[#111827] font-bold text-xl tracking-tight mt-2">Staff Portal</h1>
            <p className="text-xs text-[#7C3AED] font-semibold tracking-wider uppercase mt-1">Event Rentals CRM</p>
          </div>

          {authView === 'login' ? (
            <form onSubmit={handleLogin} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-600 mb-1 uppercase tracking-wider">Username</label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                    <User className="w-4 h-4" />
                  </div>
                  <input 
                    type="text" 
                    required
                    placeholder="admin or warehouse"
                    value={loginForm.username}
                    onChange={e => setLoginForm({...loginForm, username: e.target.value})}
                    className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl pl-10 pr-4 py-3 text-slate-900 text-sm focus:outline-none focus:border-[#7C3AED] focus:ring-1 focus:ring-[#7C3AED] transition"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-600 mb-1 uppercase tracking-wider">Password</label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                    <Lock className="w-4 h-4" />
                  </div>
                  <input 
                    type="password" 
                    required
                    placeholder="••••••••"
                    value={loginForm.password}
                    onChange={e => setLoginForm({...loginForm, password: e.target.value})}
                    className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl pl-10 pr-4 py-3 text-slate-900 text-sm focus:outline-none focus:border-[#7C3AED] focus:ring-1 focus:ring-[#7C3AED] transition"
                  />
                </div>
              </div>

              <div className="flex justify-between items-center text-xs pt-1">
                <div className="text-slate-400 flex gap-2">
                  <button type="button" onClick={() => setLoginForm({username: 'admin', password: 'admin123'})} className="text-indigo-600 font-semibold hover:underline bg-indigo-50 px-2 py-1 rounded">Admin</button>
                  <button type="button" onClick={() => setLoginForm({username: 'warehouse', password: 'ware123'})} className="text-[#7C3AED] font-semibold hover:underline bg-purple-50 px-2 py-1 rounded">Warehouse</button>
                </div>
                <button 
                  type="button" 
                  onClick={() => setAuthView('forgot')}
                  className="text-[#7C3AED] hover:underline font-semibold"
                >
                  Forgot Password?
                </button>
              </div>

              <button 
                type="submit"
                className="w-full bg-[#7C3AED] hover:bg-[#6D28D9] text-white font-semibold py-3.5 rounded-xl text-sm transition shadow-lg shadow-[#7C3AED]/25 cursor-pointer mt-3"
              >
                Sign In
              </button>
            </form>
          ) : (
            <form onSubmit={handleForgotPassword} className="space-y-4">
              <div>
                <h3 className="text-[#111827] font-bold text-base mb-1">Reset Password</h3>
                <p className="text-xs text-slate-500 mb-3">Enter your username to receive reset instructions.</p>
                <label className="block text-xs font-semibold text-slate-600 mb-1 uppercase tracking-wider">Username</label>
                <input 
                  type="text" 
                  required
                  placeholder="e.g. admin"
                  value={forgotEmail}
                  onChange={e => setForgotEmail(e.target.value)}
                  className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-4 py-3 text-slate-900 text-sm focus:outline-none focus:border-[#7C3AED] transition"
                />
              </div>

              <div className="flex gap-2 pt-2">
                <button 
                  type="button" 
                  onClick={() => setAuthView('login')}
                  className="flex-1 bg-slate-100 hover:bg-slate-200 text-slate-700 font-medium py-3 rounded-xl text-sm transition"
                >
                  Back
                </button>
                <button 
                  type="submit"
                  className="flex-1 bg-[#7C3AED] hover:bg-[#6D28D9] text-white font-semibold py-3 rounded-xl text-sm transition shadow-md shadow-[#7C3AED]/20"
                >
                  Reset
                </button>
              </div>
            </form>
          )}

        </div>
      </div>
    );
  }

  // ==========================================
  // RENDER: MOBILE APP FRAME
  // ==========================================
  return (
    <div className="min-h-screen bg-[#F8FAFC] text-slate-700 font-sans antialiased flex items-center justify-center sm:p-4 selection:bg-[#7C3AED] selection:text-white">
      
      {/* Mobile App Frame Container */}
      <div className="w-full max-w-md bg-[#F8FAFC] sm:rounded-[40px] sm:border-[8px] sm:border-[#0F172A] h-screen sm:h-[860px] flex flex-col shadow-2xl overflow-hidden relative">
        
        {/* Top Header Bar - Deep Midnight Indigo */}
        <header className="bg-[#1E1B4B] text-white px-4 py-4 flex justify-between items-center shadow-md z-30 shrink-0">
          <div className="flex items-center gap-2">
             <div className="w-10 h-10 bg-white rounded-lg p-1 flex items-center justify-center shrink-0">
               <img src="image.png" alt="CPT Logo" className="max-h-full object-contain" onError={(e) => {
                  e.target.style.display = 'none';
                  e.target.nextSibling.style.display = 'flex';
               }} />
               {/* Fallback */}
               <div className="hidden text-[#1E1B4B] font-black text-xs">CPT</div>
             </div>
            <div>
              <h1 className="text-white font-bold text-sm tracking-tight leading-tight">CPT Rentals</h1>
              <p className="text-[10px] text-[#C4B5FD] uppercase font-semibold">{user.name}</p>
            </div>
          </div>

          <div className="flex items-center gap-1.5">
            <button className="p-2 rounded-xl bg-white/10 text-white hover:bg-white/20 transition relative">
              <Bell className="w-4 h-4" />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-emerald-400"></span>
            </button>
            <button 
              onClick={handleLogout}
              className="p-2 rounded-xl bg-red-500/20 text-red-300 hover:bg-red-500/30 transition"
              title="Logout"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        </header>

        {/* Scrollable Body Content */}
        <div className="flex-1 overflow-y-auto p-4 space-y-5 pb-24">
          
          {/* TAB 1: DASHBOARD */}
          {activeTab === 'dashboard' && (
            <div className="space-y-4 animate-in fade-in duration-200">
              
              {/* Greeting Card */}
              <div className="bg-gradient-to-br from-[#1E1B4B] to-[#312E81] text-white p-5 rounded-2xl shadow-sm relative overflow-hidden">
                <div className="absolute -right-6 -bottom-6 w-32 h-32 bg-[#7C3AED]/20 rounded-full blur-2xl pointer-events-none"></div>
                <span className="text-[10px] uppercase font-semibold text-[#C4B5FD] tracking-wider block mb-1">Welcome Back</span>
                <h2 className="text-xl font-bold mb-3">{user.name} 👋</h2>
                <div className="grid grid-cols-2 gap-2 pt-3 border-t border-indigo-900/60 text-xs">
                  <div>
                    <span className="text-indigo-300 block mb-0.5">Warehouse Stock</span>
                    <span className="text-white font-bold text-base">{inventory.reduce((a, c) => a + c.in_warehouse, 0)} <span className="text-[10px] font-normal text-indigo-300">Units</span></span>
                  </div>
                  <div>
                    <span className="text-indigo-300 block mb-0.5">Net Cash Today</span>
                    <span className="text-emerald-400 font-bold text-base">₹{netCashInHand}</span>
                  </div>
                </div>
              </div>

              {/* Quick Action Tiles */}
              <div className="grid grid-cols-2 gap-3">
                <button 
                  onClick={() => setActiveTab('billing')}
                  className="bg-white p-4 rounded-2xl border border-[#E4E7EC] shadow-sm hover:border-[#7C3AED] transition text-left group cursor-pointer"
                >
                  <div className="w-10 h-10 rounded-xl bg-[#7C3AED]/10 text-[#7C3AED] flex items-center justify-center mb-3 group-hover:bg-[#7C3AED] group-hover:text-white transition">
                    <Receipt className="w-5 h-5" />
                  </div>
                  <h3 className="font-bold text-[#111827] text-sm">New Rental Bill</h3>
                  <p className="text-[10px] text-slate-400 mt-0.5">Create quote & share</p>
                </button>

                <button 
                  onClick={() => setActiveTab('inventory')}
                  className="bg-white p-4 rounded-2xl border border-[#E4E7EC] shadow-sm hover:border-[#7C3AED] transition text-left group cursor-pointer"
                >
                  <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center mb-3 group-hover:bg-[#1E1B4B] group-hover:text-white transition">
                    <Package className="w-5 h-5" />
                  </div>
                  <h3 className="font-bold text-[#111827] text-sm">Live Inventory</h3>
                  <p className="text-[10px] text-slate-400 mt-0.5">Categorized stock</p>
                </button>
              </div>

              {/* Financial Card */}
              <div className="bg-white p-5 rounded-2xl border border-[#E4E7EC] shadow-sm space-y-3">
                <div className="flex justify-between items-center">
                  <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider">Today's Financial Summary</h3>
                  <span className="text-[10px] text-emerald-600 font-bold bg-emerald-50 px-2 py-0.5 rounded-full border border-emerald-100 flex items-center gap-1"><CheckCircle className="w-3 h-3"/> Live</span>
                </div>
                <div className="space-y-2.5">
                  <div className="flex justify-between items-center text-sm">
                    <span className="text-slate-500">Total Cash In (Advances):</span>
                    <span className="font-bold text-[#111827]">₹{totalCashCollectedToday}</span>
                  </div>
                  <div className="flex justify-between items-center text-sm">
                    <span className="text-slate-500">Total Expenses / Purchases:</span>
                    <span className="font-bold text-red-600">-₹{totalOutflowToday}</span>
                  </div>
                  <div className="flex justify-between items-center text-sm pt-3 border-t border-slate-100">
                    <span className="font-bold text-slate-900">Net Cash in Hand:</span>
                    <span className="font-bold text-[#7C3AED] text-lg">₹{netCashInHand}</span>
                  </div>
                </div>
              </div>

              {/* Recent Active Invoices */}
              <div className="bg-white p-5 rounded-2xl border border-[#E4E7EC] shadow-sm space-y-3">
                <div className="flex justify-between items-center mb-1">
                  <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider">Recent Active Rentals</h3>
                  <button onClick={() => setActiveTab('billing')} className="text-[11px] text-[#7C3AED] font-semibold flex items-center gap-1 hover:underline">
                    View All <ArrowRight className="w-3 h-3" />
                  </button>
                </div>

                <div className="space-y-2">
                  {invoices.slice(0, 3).map(inv => (
                    <div key={inv.id} className="bg-[#F8FAFC] p-3 rounded-xl border border-[#E4E7EC] flex justify-between items-center text-xs hover:border-[#C4B5FD] transition">
                      <div>
                        <span className="font-bold text-[#111827] block text-sm">{inv.client_name}</span>
                        <span className="text-[10px] text-slate-400 mt-0.5 block">{inv.invoice_number} • ₹{inv.balance_due} Due</span>
                      </div>
                      <button 
                        onClick={() => {
                          let msg = `*CPT EVENTS & INFRASTRUCTURE* \nInvoice: #${inv.invoice_number}\nClient: ${inv.client_name}\nBalance Due: ₹${inv.balance_due}`;
                          window.open(`https://wa.me/${inv.client_phone.replace(/\D/g, '')}?text=${encodeURIComponent(msg)}`, '_blank');
                        }}
                        className="p-2.5 rounded-xl bg-emerald-50 text-emerald-600 hover:bg-emerald-100 transition shadow-sm"
                      >
                        <MessageCircle className="w-4 h-4" />
                      </button>
                    </div>
                  ))}
                  {invoices.length === 0 && (
                    <div className="text-center py-4 text-xs text-slate-400">No recent invoices found.</div>
                  )}
                </div>
              </div>

            </div>
          )}

          {/* TAB 2: BILLING & PAYMENT */}
          {activeTab === 'billing' && (
            <div className="space-y-4 animate-in fade-in duration-200">
              
              <div className="bg-white p-5 rounded-2xl border border-[#E4E7EC] shadow-sm">
                <h2 className="text-base font-bold text-[#111827] mb-1">Create Rental Invoice</h2>
                <p className="text-xs text-slate-400 mb-5">Generate quote, add damage penalties & share via WhatsApp.</p>

                <form onSubmit={handleCreateBill} className="space-y-5">
                  
                  {/* Client Details */}
                  <div className="space-y-3">
                    <div>
                      <label className="block text-[10px] font-bold text-slate-500 mb-1.5 uppercase tracking-wider">Client Name</label>
                      <input 
                        type="text" 
                        required
                        placeholder="e.g. Rahul Sharma"
                        value={selectedClientName}
                        onChange={e => setSelectedClientName(e.target.value)}
                        className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-3.5 py-2.5 text-slate-900 text-sm focus:outline-none focus:border-[#7C3AED] focus:ring-1 focus:ring-[#7C3AED]"
                      />
                    </div>
                    <div>
                      <label className="block text-[10px] font-bold text-slate-500 mb-1.5 uppercase tracking-wider">WhatsApp Number</label>
                      <input 
                        type="text" 
                        required
                        placeholder="+919846012345"
                        value={selectedClientPhone}
                        onChange={e => setSelectedClientPhone(e.target.value)}
                        className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-3.5 py-2.5 text-slate-900 text-sm focus:outline-none focus:border-[#7C3AED] focus:ring-1 focus:ring-[#7C3AED]"
                      />
                    </div>
                  </div>

                  {/* Rental Items */}
                  <div className="bg-[#F8FAFC] p-3 -mx-3 sm:mx-0 sm:rounded-2xl sm:border border-[#E4E7EC]">
                    <div className="flex justify-between items-center mb-3">
                      <label className="text-[10px] font-bold text-slate-500 uppercase tracking-wider">Rental Items</label>
                      <button type="button" onClick={addBillingRow} className="text-xs text-[#7C3AED] font-semibold flex items-center gap-1 hover:underline">
                        <Plus className="w-3.5 h-3.5" /> Add Row
                      </button>
                    </div>

                    <div className="space-y-2">
                      {billingItems.map((bi, idx) => (
                        <div key={idx} className="flex flex-col sm:flex-row gap-2 bg-white p-2.5 rounded-xl border border-[#E4E7EC] shadow-sm relative">
                          <select 
                            required
                            value={bi.item_id}
                            onChange={e => handleBillingItemChange(idx, 'item_id', e.target.value)}
                            className="w-full sm:flex-1 bg-transparent border-none text-slate-900 text-xs focus:outline-none"
                          >
                            <option value="">-- Select Inventory Item --</option>
                            {Object.entries(categorizedInventory).map(([cat, items]) => (
                               <optgroup label={cat} key={cat}>
                                 {items.map(inv => (
                                    <option key={inv.id} value={inv.id}>
                                      {inv.name} (In Stock: {inv.in_warehouse} | ₹{inv.unit_price})
                                    </option>
                                  ))}
                               </optgroup>
                            ))}
                          </select>
                          
                          <div className="flex gap-2 items-center border-t sm:border-t-0 sm:border-l border-slate-100 pt-2 sm:pt-0 sm:pl-2">
                            <input 
                              type="number" 
                              min="1"
                              required
                              placeholder="Qty"
                              value={bi.qty}
                              onChange={e => handleBillingItemChange(idx, 'qty', e.target.value)}
                              className="w-16 bg-[#F8FAFC] border border-[#E4E7EC] rounded-lg px-2 py-1.5 text-slate-900 text-xs text-center focus:outline-none focus:border-[#7C3AED]"
                            />
                            {billingItems.length > 1 && (
                              <button type="button" onClick={() => removeBillingRow(idx)} className="text-red-500 p-1.5 hover:bg-red-50 rounded-lg">
                                <Trash2 className="w-4 h-4" />
                              </button>
                            )}
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Damaged Items Section */}
                  <div>
                    <div className="flex justify-between items-center mb-3">
                      <label className="text-[10px] font-bold text-amber-700 uppercase tracking-wider bg-amber-50 px-2 py-1 rounded">Damaged / Missing Penalties</label>
                      <button type="button" onClick={addDamagedBillingRow} className="text-xs text-amber-600 font-semibold flex items-center gap-1 hover:underline">
                        <Plus className="w-3.5 h-3.5" /> Add Damaged
                      </button>
                    </div>

                    {billingDamagedItems.length > 0 && (
                      <div className="space-y-2 mb-4">
                        {billingDamagedItems.map((di, idx) => (
                          <div key={idx} className="flex gap-2 items-center bg-amber-50/50 p-2.5 rounded-xl border border-amber-200">
                            <input 
                              type="text"
                              required
                              placeholder="Item name"
                              value={di.name}
                              onChange={e => handleDamagedRowChange(idx, 'name', e.target.value)}
                              className="flex-1 w-0 bg-white border border-amber-200 rounded-lg px-2 py-2 text-slate-900 text-xs focus:outline-none"
                            />
                            <input 
                              type="number" 
                              min="1"
                              required
                              placeholder="Qty"
                              value={di.qty}
                              onChange={e => handleDamagedRowChange(idx, 'qty', e.target.value)}
                              className="w-14 bg-white border border-amber-200 rounded-lg px-2 py-2 text-xs text-center focus:outline-none"
                            />
                            <input 
                              type="number" 
                              min="0"
                              required
                              placeholder="Penalty ₹"
                              value={di.penalty_price}
                              onChange={e => handleDamagedRowChange(idx, 'penalty_price', e.target.value)}
                              className="w-20 bg-white border border-amber-200 rounded-lg px-2 py-2 text-xs text-center focus:outline-none"
                            />
                            <button type="button" onClick={() => removeDamagedRow(idx)} className="text-red-500 p-1">
                              <Trash2 className="w-4 h-4" />
                            </button>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>

                  {/* Financials / Payments */}
                  <div className="grid grid-cols-2 gap-3 pt-2">
                    <div>
                      <label className="block text-[10px] font-bold text-slate-500 mb-1.5 uppercase tracking-wider">Advance Paid (₹)</label>
                      <input 
                        type="number" 
                        min="0"
                        placeholder="0"
                        value={advancePaid}
                        onChange={e => setAdvancePaid(e.target.value)}
                        className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-3 py-2 text-slate-900 text-sm focus:outline-none focus:border-[#7C3AED]"
                      />
                    </div>
                    <div>
                      <label className="block text-[10px] font-bold text-slate-500 mb-1.5 uppercase tracking-wider">Prev Due Cleared (₹)</label>
                      <input 
                        type="number" 
                        min="0"
                        placeholder="0"
                        value={prevDueCollected}
                        onChange={e => setPrevDueCollected(e.target.value)}
                        className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-3 py-2 text-slate-900 text-sm focus:outline-none focus:border-[#7C3AED]"
                      />
                    </div>
                  </div>

                  <div className="pt-5 border-t border-slate-100 flex justify-between items-end">
                    <div>
                      <span className="text-[10px] text-slate-400 block font-medium uppercase tracking-wider">Grand Total</span>
                      <span className="text-xl font-black text-[#111827]">₹{currentGrandTotal}</span>
                      <span className="text-[11px] text-[#7C3AED] block font-bold mt-0.5 bg-purple-50 px-2 py-0.5 rounded inline-block">Due: ₹{currentBalanceDue}</span>
                    </div>

                    <button 
                      type="submit"
                      className="bg-[#7C3AED] hover:bg-[#6D28D9] text-white font-semibold px-5 py-3.5 rounded-xl text-xs flex items-center gap-2 shadow-lg shadow-[#7C3AED]/25 transition cursor-pointer"
                    >
                      <MessageCircle className="w-4 h-4 fill-current" /> Save & Share
                    </button>
                  </div>

                </form>
              </div>

              {/* Existing Invoices List */}
              <div className="bg-white p-5 rounded-2xl border border-[#E4E7EC] shadow-sm space-y-3">
                <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider">All Quoted Bills</h3>
                <div className="space-y-2.5">
                  {invoices.map(inv => (
                    <div key={inv.id} className="bg-[#F8FAFC] p-3 rounded-xl border border-[#E4E7EC] text-xs space-y-2">
                      <div className="flex justify-between items-start">
                        <div>
                          <span className="font-bold text-[#111827] text-sm block">{inv.client_name}</span>
                          <span className="text-[10px] text-slate-500 block">{inv.invoice_number} • {inv.date}</span>
                        </div>
                        <span className="bg-purple-100 text-[#7C3AED] font-bold px-2 py-1 rounded text-[10px]">Due: ₹{inv.balance_due}</span>
                      </div>
                      
                      <div className="flex justify-end gap-2 pt-2 border-t border-slate-200/60">
                        <button 
                          onClick={() => {
                            let msg = `*CPT EVENTS & INFRASTRUCTURE* \nInvoice: #${inv.invoice_number}\nClient: ${inv.client_name}\nBalance Due: ₹${inv.balance_due}`;
                            window.open(`https://wa.me/${inv.client_phone.replace(/\D/g, '')}?text=${encodeURIComponent(msg)}`, '_blank');
                          }}
                          className="bg-emerald-50 text-emerald-600 px-3 py-1.5 rounded-lg font-semibold flex items-center gap-1.5 hover:bg-emerald-100 transition"
                        >
                          <MessageCircle className="w-3.5 h-3.5" /> Send WA
                        </button>
                        <button 
                          onClick={() => handleDeleteInvoice(inv)}
                          className="bg-red-50 text-red-600 px-3 py-1.5 rounded-lg hover:bg-red-100 transition flex items-center gap-1"
                          title="Delete bill"
                        >
                          <Trash2 className="w-3.5 h-3.5" /> {user.role === 'admin' ? 'Delete' : ''}
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

            </div>
          )}

          {/* TAB 3: INVENTORY / PRODUCT MANAGEMENT (Category Grouped) */}
          {activeTab === 'inventory' && (
            <div className="space-y-4 animate-in fade-in duration-200">
              
              <div className="flex justify-between items-center">
                <div>
                  <h2 className="text-base font-bold text-[#111827]">Live Stock Directory</h2>
                  <p className="text-xs text-slate-400">Organized by categories.</p>
                </div>
                {user.role === 'admin' && (
                  <button 
                    onClick={() => setShowAddStock(true)}
                    className="bg-[#1E1B4B] hover:bg-slate-800 text-white font-semibold px-3 py-2 rounded-xl text-xs flex items-center gap-1 shadow transition cursor-pointer"
                  >
                    <Plus className="w-3.5 h-3.5" /> Add Stock
                  </button>
                )}
              </div>

              {/* Search Bar */}
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400">
                  <Search className="w-4 h-4" />
                </div>
                <input 
                  type="text" 
                  placeholder="Search stock items..."
                  value={searchQuery}
                  onChange={e => setSearchQuery(e.target.value)}
                  className="w-full bg-white border border-[#E4E7EC] rounded-xl pl-10 pr-4 py-3 text-slate-900 text-sm focus:outline-none focus:border-[#7C3AED] shadow-sm"
                />
              </div>

              {/* Add Stock Modal Form */}
              {showAddStock && user.role === 'admin' && (
                <div className="bg-white border border-[#7C3AED] p-5 rounded-2xl shadow-lg space-y-4 relative">
                  <div className="absolute -top-3 left-4 bg-[#7C3AED] text-white text-[10px] font-bold uppercase px-2 py-0.5 rounded shadow">Admin Only</div>
                  <h3 className="text-sm font-bold text-[#111827]">Add New Master Item</h3>
                  <form onSubmit={handleAddStock} className="space-y-3">
                    <input 
                      type="text" 
                      required
                      placeholder="Item name (e.g. LED Wall)"
                      value={newStock.name}
                      onChange={e => setNewStock({...newStock, name: e.target.value})}
                      className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-3.5 py-2.5 text-sm text-slate-900 focus:outline-none focus:border-[#7C3AED]"
                    />
                    <select
                      value={newStock.category}
                      onChange={e => setNewStock({...newStock, category: e.target.value})}
                      className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-3.5 py-2.5 text-sm text-slate-900 focus:outline-none focus:border-[#7C3AED]"
                    >
                      <option value="Tents & Structures">Tents & Structures</option>
                      <option value="Furniture">Furniture</option>
                      <option value="Lighting">Lighting</option>
                      <option value="Power & Cooling">Power & Cooling</option>
                      <option value="Audio / Visual">Audio / Visual</option>
                      <option value="Staging & Flooring">Staging & Flooring</option>
                    </select>
                    <div className="grid grid-cols-2 gap-3">
                      <div>
                         <label className="block text-[10px] text-slate-500 font-bold mb-1">Total Qty</label>
                         <input 
                           type="number" 
                           min="1"
                           required
                           placeholder="Qty"
                           value={newStock.total_qty}
                           onChange={e => setNewStock({...newStock, total_qty: e.target.value})}
                           className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-3.5 py-2.5 text-sm text-slate-900"
                         />
                      </div>
                      <div>
                         <label className="block text-[10px] text-slate-500 font-bold mb-1">Unit Price (₹)</label>
                         <input 
                           type="number" 
                           min="0"
                           required
                           placeholder="Price ₹"
                           value={newStock.unit_price}
                           onChange={e => setNewStock({...newStock, unit_price: e.target.value})}
                           className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-3.5 py-2.5 text-sm text-slate-900"
                         />
                      </div>
                    </div>
                    <div className="flex justify-end gap-2 pt-2">
                      <button type="button" onClick={() => setShowAddStock(false)} className="px-4 py-2 text-xs font-semibold text-slate-500 hover:bg-slate-100 rounded-xl">Cancel</button>
                      <button type="submit" className="bg-[#7C3AED] text-white px-5 py-2 rounded-xl text-xs font-bold shadow-md">Save Item</button>
                    </div>
                  </form>
                </div>
              )}

              {/* Categorized Inventory Listing */}
              <div className="space-y-4">
                {Object.entries(categorizedInventory).length === 0 ? (
                  <div className="text-center py-8 text-slate-400 text-sm">No items found matching your search.</div>
                ) : (
                  Object.entries(categorizedInventory).map(([category, items]) => (
                    <div key={category} className="bg-white rounded-2xl border border-[#E4E7EC] shadow-sm overflow-hidden">
                      
                      {/* Category Header */}
                      <button 
                        onClick={() => toggleCategoryCollapse(category)}
                        className="w-full bg-slate-50 border-b border-[#E4E7EC] px-4 py-3 flex justify-between items-center hover:bg-slate-100 transition"
                      >
                        <div className="flex items-center gap-2">
                           <div className="w-6 h-6 rounded-md bg-[#1E1B4B]/10 flex items-center justify-center text-[#1E1B4B]">
                             <Package className="w-3.5 h-3.5" />
                           </div>
                           <h3 className="font-bold text-sm text-[#1E1B4B]">{category}</h3>
                           <span className="text-[10px] font-bold text-slate-400 bg-white px-2 py-0.5 rounded-full border border-slate-200">{items.length}</span>
                        </div>
                        {collapsedCategories[category] ? <ChevronRight className="w-4 h-4 text-slate-400" /> : <ChevronDown className="w-4 h-4 text-slate-400" />}
                      </button>

                      {/* Items List */}
                      {!collapsedCategories[category] && (
                        <div className="divide-y divide-slate-100">
                          {items.map(item => (
                            <div key={item.id} className="p-4 space-y-3">
                              <div className="flex justify-between items-start">
                                <div>
                                  <span className="font-bold text-[#111827] text-sm block">{item.name}</span>
                                </div>
                                {item.in_warehouse === 0 ? (
                                  <span className="px-2 py-1 rounded text-[9px] font-bold uppercase tracking-wider bg-red-50 text-red-600 border border-red-100 flex items-center gap-1">
                                    <AlertTriangle className="w-2.5 h-2.5" /> Deployed
                                  </span>
                                ) : (
                                  <span className="px-2 py-1 rounded text-[9px] font-bold uppercase tracking-wider bg-emerald-50 text-emerald-600 border border-emerald-100 flex items-center gap-1">
                                    <CheckCircle className="w-2.5 h-2.5" /> Available
                                  </span>
                                )}
                              </div>

                              <div className="grid grid-cols-3 bg-[#F8FAFC] p-2.5 rounded-xl text-center text-xs border border-slate-100">
                                <div>
                                  <span className="text-slate-500 block text-[10px] uppercase font-bold tracking-wider">Total</span>
                                  <span className="font-black text-[#111827]">{item.total_qty}</span>
                                </div>
                                <div>
                                  <span className="text-slate-500 block text-[10px] uppercase font-bold tracking-wider">Warehouse</span>
                                  <span className="font-black text-emerald-600">{item.in_warehouse}</span>
                                </div>
                                <div>
                                  <span className="text-slate-500 block text-[10px] uppercase font-bold tracking-wider">On-Site</span>
                                  <span className="font-black text-[#7C3AED]">{item.on_site}</span>
                                </div>
                              </div>

                              <div className="flex justify-between items-center pt-1 text-xs">
                                <span className="text-slate-500 font-semibold">Rental Price / Unit:</span>
                                {user.role === 'admin' ? (
                                  <div className="flex items-center gap-2">
                                    <span className="font-bold text-slate-800">₹</span>
                                    <input 
                                      type="number" 
                                      value={item.unit_price}
                                      onChange={e => handlePriceChange(item.id, e.target.value)}
                                      className="w-20 bg-[#F8FAFC] border border-[#E4E7EC] rounded-lg px-2 py-1.5 text-right font-mono font-bold text-slate-900 focus:outline-none focus:border-[#7C3AED]"
                                    />
                                    <button onClick={() => handleDeleteStock(item.id)} className="p-1.5 text-red-400 hover:text-red-600 hover:bg-red-50 rounded-md">
                                      <Trash2 className="w-3.5 h-3.5" />
                                    </button>
                                  </div>
                                ) : (
                                  <span className="font-black font-mono text-slate-800 text-sm">₹{item.unit_price}</span>
                                )}
                              </div>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  ))
                )}
              </div>

            </div>
          )}

          {/* TAB 4: DAILY CLOSING SHEET & LEDGER */}
          {activeTab === 'closing' && (
            <div className="space-y-4 animate-in fade-in duration-200">
              
              <div className="flex justify-between items-center">
                <div>
                  <h2 className="text-base font-bold text-[#111827]">Daily Closing Sheet</h2>
                  <p className="text-xs text-slate-400">Cash reconciliation & stock audit.</p>
                </div>
                <button 
                  onClick={copyClosingSheet}
                  className="bg-[#1E1B4B] hover:bg-slate-800 text-white font-semibold px-3 py-2 rounded-xl text-xs flex items-center gap-1.5 shadow transition cursor-pointer"
                >
                  <Copy className="w-3.5 h-3.5" /> Copy Report
                </button>
              </div>

              {/* Cash Summary Cards */}
              <div className="grid grid-cols-2 gap-3">
                <div className="bg-white p-4 rounded-2xl border border-[#E4E7EC] shadow-sm space-y-1">
                  <span className="text-[10px] text-emerald-600 uppercase font-black tracking-wider block mb-2"><span className="bg-emerald-50 px-1.5 py-0.5 rounded">Inflow</span></span>
                  <div className="text-xl font-black text-[#111827]">₹{totalCashCollectedToday}</div>
                  <p className="text-[10px] text-slate-500 font-medium leading-tight">Advances + Dues</p>
                </div>

                <div className="bg-white p-4 rounded-2xl border border-[#E4E7EC] shadow-sm space-y-1">
                   <span className="text-[10px] text-red-600 uppercase font-black tracking-wider block mb-2"><span className="bg-red-50 px-1.5 py-0.5 rounded">Outflow</span></span>
                  <div className="text-xl font-black text-[#111827]">-₹{totalOutflowToday}</div>
                  <p className="text-[10px] text-slate-500 font-medium leading-tight">Expenses & Purchases</p>
                </div>
              </div>

              <div className="bg-gradient-to-br from-[#7C3AED] to-[#5B21B6] text-white p-5 rounded-2xl shadow-md space-y-2 relative overflow-hidden">
                <div className="absolute top-0 right-0 p-4 opacity-20">
                   <Wallet className="w-16 h-16" />
                </div>
                <span className="text-[10px] text-[#E9D5FF] uppercase font-bold tracking-wider relative z-10">Net Cash in Hand</span>
                <div className="text-3xl font-black text-white relative z-10">₹{netCashInHand}</div>
                <p className="text-[10px] text-indigo-200 font-medium relative z-10">Includes Starting Float (₹{openingFloat})</p>
              </div>

              {/* Add Expense Form */}
              <div className="bg-white p-5 rounded-2xl border border-[#E4E7EC] shadow-sm space-y-4">
                <h3 className="text-xs font-bold text-[#111827] uppercase tracking-wider">Log Daily Expense</h3>
                <form onSubmit={handleAddTransaction} className="space-y-3">
                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <label className="block text-[10px] font-bold text-slate-500 mb-1 uppercase">Type</label>
                      <select 
                        value={expenseForm.type}
                        onChange={e => setExpenseForm({...expenseForm, type: e.target.value})}
                        className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-3 py-2.5 text-xs text-slate-900 focus:outline-none focus:border-[#7C3AED]"
                      >
                        <option value="EXPENSE">Expense</option>
                        <option value="PURCHASE">Purchase</option>
                      </select>
                    </div>
                    <div>
                      <label className="block text-[10px] font-bold text-slate-500 mb-1 uppercase">Category</label>
                      <select 
                        value={expenseForm.category}
                        onChange={e => setExpenseForm({...expenseForm, category: e.target.value})}
                        className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-3 py-2.5 text-xs text-slate-900 focus:outline-none focus:border-[#7C3AED]"
                      >
                        <option value="Transport Fuel">Transport Fuel</option>
                        <option value="Crew Food & Tea">Crew Food</option>
                        <option value="Maintenance">Maintenance</option>
                        <option value="Loading Labor">Labor Wages</option>
                        <option value="Office Supplies">Office Supplies</option>
                      </select>
                    </div>
                  </div>

                  <div className="grid grid-cols-5 gap-3">
                    <div className="col-span-2">
                      <label className="block text-[10px] font-bold text-slate-500 mb-1 uppercase">Amount (₹)</label>
                      <input 
                        type="number" 
                        min="1"
                        required
                        placeholder="0"
                        value={expenseForm.amount}
                        onChange={e => setExpenseForm({...expenseForm, amount: e.target.value})}
                        className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-3 py-2.5 text-xs text-slate-900 focus:outline-none focus:border-[#7C3AED]"
                      />
                    </div>
                    <div className="col-span-3">
                      <label className="block text-[10px] font-bold text-slate-500 mb-1 uppercase">Notes</label>
                      <input 
                        type="text" 
                        placeholder="Optional details..."
                        value={expenseForm.notes}
                        onChange={e => setExpenseForm({...expenseForm, notes: e.target.value})}
                        className="w-full bg-[#F8FAFC] border border-[#E4E7EC] rounded-xl px-3 py-2.5 text-xs text-slate-900 focus:outline-none focus:border-[#7C3AED]"
                      />
                    </div>
                  </div>

                  <button 
                    type="submit" 
                    className="w-full bg-white border-2 border-[#7C3AED] hover:bg-purple-50 text-[#7C3AED] font-bold py-2.5 rounded-xl text-xs transition cursor-pointer flex items-center justify-center gap-1"
                  >
                    <Plus className="w-3.5 h-3.5" /> Record Outflow
                  </button>
                </form>
              </div>

              {/* Stock Audit Verification */}
              <div className="bg-white p-5 rounded-2xl border border-[#E4E7EC] shadow-sm space-y-4">
                <div className="flex justify-between items-center">
                  <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider">Live Stock Audit</h3>
                  {stockAuditMismatches.length === 0 ? (
                    <span className="text-[10px] text-emerald-700 bg-emerald-100 px-2.5 py-1 rounded-full font-bold flex items-center gap-1 shadow-sm">
                      <ShieldCheck className="w-3.5 h-3.5" /> Balanced
                    </span>
                  ) : (
                    <span className="text-[10px] text-red-700 bg-red-100 px-2.5 py-1 rounded-full font-bold shadow-sm">Mismatch Detected</span>
                  )}
                </div>
                <div className="space-y-2 max-h-48 overflow-y-auto pr-1 custom-scrollbar">
                  {inventory.map(item => (
                    <div key={item.id} className="bg-[#F8FAFC] p-3 rounded-xl border border-[#E4E7EC] flex justify-between items-center text-xs">
                      <span className="font-bold text-slate-700 truncate max-w-[150px]">{item.name}</span>
                      <div className="text-[11px] text-slate-500 font-mono flex items-center gap-2">
                        <span>WH: <strong className="text-emerald-600">{item.in_warehouse}</strong></span>
                        <span className="text-slate-300">|</span>
                        <span>Site: <strong className="text-[#7C3AED]">{item.on_site}</strong></span>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

            </div>
          )}

        </div>

        {/* Bottom Navigation Bar */}
        <nav className="absolute bottom-0 inset-x-0 bg-white border-t border-[#E4E7EC] px-6 py-3 flex justify-between items-center z-40 shadow-[0_-4px_20px_rgba(0,0,0,0.05)]">
          {[
            { id: 'dashboard', label: 'Home', icon: BarChart3 },
            { id: 'billing', label: 'Billing', icon: Receipt },
            { id: 'inventory', label: 'Stock', icon: Package },
            { id: 'closing', label: 'Closing', icon: Wallet }
          ].map(tab => {
            const Icon = tab.icon;
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex flex-col items-center gap-1.5 transition cursor-pointer relative ${
                  isActive ? 'text-[#7C3AED]' : 'text-slate-400 hover:text-slate-600'
                }`}
              >
                {isActive && <span className="absolute -top-3 inset-x-1 h-0.5 bg-[#7C3AED] rounded-full"></span>}
                <Icon className={`w-5 h-5 ${isActive ? 'stroke-[2.5]' : 'stroke-[1.75]'}`} />
                <span className={`text-[10px] ${isActive ? 'font-bold' : 'font-medium'}`}>{tab.label}</span>
              </button>
            );
          })}
        </nav>

      </div>

      {/* Notification Modal */}
      {modal.show && (
        <div className="fixed inset-0 z-50 bg-[#0F172A]/40 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white border border-[#E4E7EC] rounded-2xl max-w-sm w-full p-6 shadow-2xl space-y-4 animate-in fade-in zoom-in duration-200">
            <div className="flex items-center gap-3">
              <div className={`w-10 h-10 rounded-xl flex items-center justify-center font-bold ${
                modal.type === 'error' ? 'bg-red-50 text-red-600 border border-red-100' : 'bg-purple-50 text-[#7C3AED] border border-purple-100'
              }`}>
                {modal.type === 'error' ? <AlertTriangle className="w-5 h-5" /> : <Check className="w-5 h-5" />}
              </div>
              <div>
                <h3 className="text-[#111827] font-bold text-base">{modal.title}</h3>
                <p className="text-[10px] text-slate-400 uppercase font-bold tracking-wider">CPT CRM Notice</p>
              </div>
            </div>
            
            <p className="text-sm text-slate-600 leading-relaxed font-medium">{modal.message}</p>
            
            <div className="flex justify-end pt-2">
              <button 
                onClick={() => setModal({ show: false, title: '', message: '', type: 'success' })}
                className="bg-[#1E1B4B] hover:bg-slate-800 text-white font-semibold px-5 py-2.5 rounded-xl text-xs transition shadow-md"
              >
                Understood
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Custom Scrollbar Styles for the lists if needed */}
      <style dangerouslySetInnerHTML={{__html: `
        .custom-scrollbar::-webkit-scrollbar { width: 4px; }
        .custom-scrollbar::-webkit-scrollbar-track { background: #f1f5f9; border-radius: 4px; }
        .custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
      `}} />
    </div>
  );
}