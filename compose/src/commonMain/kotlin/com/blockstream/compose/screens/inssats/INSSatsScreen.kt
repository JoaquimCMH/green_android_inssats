package com.blockstream.compose.screens.inssats

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.MultilineChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blockstream.common.models.inssats.INSSatsViewModelAbstract
import com.blockstream.common.navigation.NavigateDestinations
import com.blockstream.compose.utils.SetupScreen
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.AxisStyle
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.XYGraph
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

// ---------- Embedded data ----------
private val DATA_JSON = """
{
  "retirement": {
    "currentAge": 31,
    "lifeExpectancy": 85,
   "baseProjection": [
      { "age": 31, "btc": 0.00489193 },
      { "age": 32, "btc": 0.00885207 },
      { "age": 33, "btc": 0.01205790 },
      { "age": 34, "btc": 0.01465309 },
      { "age": 35, "btc": 0.01675396 },
      { "age": 36, "btc": 0.01845466 },
      { "age": 37, "btc": 0.01983142 },
      { "age": 38, "btc": 0.02094594 },
      { "age": 39, "btc": 0.02184817 },
      { "age": 40, "btc": 0.02257855 },
      { "age": 41, "btc": 0.02316981 },
      { "age": 42, "btc": 0.02364844 },
      { "age": 43, "btc": 0.02403591 },
      { "age": 44, "btc": 0.02434958 },
      { "age": 45, "btc": 0.02460349 },
      { "age": 46, "btc": 0.02055982 },
      { "age": 47, "btc": 0.01728637 },
      { "age": 48, "btc": 0.01463643 },
      { "age": 49, "btc": 0.01249124 },
      { "age": 50, "btc": 0.01075466 },
      { "age": 51, "btc": 0.00934886 },
      { "age": 52, "btc": 0.00821083 },
      { "age": 53, "btc": 0.00728956 },
      { "age": 54, "btc": 0.00654378 },
      { "age": 55, "btc": 0.00594005 },
      { "age": 56, "btc": 0.00545132 },
      { "age": 57, "btc": 0.00505567 },
      { "age": 58, "btc": 0.00473539 },
      { "age": 59, "btc": 0.00447612 },
      { "age": 60, "btc": 0.00426623 },
      { "age": 61, "btc": 0.00409632 },
      { "age": 62, "btc": 0.00395877 },
      { "age": 63, "btc": 0.00384742 },
      { "age": 64, "btc": 0.00375728 },
      { "age": 65, "btc": 0.00368431 },
      { "age": 66, "btc": 0.00362524 },
      { "age": 67, "btc": 0.00357743 },
      { "age": 68, "btc": 0.00353871 },
      { "age": 69, "btc": 0.00350738 },
      { "age": 70, "btc": 0.00348201 },
      { "age": 71, "btc": 0.00346147 },
      { "age": 72, "btc": 0.00344485 },
      { "age": 73, "btc": 0.00343139 },
      { "age": 74, "btc": 0.00342050 },
      { "age": 75, "btc": 0.00341168 },
      { "age": 76, "btc": 0.00340454 },
      { "age": 77, "btc": 0.00339876 },
      { "age": 78, "btc": 0.00339408 },
      { "age": 79, "btc": 0.00339029 },
      { "age": 80, "btc": 0.00338723 },
      { "age": 81, "btc": 0.00338474 },
      { "age": 82, "btc": 0.00338274 },
      { "age": 83, "btc": 0.00338111 },
      { "age": 84, "btc": 0.00337979 },
      { "age": 85, "btc": 0.00337873 }
    ],

    "expectedProjection": [
      { "age": 32, "btc": 0.00488390 },
      { "age": 33, "btc": 0.00883754 },
      { "age": 34, "btc": 0.01203811 },
      { "age": 35, "btc": 0.01462904 },
      { "age": 36, "btc": 0.01672646 },
      { "age": 37, "btc": 0.01842437 },
      { "age": 38, "btc": 0.01979887 },
      { "age": 39, "btc": 0.02091156 },
      { "age": 40, "btc": 0.02181231 },
      { "age": 41, "btc": 0.02254149 },
      { "age": 42, "btc": 0.02313178 },
      { "age": 43, "btc": 0.02360963 },
      { "age": 44, "btc": 0.02399646 },
      { "age": 45, "btc": 0.02430961 },
      { "age": 46, "btc": 0.02456312 },
      { "age": 47, "btc": 0.02476833 },
      { "age": 48, "btc": 0.02068323 },
      { "age": 49, "btc": 0.01737625 },
      { "age": 50, "btc": 0.01469916 },
      { "age": 51, "btc": 0.01253200 },
      { "age": 52, "btc": 0.01077763 },
      { "age": 53, "btc": 0.00935743 },
      { "age": 54, "btc": 0.00820774 },
      { "age": 55, "btc": 0.00727704 },
      { "age": 56, "btc": 0.00652361 },
      { "age": 57, "btc": 0.00591370 },
      { "age": 58, "btc": 0.00541996 },
      { "age": 59, "btc": 0.00502026 },
      { "age": 60, "btc": 0.00469670 },
      { "age": 61, "btc": 0.00443477 },
      { "age": 62, "btc": 0.00422273 },
      { "age": 63, "btc": 0.00405108 },
      { "age": 64, "btc": 0.00391212 },
      { "age": 65, "btc": 0.00379963 },
      { "age": 66, "btc": 0.00370857 },
      { "age": 67, "btc": 0.00363485 },
      { "age": 68, "btc": 0.00357518 },
      { "age": 69, "btc": 0.00352687 },
      { "age": 70, "btc": 0.00348776 },
      { "age": 71, "btc": 0.00345611 },
      { "age": 72, "btc": 0.00343048 },
      { "age": 73, "btc": 0.00340973 },
      { "age": 74, "btc": 0.00339294 },
      { "age": 75, "btc": 0.00337934 },
      { "age": 76, "btc": 0.00336833 },
      { "age": 77, "btc": 0.00335943 },
      { "age": 78, "btc": 0.00335221 },
      { "age": 79, "btc": 0.00334637 },
      { "age": 80, "btc": 0.00334165 },
      { "age": 81, "btc": 0.00333782 },
      { "age": 82, "btc": 0.00333472 },
      { "age": 83, "btc": 0.00333222 },
      { "age": 84, "btc": 0.00333019 },
      { "age": 85, "btc": 0.00332854 }
    ],

    "addAmountSeed": [
      { "age": 27, "btc": 0.00488140 },
      { "age": 28, "btc": 0.00883300 },
      { "age": 29, "btc": 0.01203192 },
      { "age": 30, "btc": 0.01462153 },
      { "age": 31, "btc": 0.01671787 },
      { "age": 32, "btc": 0.01841491 },
      { "age": 33, "btc": 0.01978871 },
      { "age": 34, "btc": 0.02090083 },
      { "age": 35, "btc": 0.02180111 },
      { "age": 36, "btc": 0.02252992 },
      { "age": 37, "btc": 0.02311990 },
      { "age": 38, "btc": 0.02359751 },
      { "age": 39, "btc": 0.02398414 },
      { "age": 40, "btc": 0.02429713 },
      { "age": 41, "btc": 0.02455050 },
      { "age": 42, "btc": 0.02475561 },
      { "age": 43, "btc": 0.02067261 },
      { "age": 44, "btc": 0.01736732 },
      { "age": 45, "btc": 0.01469162 },
      { "age": 46, "btc": 0.01252557 },
      { "age": 47, "btc": 0.01077210 },
      { "age": 48, "btc": 0.00935262 },
      { "age": 49, "btc": 0.00820352 },
      { "age": 50, "btc": 0.00727330 },
      { "age": 51, "btc": 0.00652026 },
      { "age": 52, "btc": 0.00591066 },
      { "age": 53, "btc": 0.00541717 },
      { "age": 54, "btc": 0.00501768 },
      { "age": 55, "btc": 0.00469429 },
      { "age": 56, "btc": 0.00443249 },
      { "age": 57, "btc": 0.00422056 },
      { "age": 58, "btc": 0.00404900 },
      { "age": 59, "btc": 0.00391011 },
      { "age": 60, "btc": 0.00379768 },
      { "age": 61, "btc": 0.00370667 },
      { "age": 62, "btc": 0.00363299 },
      { "age": 63, "btc": 0.00357334 },
      { "age": 64, "btc": 0.00352506 },
      { "age": 65, "btc": 0.00348597 },
      { "age": 66, "btc": 0.00345433 },
      { "age": 67, "btc": 0.00342872 },
      { "age": 68, "btc": 0.00340798 },
      { "age": 69, "btc": 0.00339119 },
      { "age": 70, "btc": 0.00337761 },
      { "age": 71, "btc": 0.00336661 },
      { "age": 72, "btc": 0.00335770 },
      { "age": 73, "btc": 0.00335049 },
      { "age": 74, "btc": 0.00334466 },
      { "age": 75, "btc": 0.00333993 },
      { "age": 76, "btc": 0.00333611 },
      { "age": 77, "btc": 0.00333301 },
      { "age": 78, "btc": 0.00333050 },
      { "age": 79, "btc": 0.00332848 },
      { "age": 80, "btc": 0.00332683 },
      { "age": 81, "btc": 0.00332550 },
      { "age": 82, "btc": 0.00332443 },
      { "age": 83, "btc": 0.00332356 },
      { "age": 84, "btc": 0.00332285 },
      { "age": 85, "btc": 0.00332228 }
    ],
    
    "removeAmountSeed": [
      { "age": 35, "btc": 0.00486380 },
      { "age": 36, "btc": 0.00880116 },
      { "age": 37, "btc": 0.01198854 },
      { "age": 38, "btc": 0.01456881 },
      { "age": 39, "btc": 0.01665759 },
      { "age": 40, "btc": 0.01834852 },
      { "age": 41, "btc": 0.01971736 },
      { "age": 42, "btc": 0.02082547 },
      { "age": 43, "btc": 0.02172251 },
      { "age": 44, "btc": 0.02244868 },
      { "age": 45, "btc": 0.02303654 },
      { "age": 46, "btc": 0.02351243 },
      { "age": 47, "btc": 0.02389767 },
      { "age": 48, "btc": 0.02420953 },
      { "age": 49, "btc": 0.02446198 },
      { "age": 50, "btc": 0.02466636 },
      { "age": 51, "btc": 0.02059808 },
      { "age": 52, "btc": 0.01730471 },
      { "age": 53, "btc": 0.01463864 },
      { "age": 54, "btc": 0.01248040 },
      { "age": 55, "btc": 0.01073326 },
      { "age": 56, "btc": 0.00931890 },
      { "age": 57, "btc": 0.00817394 },
      { "age": 58, "btc": 0.00724708 },
      { "age": 59, "btc": 0.00649675 },
      { "age": 60, "btc": 0.00588935 },
      { "age": 61, "btc": 0.00539764 },
      { "age": 62, "btc": 0.00499959 },
      { "age": 63, "btc": 0.00467736 },
      { "age": 64, "btc": 0.00441651 },
      { "age": 65, "btc": 0.00420534 },
      { "age": 66, "btc": 0.00403440 },
      { "age": 67, "btc": 0.00389601 },
      { "age": 68, "btc": 0.00378399 },
      { "age": 69, "btc": 0.00369330 },
      { "age": 70, "btc": 0.00361989 },
      { "age": 71, "btc": 0.00356046 },
      { "age": 72, "btc": 0.00351235 },
      { "age": 73, "btc": 0.00347340 },
      { "age": 74, "btc": 0.00344188 },
      { "age": 75, "btc": 0.00341635 },
      { "age": 76, "btc": 0.00339569 },
      { "age": 77, "btc": 0.00337897 },
      { "age": 78, "btc": 0.00336543 },
      { "age": 79, "btc": 0.00335447 },
      { "age": 80, "btc": 0.00334559 },
      { "age": 81, "btc": 0.00333841 },
      { "age": 82, "btc": 0.00333260 },
      { "age": 83, "btc": 0.00332789 },
      { "age": 84, "btc": 0.00332408 },
      { "age": 85, "btc": 0.00332099 }
    ],
    
    
    "investmentDashboard": [
      { "age": 31, "btc": 0.0047 },
      { "age": 32, "btc": 0.00488390 },
      { "age": 33, "btc": 0.013 },
      { "age": 34, "btc": 0.01303811 },
      { "age": 35, "btc": 0.01462904 }
    ],



    "overview": {
      "retirementAge": 46,
      "totalSavings": "₿ 0.0294272",
      "btcPriceAtRetirement": "$ 1,916,708",
      "currentBtcPrice": "$ 103,670.80",
      "desiredAnnualIncome": "₿ 0.00062357",
      "monthlyIncomeAtRetirement": "₿ 0.00005196"
    }
  }
}
""".trimIndent()



