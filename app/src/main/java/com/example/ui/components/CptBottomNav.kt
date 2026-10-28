package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppTab
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.RoyalPurple
import com.example.ui.theme.TextMuted

private data class NavItemData(
    val tab: AppTab,
    val icon: ImageVector,
    val label: String
)

@Composable
fun CptBottomNav(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItemData(AppTab.DASHBOARD, Icons.Default.BarChart, "Home"),
        NavItemData(AppTab.BILLING, Icons.Default.ReceiptLong, "Billing"),
        NavItemData(AppTab.INVENTORY, Icons.Default.Inventory2, "Stock"),
        NavItemData(AppTab.CLOSING, Icons.Default.AccountBalanceWallet, "Closing")
    )

    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
        ) {
            // Subtle top border line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderSubtle)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = selectedTab == item.tab
                    val interactionSource = remember { MutableInteractionSource() }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onTabSelected(item.tab) }
                            .padding(vertical = 4.dp, horizontal = 12.dp)
                            .testTag("nav_tab_${item.tab.name.lowercase()}")
                    ) {
                        // Top active indicator indicator dot/pill
                        Box(
                            modifier = Modifier
                                .size(width = 24.dp, height = 3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isSelected) RoyalPurple else Color.Transparent)
                        )

                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) RoyalPurple else TextMuted,
                            modifier = Modifier
                                .padding(top = 4.dp, bottom = 2.dp)
                                .size(22.dp)
                        )

                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) RoyalPurple else TextMuted
                        )
                    }
                }
            }
        }
    }
}
