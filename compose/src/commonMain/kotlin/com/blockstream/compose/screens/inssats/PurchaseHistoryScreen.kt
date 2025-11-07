package com.blockstream.compose.screens.inssats

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blockstream.common.models.inssats.PurchaseHistoryViewModelAbstract
import com.blockstream.compose.utils.SetupScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// -------------------- JSON (Deposits / Purchases) --------------------
private val PURCHASES_JSON = """
{
  "purchases": [
    { "date": "2024-01-15", "time": "09:42", "amountContributed": "${'$'}500.00",    "btcPrice": "${'$'}42,150.00",   "btcAcquired": "0.01186700", "currentValue": "${'$'}1,222.30" },
    { "date": "2024-02-15", "time": "18:10", "amountContributed": "${'$'}500.00",    "btcPrice": "${'$'}48,320.00",   "btcAcquired": "0.01035062", "currentValue": "${'$'}1,066.11" },
    { "date": "2024-03-15", "time": "12:33", "amountContributed": "${'$'}500.00",    "btcPrice": "${'$'}67,890.00",   "btcAcquired": "0.00736649", "currentValue": "${'$'}758.75" },
    { "date": "2024-04-15", "time": "16:05", "amountContributed": "${'$'}500.00",    "btcPrice": "${'$'}128,000.00",  "btcAcquired": "0.00390625", "currentValue": "${'$'}402.34" }, 
    { "date": "2024-05-15", "time": "08:21", "amountContributed": "${'$'}500.00",    "btcPrice": "${'$'}61,780.00",   "btcAcquired": "0.00808975", "currentValue": "${'$'}833.24" },
    { "date": "2024-06-15", "time": "10:02", "amountContributed": "${'$'}500.00",    "btcPrice": "${'$'}66,420.00",   "btcAcquired": "0.00752900", "currentValue": "${'$'}775.49" },
    { "date": "2024-07-15", "time": "14:55", "amountContributed": "${'$'}500.00",    "btcPrice": "${'$'}63,150.00",   "btcAcquired": "0.00791800", "currentValue": "${'$'}815.55" },
    { "date": "2024-08-15", "time": "20:19", "amountContributed": "${'$'}278.90",    "btcPrice": "${'$'}59,340.00",   "btcAcquired": "0.00470000", "currentValue": "${'$'}484.10" }, 
    { "date": "2024-09-15", "time": "07:48", "amountContributed": "${'$'}766.48",    "btcPrice": "${'$'}58,960.00",   "btcAcquired": "0.01300000", "currentValue": "${'$'}1,339.00" },
    { "date": "2024-10-15", "time": "11:11", "amountContributed": "${'$'}488.39",    "btcPrice": "${'$'}100,000.00",  "btcAcquired": "0.00488390", "currentValue": "${'$'}503.04" },  
    { "date": "2024-11-15", "time": "13:00", "amountContributed": "${'$'}1,337.89",  "btcPrice": "${'$'}102,613.80",  "btcAcquired": "0.01303811", "currentValue": "${'$'}1,342.93" },
    { "date": "2024-12-15", "time": "09:12", "amountContributed": "${'$'}1,271.56",  "btcPrice": "${'$'}86,920.00",   "btcAcquired": "0.01462904", "currentValue": "${'$'}1,506.79" } 
  ]
}
""".trimIndent()

// -------------------- JSON (Withdrawals) --------------------
private val WITHDRAW_JSON = """
{
  "withdrawals": [
    { "id": "w-2024-02", "date": "2024-02-28", "time": "15:10", "amountFiat": "${'$'}300.00", "btcAmount": "0.00600000", "btcPrice": "${'$'}50,000.00" },
    { "id": "w-2024-06", "date": "2024-06-28", "time": "11:05", "amountFiat": "${'$'}420.00", "btcAmount": "0.00666667", "btcPrice": "${'$'}63,000.00" },
    { "id": "w-2024-10", "date": "2024-10-30", "time": "09:30", "amountFiat": "${'$'}150.00", "btcAmount": "0.00150000", "btcPrice": "${'$'}100,000.00" }
  ]
}
""".trimIndent()

// -------------------- Models --------------------
@Serializable data class Purchase(
    val date: String,
    val time: String,
    val amountContributed: String,
    val btcPrice: String,
    val btcAcquired: String,
    val currentValue: String
)
@Serializable data class PurchasesWrapper(val purchases: List<Purchase>)

@Serializable data class Withdrawal(
    val id: String,
    val date: String,
    val time: String,
    val amountFiat: String,
    val btcAmount: String,
    val btcPrice: String
)
@Serializable data class WithdrawalsWrapper(val withdrawals: List<Withdrawal>)

// Item unificado para a listagem
private sealed class StatementItem(val date: String) {
    class Deposit(val data: Purchase) : StatementItem(data.date)
    class Withdraw(val data: Withdrawal) : StatementItem(data.date)
}

// -------------------- Helpers --------------------
private fun parseUsd(s: String): Double =
    s.replace("$", "").replace(",", "").trim().toDoubleOrNull() ?: 0.0
private fun parseBtc(s: String): Double = s.toDoubleOrNull() ?: 0.0
private fun usd(amount: Double): String = "$" + "%,.2f".format(amount)
private fun btc(amount: Double): String = "%,.8f".format(amount)
private fun percent(p: Double): String = "%,.2f%%".format(p)

private val GainGreen = Color(0xFF2ECC71)
private val LossRed = Color(0xFFE74C3C)
private val StripeGreen = Color(0x1A2ECC71)
private val StripeRed = Color(0x1AE74C3C)