// ---------- Models ----------
@Serializable data class SeriesPoint(val age: Int, val btc: Float)
@Serializable data class Overview(
    val retirementAge: Int,
    val totalSavings: String,
    val btcPriceAtRetirement: String,
    val currentBtcPrice: String,
    val desiredAnnualIncome: String,
    val monthlyIncomeAtRetirement: String
)
@Serializable data class RetirementPayload(
    val currentAge: Int,
    val lifeExpectancy: Int,
    val baseProjection: List<SeriesPoint>,
    val expectedProjection: List<SeriesPoint>,
    val addAmountSeed: List<SeriesPoint>,
    val removeAmountSeed: List<SeriesPoint>,
    val investmentDashboard: List<SeriesPoint>,
    val overview: Overview
)
@Serializable data class Root(val retirement: RetirementPayload)

// ---------- Utils ----------
private data class ChartColors(
    val base: Color = Color(0xFFFFA726),     // BTC (orange)
    val actual: Color = Color(0xFF9C27B0),   // Actual (purple)
    val simulation: Color = Color(0xFF1E88E5),// Simulation (blue)
    val investment: Color = Color(0xFF8D6E63), // Investment Dashboard (brown)
    val border: Color = Color(0x223A3A3A),
    val gridLine: Color = Color(0xFF2A2A2A)  // Cinza escuro para grid
)

