package com.blockstream.common.models.inssats

import com.blockstream.common.data.GreenWallet
import com.blockstream.common.extensions.previewWallet
import com.blockstream.common.models.GreenViewModel
import com.blockstream.ui.navigation.NavData
import com.rickclephas.kmp.observableviewmodel.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias Sats = Long
typealias PubKey = String
typealias Signature = String

data class VaultConfig(
    val label: String,
    val balance: Sats
)

data class PendingOperation(
    val id: String,
    val amount: Sats,
    val destination: PubKey
)

data class PlannedWithdrawalUiState(
    val vaultLabel: String = "Main Vault",
    val vaultBalance: Sats = 1_500_000L,
    val initiateAmount: String = "",
    val destinationPubKey: String = "",
    val saverSignature: String = "",
    val initiateResult: String? = null,
    val pendingOperation: PendingOperation? = null,
    val executeResult: String? = null,
    val error: String? = null
)

abstract class PlannedWithdrawalViewModelAbstract(
    greenWallet: GreenWallet
) : GreenViewModel(greenWalletOrNull = greenWallet) {
    abstract val uiState: StateFlow<PlannedWithdrawalUiState>
    abstract fun updateAmountInput(value: String)
    abstract fun updateDestination(value: String)
    abstract fun updateSignature(value: String)
    abstract fun onInitiate()
    abstract fun onExecute()
    abstract fun clearMessages()

    override fun screenName(): String = "PlannedWithdrawal"
}

class PlannedWithdrawalViewModel(
    greenWallet: GreenWallet
) : PlannedWithdrawalViewModelAbstract(greenWallet = greenWallet) {

    private val _uiState = MutableStateFlow(PlannedWithdrawalUiState())
    override val uiState: StateFlow<PlannedWithdrawalUiState> = _uiState.asStateFlow()

    private var currentVault = VaultConfig(
        label = _uiState.value.vaultLabel,
        balance = _uiState.value.vaultBalance
    )

    init {
        viewModelScope.launch {
            _navData.value = NavData(
                title = "Planned Withdrawal",
                subtitle = greenWallet.name
            )
        }
    }

    override fun updateAmountInput(value: String) {
        _uiState.update { it.copy(initiateAmount = value, error = null) }
    }

    override fun updateDestination(value: String) {
        _uiState.update { it.copy(destinationPubKey = value, error = null) }
    }

    override fun updateSignature(value: String) {
        _uiState.update { it.copy(saverSignature = value, error = null) }
    }

    override fun onInitiate() {
        val current = _uiState.value
        val amount = current.initiateAmount.toLongOrNull()
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(error = "Amount must be a positive integer (sats).") }
            return
        }
        val destination = current.destinationPubKey
        if (destination.isBlank()) {
            _uiState.update { it.copy(error = "Destination pubkey is required.") }
            return
        }
        val signature = current.saverSignature
        if (signature.isBlank()) {
            _uiState.update { it.copy(error = "Saver signature is required.") }
            return
        }

        val (updatedVault, pendingOperation) = try {
            initiatePlannedWithdrawal(
                vault = currentVault,
                amount = amount,
                destination = destination,
                saverSig = signature
            )
        } catch (e: IllegalArgumentException) {
            _uiState.update { it.copy(error = e.message) }
            return
        }
        currentVault = updatedVault
        _uiState.update {
            it.copy(
                vaultBalance = updatedVault.balance,
                initiateResult = "Pending operation created (id=${pendingOperation.id}).",
                pendingOperation = pendingOperation,
                executeResult = null,
                error = null
            )
        }
    }

    override fun onExecute() {
        val operation = _uiState.value.pendingOperation
        if (operation == null) {
            _uiState.update { it.copy(error = "No pending operation to execute.") }
            return
        }

        val updatedVault = executePlannedWithdrawal(
            vault = currentVault,
            operation = operation
        )
        currentVault = updatedVault
        _uiState.update {
            it.copy(
                vaultBalance = updatedVault.balance,
                executeResult = "Withdrawal executed for ${operation.amount} sats.",
                pendingOperation = null,
                error = null
            )
        }
    }

    override fun clearMessages() {
        _uiState.update { it.copy(initiateResult = null, executeResult = null, error = null) }
    }

    private fun initiatePlannedWithdrawal(
        vault: VaultConfig,
        amount: Sats,
        destination: PubKey,
        saverSig: Signature
    ): Pair<VaultConfig, PendingOperation> {
        require(vault.balance >= amount) { "Insufficient balance in vault." }
        val newVault = vault.copy(balance = vault.balance - amount)
        val pending = PendingOperation(
            id = "op-${System.currentTimeMillis()}",
            amount = amount,
            destination = destination
        )
        return newVault to pending
    }

    private fun executePlannedWithdrawal(
        vault: VaultConfig,
        operation: PendingOperation
    ): VaultConfig {
        return vault.copy(balance = vault.balance)
    }
}

class PlannedWithdrawalViewModelPreview : PlannedWithdrawalViewModelAbstract(
    greenWallet = previewWallet(isHardware = false)
) {
    private val _state = MutableStateFlow(PlannedWithdrawalUiState())
    override val uiState: StateFlow<PlannedWithdrawalUiState> = _state.asStateFlow()

    override fun updateAmountInput(value: String) { _state.value = _state.value.copy(initiateAmount = value) }
    override fun updateDestination(value: String) { _state.value = _state.value.copy(destinationPubKey = value) }
    override fun updateSignature(value: String) { _state.value = _state.value.copy(saverSignature = value) }
    override fun onInitiate() {}
    override fun onExecute() {}
    override fun clearMessages() {}

    companion object {
        fun preview() = PlannedWithdrawalViewModelPreview()
    }
}

