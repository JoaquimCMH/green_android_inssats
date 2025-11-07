package com.blockstream.common.models.inssats

import com.blockstream.common.data.GreenWallet
import com.blockstream.common.extensions.previewWallet
import com.blockstream.common.models.GreenViewModel
import com.blockstream.ui.events.Event
import com.blockstream.ui.navigation.NavData
import com.rickclephas.kmp.observableviewmodel.launch

abstract class PurchaseHistoryViewModelAbstract(
    greenWallet: GreenWallet
) : GreenViewModel(greenWalletOrNull = greenWallet) {
    override fun screenName(): String = "PurchaseHistory"
}

class PurchaseHistoryViewModel(
    greenWallet: GreenWallet
) : PurchaseHistoryViewModelAbstract(greenWallet = greenWallet) {
    
    class LocalEvents {
        // Local events will be added here as needed
    }

    init {
        viewModelScope.launch {
            _navData.value = NavData(
                title = "Transaction History",
                subtitle = greenWallet.name
            )
        }
        
        bootstrap()
    }

    override suspend fun handleEvent(event: Event) {
        super.handleEvent(event)
        
        // Handle events here as needed
    }
}

class PurchaseHistoryViewModelPreview : PurchaseHistoryViewModelAbstract(
    greenWallet = previewWallet(isHardware = false)
) {
    companion object {
        fun preview() = PurchaseHistoryViewModelPreview()
    }
}