private fun List<SeriesPoint>.toPoints(): List<Point<Float, Float>> =
    sortedBy { it.age }.map { Point(it.age.toFloat(), it.btc) }

private fun List<Point<Float, Float>>.continueFrom(anchor: Point<Float, Float>): List<Point<Float, Float>> {
    if (isEmpty()) return listOf(anchor)
    val tail = filter { it.x >= anchor.x }
    return buildList {
        add(anchor)
        addAll(tail)
    }
}

private fun List<SeriesPoint>.shiftLeftYears(years: Int): List<SeriesPoint> {
    if (years <= 0 || isEmpty()) return this
    val minAge = minOf { it.age }
    val peakAge = maxBy { it.btc }.age
    return map { p ->
        val shiftedAge = max(minAge, p.age - years)
        val peakDist = (peakAge - p.age).coerceAtLeast(0)
        val boost = (0.04f * years) * exp(-peakDist / 8f)
        SeriesPoint(age = shiftedAge, btc = (p.btc + boost))
    }
        .groupBy { it.age }
        .map { (age, list) -> SeriesPoint(age, list.maxOf { it.btc }) }
        .sortedBy { it.age }
}

private fun prettySats(v: Long): String {
    val s = abs(v).toString()
    val b = StringBuilder()
    s.reversed().chunked(3).forEachIndexed { i, c -> if (i > 0) b.append(','); b.append(c) }
    return b.reverse().toString()
}

