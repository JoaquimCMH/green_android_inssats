package com.blockstream.compose.screens.inssats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlaylistAddCheck
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blockstream.common.models.inssats.PlannedWithdrawalViewModelAbstract
import com.blockstream.compose.utils.SetupScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PlannedWithdrawalScreen(viewModel: PlannedWithdrawalViewModelAbstract) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error, uiState.initiateResult, uiState.executeResult) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
        }
        uiState.initiateResult?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
        }
        uiState.executeResult?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearMessages()
        }
    }

    SetupScreen(viewModel = viewModel) {
        Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                VaultSummaryCard(balance = uiState.vaultBalance, label = uiState.vaultLabel)

                InitiateWithdrawalCard(
                    amount = uiState.initiateAmount,
                    destination = uiState.destinationPubKey,
                    signature = uiState.saverSignature,
                    onAmountChange = viewModel::updateAmountInput,
                    onDestinationChange = viewModel::updateDestination,
                    onSignatureChange = viewModel::updateSignature,
                    onSubmit = viewModel::onInitiate
                )

                ExecuteWithdrawalCard(
                    pendingAmount = uiState.pendingOperation?.amount,
                    pendingDestination = uiState.pendingOperation?.destination,
                    onExecute = { viewModel.onExecute() }
                )
            }
        }
    }
}

@Composable
private fun VaultSummaryCard(balance: Long, label: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Vault", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Balance", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${balance} sats", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun InitiateWithdrawalCard(
    amount: String,
    destination: String,
    signature: String,
    onAmountChange: (String) -> Unit,
    onDestinationChange: (String) -> Unit,
    onSignatureChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var urgent by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.PlaylistAddCheck, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Initiate Planned Withdrawal", style = MaterialTheme.typography.titleMedium)
            }

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Amount (sats)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = destination,
                onValueChange = onDestinationChange,
                label = { Text("Destination pubkey") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = signature,
                onValueChange = onSignatureChange,
                label = { Text("Saver signature") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Urgent request", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Flag this withdrawal as urgent",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = urgent,
                    onCheckedChange = { urgent = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
                )
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Request")
            }
        }
    }
}

@Composable
private fun ExecuteWithdrawalCard(
    pendingAmount: Long?,
    pendingDestination: String?,
    onExecute: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.QrCode2, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Execute Pending Operation", style = MaterialTheme.typography.titleMedium)
            }

            if (pendingAmount != null && pendingDestination != null) {
                Text("Amount: ${pendingAmount} sats", fontWeight = FontWeight.SemiBold)
                Text("Destination: ${pendingDestination}")
                Button(
                    onClick = onExecute,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Execute")
                }
            } else {
                Text(
                    text = "No pending operation. Initiate a withdrawal first.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

