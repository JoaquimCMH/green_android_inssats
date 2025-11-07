## INSSats Tracker inside Blockstream Green

This README shows how to demo the INSSats retirement experience embedded in the project.

### Why Blockstream Green?
- Reuses a production-grade wallet: multisig, hardware support and onboarding flows are already solved.
- Gives the Saver persona a familiar home while we experiment with retirement-specific UX.
- Lets us ship fast—only the new retirement screens are customized, everything else comes from Green.

### Launching the prototype
1. Build the debug build: `./gradlew :androidApp:installDebug`.
2. Install/run on an emulator or USB device.
3. Log into any wallet (test wallets work). Navigation relies on the currently selected `greenWallet`.
4. In the **Wallet Overview** screen, scroll until you see the **Retirement Tracker** card and tap **View Progress**.

### Screen tour
- **Retirement Tracker (`INSSatsScreen`)**
  - Displays base vs expected projections and the most recent contribution path on a single timeline chart.
  - "Simulate Balance Adjustment" lets you model extra deposits or withdrawals and see their effect on savings horizon.
  - Action cards at the bottom jump to transaction history and the two-step withdrawal flow.
- **Transaction History (`PurchaseHistoryScreen`)**
  - Merges deposits and withdrawals into one statement with aggregated totals (contributed, current value, P&L, BTC holdings).
  - Each withdrawal entry exposes a `Veto` quick action, representing the safety net step before funds leave the vault.
  - Uses mocked JSON data so the UI can be validated without a backend connection.
- **Planned Withdrawal (`PlannedWithdrawalScreen`)**
  - Two-card layout: request (amount, destination, saver signature) and execution of a pending operation.
  - Basic validations and snackbar feedback illustrate how Saver, Broker and Vault Keeper coordinate.

⚠️ The current data feeds (balances, projections, statements and operations) are mocked. The screens exist to demonstrate how INSSats would behave when plugged into a real Blockstream Green wallet, showcasing the end-to-end flow before backend wiring.

