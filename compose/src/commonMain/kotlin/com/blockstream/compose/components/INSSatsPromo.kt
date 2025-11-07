package com.blockstream.compose.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.blockstream.common.models.GreenViewModel
import com.blockstream.common.navigation.NavigateDestinations
import com.blockstream.compose.theme.bodyMedium
import com.blockstream.compose.theme.titleMedium

@Composable
fun INSSatsPromo(
    viewModel: GreenViewModel,
    modifier: Modifier = Modifier
) {
    GreenCard(
        padding = 0,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {

        Column(Modifier.fillMaxWidth()) {

            // Header com gradiente e imagem destacada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 20.dp)
                    ) {


                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Retirement Tracker",
                            style = titleMedium
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = "A clear overview of your long-term retirement goal.",
                            style = bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AsyncImage(
                        model = "file:///android_asset/composeResources/blockstream_green.common.generated.resources/drawable/pension.png",
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(140.dp)
                    )
                }
            }

            Divider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GreenButton(
                    text = "View Progress",
                    size = GreenButtonSize.BIG,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    viewModel.postEvent(
                        NavigateDestinations.INSSats(viewModel.greenWallet)
                    )
                }
            }
        }
    }
}
