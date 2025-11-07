package com.blockstream.common.models.inssats

import com.blockstream.common.data.GreenWallet
import com.blockstream.common.extensions.previewWallet
import com.blockstream.common.models.GreenViewModel
import com.blockstream.ui.events.Event
import com.blockstream.ui.navigation.NavData
import com.rickclephas.kmp.observableviewmodel.launch

abstract class INSSatsViewModelAbstract(greenWallet: GreenWallet) : GreenViewModel(greenWalletOrNull = greenWallet) {
    // Properties and methods will be added here as we implement features
}

class INSSatsViewModel(greenWallet: GreenWallet) : INSSatsViewModelAbstract(greenWallet) {

    override fun screenName(): String = "INSSats"

    class LocalEvents {
        // Local events will be added here as we implement features
    }

    init {
        viewModelScope.launch {
            _navData.value = NavData(
                title = "Retirement",
                subtitle = greenWallet.name
            )
        }
        
        bootstrap()
    }

    override suspend fun handleEvent(event: Event) {
        super.handleEvent(event)
        
        // Handle events here as we implement features
    }
}

class INSSatsViewModelPreview : INSSatsViewModelAbstract(
    greenWallet = previewWallet(isHardware = false)
) {
    companion object {
        fun preview() = INSSatsViewModelPreview()
    }
}

