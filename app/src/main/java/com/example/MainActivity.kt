package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.CptRepository
import com.example.model.AppTab
import com.example.ui.components.AddStockDialog
import com.example.ui.components.CptBottomNav
import com.example.ui.components.CptTopBar
import com.example.ui.components.NotificationDialog
import com.example.ui.screens.BillingScreen
import com.example.ui.screens.ClosingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.MidnightIndigo
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CptViewModel
import com.example.viewmodel.CptViewModelFactory

class MainActivity : ComponentActivity() {

    private val repository by lazy { CptRepository(applicationContext) }
    private val viewModel: CptViewModel by viewModels {
        CptViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                CptApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CptApp(viewModel: CptViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val notification by viewModel.notification.collectAsState()
    val showAddStockDialog by viewModel.showAddStockDialog.collectAsState()

    // Handle back button: if in a subtab, return to Dashboard
    if (currentUser != null && activeTab != AppTab.DASHBOARD) {
        BackHandler {
            viewModel.selectTab(AppTab.DASHBOARD)
        }
    }

    if (currentUser == null) {
        LoginScreen(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Scaffold(
            topBar = {
                Box(modifier = Modifier.background(MidnightIndigo).statusBarsPadding()) {
                    CptTopBar(
                        user = currentUser!!,
                        onLogoutClick = { viewModel.logout() },
                        onNotificationClick = {
                            viewModel.showNotification(
                                title = "System Alerts",
                                message = "All systems operational. Live stock tracking & WhatsApp billing ready."
                            )
                        }
                    )
                }
            },
            bottomBar = {
                CptBottomNav(
                    selectedTab = activeTab,
                    onTabSelected = { tab -> viewModel.selectTab(tab) }
                )
            },
            containerColor = BackgroundLight,
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Crossfade(
                targetState = activeTab,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    AppTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    AppTab.BILLING -> BillingScreen(viewModel = viewModel)
                    AppTab.INVENTORY -> InventoryScreen(viewModel = viewModel)
                    AppTab.CLOSING -> ClosingScreen(viewModel = viewModel)
                }
            }
        }
    }

    // Dialogs
    notification?.let { notif ->
        NotificationDialog(
            notification = notif,
            onDismiss = { viewModel.dismissNotification() }
        )
    }

    if (showAddStockDialog) {
        AddStockDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeAddStockDialog() }
        )
    }
}
