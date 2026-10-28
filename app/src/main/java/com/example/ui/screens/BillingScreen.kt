package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InventoryItem
import com.example.model.Invoice
import com.example.model.UserRole
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PalePurpleBg
import com.example.ui.theme.RoseError
import com.example.ui.theme.RoseLight
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.BillingRowDraft
import com.example.viewmodel.CptViewModel
import com.example.viewmodel.DamageRowDraft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    viewModel: CptViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val invoices by viewModel.invoices.collectAsState()

    val clientName by viewModel.billingClientName.collectAsState()
    val clientPhone by viewModel.billingClientPhone.collectAsState()
    val billingRows by viewModel.billingItems.collectAsState()
    val damageRows by viewModel.damageItems.collectAsState()
    val advancePaid by viewModel.billingAdvancePaid.collectAsState()
    val prevDueCollected by viewModel.billingPrevDueCollected.collectAsState()

    val grandTotal by viewModel.grandTotal.collectAsState()
    val balanceDue by viewModel.balanceDue.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Form Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Create Rental Invoice",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Generate quote, add damage penalties & share via WhatsApp.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Client Name
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { viewModel.billingClientName.value = it },
                        label = { Text("Client Name") },
                        placeholder = { Text("e.g. Rahul Sharma") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalPurple,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = BackgroundLight,
                            unfocusedContainerColor = BackgroundLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("billing_client_name_input")
                    )

                    // Client Phone
                    OutlinedTextField(
                        value = clientPhone,
                        onValueChange = { viewModel.billingClientPhone.value = it },
                        label = { Text("WhatsApp Number") },
                        placeholder = { Text("+919846012345") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalPurple,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = BackgroundLight,
                            unfocusedContainerColor = BackgroundLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("billing_client_phone_input")
                    )

                    // Rental Items Sub-Section
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = BackgroundLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "RENTAL ITEMS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp,
                                    color = TextSecondary
                                )

                                TextButton(
                                    onClick = { viewModel.addBillingRow() },
                                    modifier = Modifier.testTag("billing_add_item_row_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Row",
                                        tint = RoyalPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Row", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RoyalPurple)
                                }
                            }

                            billingRows.forEach { rowDraft ->
                                BillingItemRowEditor(
                                    row = rowDraft,
                                    inventory = inventory,
                                    canRemove = billingRows.size > 1,
                                    onItemSelected = { itemId -> viewModel.updateBillingRowItem(rowDraft.draftId, itemId) },
                                    onQtyChanged = { qty -> viewModel.updateBillingRowQty(rowDraft.draftId, qty) },
                                    onRemove = { viewModel.removeBillingRow(rowDraft.draftId) }
                                )
                            }
                        }
                    }

                    // Damaged Penalties Sub-Section
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AmberLight)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "DAMAGED / MISSING PENALTIES",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.6.sp,
                                    color = Color(0xFFB45309)
                                )
                            }

                            TextButton(
                                onClick = { viewModel.addDamageRow() },
                                modifier = Modifier.testTag("billing_add_damaged_row_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Damaged",
                                    tint = AmberWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Damaged", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AmberWarning)
                            }
                        }

                        damageRows.forEach { damRow ->
                            DamageItemRowEditor(
                                row = damRow,
                                onNameChanged = { viewModel.updateDamageRow(damRow.draftId, name = it) },
                                onQtyChanged = { viewModel.updateDamageRow(damRow.draftId, qty = it) },
                                onPenaltyChanged = { viewModel.updateDamageRow(damRow.draftId, penalty = it) },
                                onRemove = { viewModel.removeDamageRow(damRow.draftId) }
                            )
                        }
                    }

                    // Advance & Previous Due
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = advancePaid,
                            onValueChange = { viewModel.billingAdvancePaid.value = it },
                            label = { Text("Advance Paid (₹)") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalPurple,
                                unfocusedBorderColor = BorderSubtle,
                                focusedContainerColor = BackgroundLight,
                                unfocusedContainerColor = BackgroundLight
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("billing_advance_input")
                        )

                        OutlinedTextField(
                            value = prevDueCollected,
                            onValueChange = { viewModel.billingPrevDueCollected.value = it },
                            label = { Text("Prev Due Cleared (₹)") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalPurple,
                                unfocusedBorderColor = BorderSubtle,
                                focusedContainerColor = BackgroundLight,
                                unfocusedContainerColor = BackgroundLight
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("billing_prev_due_input")
                        )
                    }

                    HorizontalDivider(color = BorderSubtle)

                    // Grand Total & Submit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GRAND TOTAL",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextSecondary,
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = "₹${"%.0f".format(grandTotal)}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(PalePurpleBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Due: ₹${"%.0f".format(balanceDue)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.createBill(context) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("billing_save_and_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "WhatsApp",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save & Share", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // All Quoted Bills List
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "ALL QUOTED BILLS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = TextSecondary
                    )

                    if (invoices.isEmpty()) {
                        Text(
                            text = "No invoices generated yet.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        invoices.forEach { inv ->
                            InvoiceItemCard(
                                invoice = inv,
                                userRole = user?.role ?: UserRole.WAREHOUSE,
                                onSendWhatsApp = { viewModel.shareInvoiceViaWhatsApp(context, inv) },
                                onDelete = { viewModel.deleteInvoice(inv) }
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingItemRowEditor(
    row: BillingRowDraft,
    inventory: List<InventoryItem>,
    canRemove: Boolean,
    onItemSelected: (String) -> Unit,
    onQtyChanged: (String) -> Unit,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = inventory.find { it.id == row.itemId }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Select Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedItem?.let { "${it.name} (In WH: ${it.inWarehouse} | ₹${"%.0f".format(it.unitPrice)})" } ?: "Select Inventory Item",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalPurple,
                        unfocusedBorderColor = BorderSubtle
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    inventory.groupBy { it.category }.forEach { (category, items) ->
                        Text(
                            text = category.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                        items.forEach { inv ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(inv.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            "In Stock: ${inv.inWarehouse} | ₹${"%.0f".format(inv.unitPrice)}",
                                            fontSize = 10.sp,
                                            color = EmeraldSuccess
                                        )
                                    }
                                },
                                onClick = {
                                    onItemSelected(inv.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Qty & Remove Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Qty:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = row.qty,
                        onValueChange = onQtyChanged,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalPurple,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier
                            .width(68.dp)
                            .height(44.dp)
                    )
                }

                if (selectedItem != null) {
                    val lineTotal = (row.qty.toIntOrNull() ?: 1) * selectedItem.unitPrice
                    Text(
                        text = "₹${"%.0f".format(lineTotal)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }

                if (canRemove) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RoseLight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Remove row",
                            tint = RoseError,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DamageItemRowEditor(
    row: DamageRowDraft,
    onNameChanged: (String) -> Unit,
    onQtyChanged: (String) -> Unit,
    onPenaltyChanged: (String) -> Unit,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = AmberLight.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedTextField(
                value = row.name,
                onValueChange = onNameChanged,
                placeholder = { Text("Damaged item", fontSize = 11.sp) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberWarning,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.weight(1.5f)
            )

            OutlinedTextField(
                value = row.qty,
                onValueChange = onQtyChanged,
                placeholder = { Text("Qty", fontSize = 10.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberWarning,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.width(56.dp)
            )

            OutlinedTextField(
                value = row.penaltyPrice,
                onValueChange = onPenaltyChanged,
                placeholder = { Text("₹", fontSize = 10.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberWarning,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.width(76.dp)
            )

            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RoseError, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun InvoiceItemCard(
    invoice: Invoice,
    userRole: UserRole,
    onSendWhatsApp: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = BackgroundLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = invoice.clientName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "${invoice.invoiceNumber} • ${invoice.date}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PalePurpleBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Due: ₹${"%.0f".format(invoice.balanceDue)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple
                    )
                }
            }

            // Items mini-summary
            Text(
                text = invoice.items.joinToString(", ") { "${it.name} (x${it.qty})" },
                fontSize = 11.sp,
                color = TextMuted,
                maxLines = 2
            )

            HorizontalDivider(color = BorderSubtle)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onSendWhatsApp,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldLight,
                        contentColor = EmeraldSuccess
                    ),
                    modifier = Modifier.testTag("invoice_card_whatsapp_${invoice.id}")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Send WA", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(RoseLight)
                        .testTag("invoice_card_delete_${invoice.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete invoice",
                        tint = RoseError,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