private fun formatSatsWithSign(v: Long): String = when {
    v > 0 -> "+${prettySats(v)}"
    v < 0 -> "-${prettySats(v)}"
    else -> "0"
}

// ---------- Screen ----------
@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun INSSatsScreen(viewModel: INSSatsViewModelAbstract) {
    val json = remember { Json { ignoreUnknownKeys = true } }
    val payload = remember { json.decodeFromString<Root>(DATA_JSON).retirement }
    val colors = remember { ChartColors() }

    var showDialog by remember { mutableStateOf(false) }
    var simulationInput by remember { mutableStateOf("0") } // VALOR INICIAL
    var appliedSimulation by remember { mutableStateOf(0L) }

    val base = remember(payload) { payload.baseProjection.toPoints() }
    val investmentDashboard = remember(payload) { payload.investmentDashboard.toPoints() }
    val lastInvestmentPoint = remember(investmentDashboard) { investmentDashboard.last() }
    val actual = remember(payload, lastInvestmentPoint) {
        payload.expectedProjection
            .toPoints()
            .continueFrom(lastInvestmentPoint)
    }
    val simulationSeries = remember(payload, lastInvestmentPoint, appliedSimulation) {
        when {
            appliedSimulation > 0L -> payload.addAmountSeed
                .toPoints()
                .continueFrom(lastInvestmentPoint)
            appliedSimulation < 0L -> payload.removeAmountSeed
                .toPoints()
                .continueFrom(lastInvestmentPoint)
            else -> null
        }
    }
    val hasSimulation = simulationSeries != null

    val allSeries = remember(base, actual, investmentDashboard, simulationSeries) {
        buildList {
            add(base)
            add(actual)
            add(investmentDashboard)
            simulationSeries?.let { add(it) }
        }
    }

    val minAge = allSeries.minOf { it.first().x }
    val maxAge = allSeries.maxOf { it.last().x }
    val maxBtc = allSeries.flatMap { it }.maxOf { it.y } * 1.1f

    SetupScreen(viewModel = viewModel) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // UM ÚNICO BOX
            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SolidColor(colors.border), RoundedCornerShape(12.dp))
            ) {
                Column(Modifier.padding(16.dp)) {
                    // OVERVIEW
                    Text("Overview", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(12.dp))
                    MetricsGrid(payload.overview)

                    Spacer(Modifier.height(16.dp))

                    // HR SEPARADOR
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = colors.border
                    )

                    Spacer(Modifier.height(16.dp))

                    Box {
                        XYGraph(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
                            xAxisModel = FloatLinearAxisModel(
                                range = minAge..maxAge,
                                minimumMajorTickIncrement = 5f  // DE 5 EM 5 ANOS FORÇADO
                            ),
                            yAxisModel = FloatLinearAxisModel(
                                range = 0f..maxBtc,
                                minimumMajorTickIncrement = maxBtc / 7f  // 7 TICKS
                            ),
                            xAxisLabels = { v -> 
                                val age = v.toInt()
                                if (age % 5 == 0) age.toString() else ""  // Só múltiplos de 5
                            },
                            yAxisLabels = { v -> "%.3f".format(v) },
                            xAxisTitle = null,
                            yAxisTitle = null,
                            xAxisStyle = AxisStyle(color = Color(0xFF4A4A4A), lineWidth = 1.dp),
                            yAxisStyle = AxisStyle(color = Color(0xFF4A4A4A), lineWidth = 1.dp),
                            // LINHAS CINZA ESCURO
                            horizontalMajorGridLineStyle = LineStyle(
                                brush = SolidColor(colors.gridLine),
                                strokeWidth = 1.dp
                            ),
                            verticalMajorGridLineStyle = LineStyle(
                                brush = SolidColor(colors.gridLine),
                                strokeWidth = 1.dp
                            ),
                            horizontalMinorGridLineStyle = null,
                            verticalMinorGridLineStyle = null
                        ) {

                            // BTC - Linha sólida laranja ARREDONDADA
                            LinePlot(
                                data = base,
                                lineStyle = LineStyle(
                                    brush = SolidColor(colors.base),
                                    strokeWidth = 2.dp,
                                    pathEffect = PathEffect.cornerPathEffect(1f)
                                ),
                                animationSpec = tween(durationMillis = 2000)
                            )
                            // Actual (tracejada roxa) ARREDONDADA - DESENHAR PRIMEIRO
                            LinePlot(
                                data = actual,
                                lineStyle = LineStyle(
                                    brush = SolidColor(colors.actual),
                                    strokeWidth = 2.dp,
                                    pathEffect = PathEffect.chainPathEffect(
                                        PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
                                        PathEffect.cornerPathEffect(1f)
                                    )
                                ),
                                animationSpec = tween(durationMillis = 2000)
                            )
                            
                            // SIMULATION - SÓ APARECE SE TIVER VALOR - ARREDONDADA
                            if (hasSimulation) {
                                key(appliedSimulation) {
                                    LinePlot(
                                        data = simulationSeries!!,
                                        lineStyle = LineStyle(
                                            brush = SolidColor(colors.simulation),
                                            strokeWidth = 2.dp,
                                            pathEffect = PathEffect.chainPathEffect(
                                                PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
                                                PathEffect.cornerPathEffect(1f)
                                            )
                                        ),
                                        animationSpec = tween(durationMillis = 2000)
                                    )
                                }
                            }
                            
                            // INVESTMENT DASHBOARD - Linha sólida marrom ARREDONDADA
                            LinePlot(
                                data = investmentDashboard,
                                lineStyle = LineStyle(
                                    brush = SolidColor(colors.investment),
                                    strokeWidth = 2.dp,
                                    pathEffect = PathEffect.cornerPathEffect(1f)
                                ),
                                animationSpec = tween(durationMillis = 2000)
                            )

                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    // LEGENDA NA PARTE DE BAIXO
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        LegendDot("Expected", colors.base)
                        Spacer(Modifier.width(10.dp))
                        LegendDot("Actual", colors.actual)
                        Spacer(Modifier.width(10.dp))
                        LegendDot("Investment", colors.investment)
                        if (hasSimulation) {
                            Spacer(Modifier.width(10.dp))
                            LegendDot("Simulation", colors.simulation)
                        }
                    }


                    if (hasSimulation) {
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Badge(text = "${formatSatsWithSign(appliedSimulation)} sats", color = colors.simulation)
                            val yearsLabel = when {
                                appliedSimulation > 0L -> "-4 years"
                                appliedSimulation < 0L -> "+4 years"
                                else -> "0 years"
                            }
                            Badge(text = yearsLabel, color = colors.simulation)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // SIMULATE BUTTON DENTRO DO BOX - CENTRALIZADO
                    OutlinedButton(
                        onClick = { showDialog = true },
                        shape = RoundedCornerShape(999.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Simulate Balance Adjustment")
                    }
                }
            }
            
            // BOTÃO PARA HISTÓRICO DE COMPRAS
            Spacer(Modifier.height(16.dp))
            
            OutlinedCard(
                onClick = { viewModel.postEvent(NavigateDestinations.PurchaseHistory(viewModel.greenWallet)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Transaction History",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "View your periodic transactions",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8D8D8D)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go to history",
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedCard(
                onClick = { viewModel.postEvent(NavigateDestinations.PlannedWithdrawal(viewModel.greenWallet)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Planned Withdrawal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Coordinate a two-step withdrawal",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8D8D8D)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go to planned withdrawal",
                    )
                }
            }
        }
    }

    // Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = { 
                TextButton(onClick = { 
                    appliedSimulation = simulationInput.toLongOrNull() ?: 0L
                    showDialog = false 
                }) { 
                    Text("Apply") 
                } 
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } },
            title = { 
                Text(
                    "Simulate Balance Adjustment",
                    style = MaterialTheme.typography.bodyMedium  // TÍTULO MENOR
                ) 
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = simulationInput,
                        onValueChange = { value ->
                            simulationInput = value.filterIndexed { index, ch ->
                                ch.isDigit() || (ch == '-' && index == 0)
                            }
                        },
                        label = { Text("Amount (sats)") },
                        placeholder = { Text("Enter sats amount") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    PresetsRow { preset -> simulationInput = preset }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ---------- UI pieces ----------
@Composable
private fun MetricsGrid(ov: Overview) {
    @Composable fun RowItem(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically  // CENTRALIZADO VERTICALMENTE
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,  // FONTE CONSTANTE
                color = Color(0xFF8D8D8D),
                modifier = Modifier.weight(0.6f).padding(end = 8.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,  // FONTE CONSTANTE
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,  // NEGRITO
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.4f)
            )
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RowItem("Retirement Age", ov.retirementAge.toString())
        RowItem("Total Savings", ov.totalSavings)
        RowItem("BTC Price at Retirement", ov.btcPriceAtRetirement)
        RowItem("Current BTC Price", ov.currentBtcPrice)
        RowItem("Desired Annual Income", ov.desiredAnnualIncome)
        RowItem("Monthly Income", ov.monthlyIncomeAtRetirement)
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(8.dp)) { drawCircle(color = color) }
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = color, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
private fun Badge(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.12f), modifier = Modifier.height(28.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 10.dp)) {
            Text(text, color = color, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun PresetsRow(onPreset: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PresetChip("-400k", "-400000", onPreset)
        PresetChip("0", "0", onPreset)
        PresetChip("400k", "400000", onPreset)
        PresetChip("1M", "1000000", onPreset)
    }
}

@Composable
private fun PresetChip(label: String, value: String, onPreset: (String) -> Unit) {
    OutlinedButton(
        onClick = { onPreset(value) },
        shape = RoundedCornerShape(999.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
    ) { Text(label) }
}
