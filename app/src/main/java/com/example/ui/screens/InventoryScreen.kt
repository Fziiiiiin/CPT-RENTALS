package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InventoryItem
import com.example.model.UserRole
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.MidnightIndigo
import com.example.ui.theme.RoseError
import com.example.ui.theme.RoseLight
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.CptViewModel

@Composable
fun InventoryScreen(
    viewModel: CptViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val collapsedCategories by viewModel.collapsedCategories.collectAsState()

    val filteredItems = remember(inventory, searchQuery) {
        if (searchQuery.isBlank()) {
            inventory
        } else {
            inventory.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val groupedItems = remember(filteredItems) {
        filteredItems.groupBy { it.category }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Screen Header & Add Stock Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Live Stock Directory",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Real-time stock by categories",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                if (user?.role == UserRole.ADMIN) {
                    Button(
                        onClick = { viewModel.openAddStockDialog() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MidnightIndigo),
                        modifier = Modifier.testTag("inventory_add_stock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Stock", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search stock items...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoyalPurple,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inventory_search_input")
            )
        }

        if (groupedItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No stock items match your search.",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            groupedItems.forEach { (category, items) ->
                val isCollapsed = collapsedCategories.contains(category)

                item(key = category) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Category Header Button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC))
                                    .clickable { viewModel.toggleCategoryCollapse(category) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MidnightIndigo.copy(alpha = 0.08f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Inventory2,
                                            contentDescription = null,
                                            tint = MidnightIndigo,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Text(
                                        text = category,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MidnightIndigo
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color.White,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                                    ) {
                                        Text(
                                            text = "${items.size}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (isCollapsed) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                                    contentDescription = if (isCollapsed) "Expand" else "Collapse",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Items List inside Category
                            AnimatedVisibility(visible = !isCollapsed) {
                                Column {
                                    items.forEachIndexed { index, item ->
                                        if (index > 0) {
                                            HorizontalDivider(color = Color(0xFFF1F5F9))
                                        }
                                        InventoryItemRow(
                                            item = item,
                                            isAdmin = user?.role == UserRole.ADMIN,
                                            onPriceChanged = { newPrice ->
                                                viewModel.updateItemUnitPrice(item.id, newPrice)
                                            },
                                            onDelete = {
                                                viewModel.deleteItem(item.id)
                                            }
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

@Composable
fun InventoryItemRow(
    item: InventoryItem,
    isAdmin: Boolean,
    onPriceChanged: (Double) -> Unit,
    onDelete: () -> Unit
) {
    var priceInput by remember(item.id) { mutableStateOf("%.0f".format(item.unitPrice)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Name & Stock status badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            if (item.inWarehouse > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldLight)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Available",
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Available",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RoseLight)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Deployed",
                        tint = RoseError,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Deployed",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoseError
                    )
                }
            }
        }

        // Metrics Grid (Total, In Warehouse, On-Site)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = BackgroundLight,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TOTAL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${item.totalQty}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "WAREHOUSE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${item.inWarehouse}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldSuccess
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ON-SITE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${item.onSite}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RoyalPurple
                    )
                }
            }
        }

        // Price Row & Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rental Price / Unit:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            if (isAdmin) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("₹", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { input ->
                            priceInput = input
                            val num = input.toDoubleOrNull()
                            if (num != null && num >= 0) {
                                onPriceChanged(num)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val num = priceInput.toDoubleOrNull() ?: item.unitPrice
                                onPriceChanged(num)
                            }
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalPurple,
                            unfocusedBorderColor = BorderSubtle,
                            focusedContainerColor = BackgroundLight,
                            unfocusedContainerColor = BackgroundLight
                        ),
                        modifier = Modifier
                            .width(84.dp)
                            .height(44.dp)
                            .testTag("inventory_price_input_${item.id}")
                    )

                    IconButton(
                        onClick = {
                            val num = priceInput.toDoubleOrNull() ?: item.unitPrice
                            onPriceChanged(num)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldLight)
                            .testTag("inventory_save_price_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save price",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RoseLight)
                            .testTag("inventory_delete_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete item",
                            tint = RoseError,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = "₹${"%.0f".format(item.unitPrice)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = TextPrimary
                )
            }
        }
    }
}
