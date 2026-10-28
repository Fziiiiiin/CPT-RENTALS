package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightIndigo
import com.example.ui.theme.RoseError
import com.example.ui.theme.RoseLight
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.RoyalPurpleDark
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CptViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClosingScreen(
    viewModel: CptViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val inventory by viewModel.inventory.collectAsState()
    val cashCollectedToday by viewModel.todayCashCollected.collectAsState()
    val outflowToday by viewModel.todayOutflow.collectAsState()
    val netCashInHand by viewModel.netCashInHand.collectAsState()
    val stockMismatches by viewModel.stockMismatches.collectAsState()

    val expenseType by viewModel.expenseType.collectAsState()
    val expenseCategory by viewModel.expenseCategory.collectAsState()
    val expenseAmount by viewModel.expenseAmount.collectAsState()
    val expenseNotes by viewModel.expenseNotes.collectAsState()

    var typeMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Transport Fuel",
        "Crew Food & Tea",
        "Maintenance",
        "Loading Labor",
        "Office Supplies"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Top Header & Share/Copy Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Closing Sheet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Cash reconciliation & stock audit",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { viewModel.copyClosingSheetToClipboard(context) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MidnightIndigo),
                        modifier = Modifier.testTag("closing_copy_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.shareClosingSheet(context) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                        modifier = Modifier.testTag("closing_share_report_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Cash Flow Metric Tiles (Inflow & Outflow)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Inflow
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "INFLOW",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldSuccess,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "₹${"%.0f".format(cashCollectedToday)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Advances + Dues",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Outflow
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(RoseLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OUTFLOW",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RoseError,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "-₹${"%.0f".format(outflowToday)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RoseError
                        )
                        Text(
                            text = "Expenses & Purchases",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Net Cash in Hand Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(RoyalPurple, RoyalPurpleDark)
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NET CASH IN HAND",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = SoftLavender
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹${"%.0f".format(netCashInHand)}",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Includes Starting Float (₹5,000)",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(56.dp)
                    )
                }
            }
        }

        // Log Daily Expense Form Card
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
                        text = "LOG DAILY EXPENSE / PURCHASE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = TextSecondary
                    )

                    // Type & Category Dropdowns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Type
                        ExposedDropdownMenuBox(
                            expanded = typeMenuExpanded,
                            onExpandedChange = { typeMenuExpanded = !typeMenuExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = expenseType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RoyalPurple,
                                    unfocusedBorderColor = BorderSubtle,
                                    focusedContainerColor = BackgroundLight,
                                    unfocusedContainerColor = BackgroundLight
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = typeMenuExpanded,
                                onDismissRequest = { typeMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("EXPENSE") },
                                    onClick = {
                                        viewModel.expenseType.value = "EXPENSE"
                                        typeMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("PURCHASE") },
                                    onClick = {
                                        viewModel.expenseType.value = "PURCHASE"
                                        typeMenuExpanded = false
                                    }
                                )
                            }
                        }

                        // Category
                        ExposedDropdownMenuBox(
                            expanded = categoryMenuExpanded,
                            onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded },
                            modifier = Modifier.weight(1.3f)
                        ) {
                            OutlinedTextField(
                                value = expenseCategory,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RoyalPurple,
                                    unfocusedBorderColor = BorderSubtle,
                                    focusedContainerColor = BackgroundLight,
                                    unfocusedContainerColor = BackgroundLight
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = categoryMenuExpanded,
                                onDismissRequest = { categoryMenuExpanded = false }
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat, fontSize = 12.sp) },
                                        onClick = {
                                            viewModel.expenseCategory.value = cat
                                            categoryMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Amount & Notes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = expenseAmount,
                            onValueChange = { viewModel.expenseAmount.value = it },
                            label = { Text("Amount (₹)") },
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
                                .testTag("closing_expense_amount_input")
                        )

                        OutlinedTextField(
                            value = expenseNotes,
                            onValueChange = { viewModel.expenseNotes.value = it },
                            label = { Text("Notes") },
                            placeholder = { Text("Details...") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalPurple,
                                unfocusedBorderColor = BorderSubtle,
                                focusedContainerColor = BackgroundLight,
                                unfocusedContainerColor = BackgroundLight
                            ),
                            modifier = Modifier
                                .weight(1.4f)
                                .testTag("closing_expense_notes_input")
                        )
                    }

                    Button(
                        onClick = { viewModel.addExpense() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("closing_record_expense_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record Outflow", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Live Stock Audit Card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE STOCK AUDIT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            color = TextSecondary
                        )

                        if (stockMismatches.isEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EmeraldLight)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Balanced",
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "All Balanced",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(RoseLight)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Mismatch",
                                    tint = RoseError,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Mismatch (${stockMismatches.size})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoseError
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        inventory.forEach { item ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BackgroundLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "WH: ${item.inWarehouse}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldSuccess,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text("|", fontSize = 11.sp, color = TextMuted)
                                        Text(
                                            text = "Site: ${item.onSite}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RoyalPurple,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