// -------------------- Screen --------------------
@Composable
fun PurchaseHistoryScreen(viewModel: PurchaseHistoryViewModelAbstract) {
    val json = remember { Json { ignoreUnknownKeys = true } }
    val purchases = remember { json.decodeFromString<PurchasesWrapper>(PURCHASES_JSON).purchases }
    val withdrawals = remember { json.decodeFromString<WithdrawalsWrapper>(WITHDRAW_JSON).withdrawals }

    // Agregados (somente deposits)
    val totalContributed = purchases.sumOf { parseUsd(it.amountContributed) }
    val totalCurrent = purchases.sumOf { parseUsd(it.currentValue) }
    val totalBtc = purchases.sumOf { parseBtc(it.btcAcquired) }
    val pnl = totalCurrent - totalContributed
    val pnlPct = if (totalContributed > 0) (pnl / totalContributed) * 100.0 else 0.0
    val avgPrice = if (totalBtc > 0) totalContributed / totalBtc else 0.0

    // Mescla em uma listagem única (mais recente primeiro)
    val statementItems = remember(purchases, withdrawals) {
        (withdrawals.map { StatementItem.Withdraw(it) } + purchases.map { StatementItem.Deposit(it) })
            .sortedByDescending { it.date } // "YYYY-MM-DD"
    }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estado de veto por ID
    val vetoedMap = remember { mutableStateMapOf<String, Boolean>() }

    SetupScreen(viewModel = viewModel) {
        Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Overview (clean)
                item { PortfolioOverviewPlain(totalContributed, totalCurrent, pnl, pnlPct, avgPrice, totalBtc) }

                // Título
                item {
                    Text("Statement", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                }

                // Lista única
                items(statementItems) { item ->
                    when (item) {
                        is StatementItem.Deposit -> DepositCard(p = item.data)
                        is StatementItem.Withdraw -> {
                            val isVetoed = vetoedMap[item.data.id] == true
                            WithdrawalCard(
                                w = item.data,
                                isVetoed = isVetoed,
                                onVetoClick = {
                                    if (!isVetoed) {
                                        vetoedMap[item.data.id] = true
                                        scope.launch { snackbar.showSnackbar("Withdrawal vetoed.") }
                                    }
                                }
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

// -------------------- Overview (no background) --------------------
@Composable
private fun PortfolioOverviewPlain(
    totalContributed: Double,
    totalCurrent: Double,
    pnl: Double,
    pnlPct: Double,
    avgPrice: Double,
    totalBtc: Double
) {
    val gain = pnl >= 0
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Portfolio Overview", style = MaterialTheme.typography.titleLarge)

        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Column {
                Text("Total Contributed", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(usd(totalContributed), fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Current Value", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(usd(totalCurrent), fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End)
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Column {
                Text("BTC Holdings", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("₿" + btc(totalBtc), fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Average Price", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(usd(avgPrice), fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End)
            }
        }

        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("P/L", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (gain) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward,
                    null,
                    tint = if (gain) GainGreen else LossRed
                )
                Spacer(Modifier.width(6.dp))
                Text("${usd(pnl)} (${percent(pnlPct)})", color = if (gain) GainGreen else LossRed, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun DepositCard(p: Purchase) {
    val contributed = parseUsd(p.amountContributed)
    val current = parseUsd(p.currentValue)
    val gain = current - contributed
    val gainPositive = gain >= 0
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // para a faixa ocupar a altura do conteúdo
        ) {
            // Left border (accent)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(GainGreen)
                    .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
            )

            Column(Modifier.padding(14.dp).weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(p.date, fontWeight = FontWeight.SemiBold)
                        Text("Deposit", style = MaterialTheme.typography.labelSmall, color = GainGreen)
                        Text("Contribution: ${p.amountContributed}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (gainPositive) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward,
                                contentDescription = null,
                                tint = if (gainPositive) GainGreen else LossRed
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                p.currentValue,
                                fontWeight = FontWeight.SemiBold,
                                color = if (gainPositive) GainGreen else LossRed
                            )
                        }
                    }
                    Icon(
                        if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (expanded) {
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(10.dp))

                    KeyValue("Price at Purchase", p.btcPrice)
                    KeyValue("BTC Acquired", "₿" + btc(parseBtc(p.btcAcquired)), bold = true)
                }
            }
        }
    }
}


// ========== Withdrawal Card (left accent + veto state) ==========
@Composable
private fun WithdrawalCard(
    w: Withdrawal,
    isVetoed: Boolean,
    onVetoClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val borderColor = if (isVetoed) LossRed else MaterialTheme.colorScheme.outlineVariant
    val borderWidth = if (isVetoed) 2.dp else 1.dp

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Left border (accent)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(LossRed)
                    .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
            )

            Column(Modifier.padding(14.dp).weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(w.date, fontWeight = FontWeight.SemiBold)
                        Text("Withdrawal", style = MaterialTheme.typography.labelSmall, color = LossRed)
                        Text("Amount: ${w.amountFiat}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(
                        if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (expanded) {
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(10.dp))

                    KeyValue("BTC Amount", "₿" + btc(parseBtc(w.btcAmount)))
                    KeyValue("Price at Withdrawal", w.btcPrice)

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (!isVetoed) {
                            TextButton(onClick = onVetoClick) {
                                Icon(Icons.Rounded.Gavel, contentDescription = null, tint = LossRed)
                                Spacer(Modifier.width(6.dp))
                                Text("Veto", color = LossRed)
                            }
                        } else {
                            Text(
                                "Vetoed",
                                color = LossRed,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(LossRed.copy(alpha = 0.12f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


// -------------------- Reusable KV --------------------
@Composable
private fun KeyValue(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal)
    }
}
