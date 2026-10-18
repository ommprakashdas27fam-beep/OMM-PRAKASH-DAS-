package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BombType
import com.example.ui.HyruleViewModel
import com.example.ui.components.SheikahCard
import com.example.ui.theme.*

@Composable
fun RunesScreen(
    viewModel: HyruleViewModel,
    modifier: Modifier = Modifier
) {
    val selectedRune by viewModel.selectedRune.collectAsState()

    val runesList = listOf(
        "bomb" to "💣 Remote Bomb",
        "magnesis" to "🧲 Magnesis",
        "stasis" to "⏳ Stasis+",
        "cryonis" to "🧊 Cryonis",
        "sensor" to "📡 Sensor+"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SheikahSlateBg)
    ) {
        // Rune Selector Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "SHEIKAH SLATE RUNE TOOLKIT",
                color = SheikahCyanBright,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Ancient Sheikah technology runes & physics simulations",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(runesList.size) { idx ->
                    val (key, label) = runesList[idx]
                    val isSelected = selectedRune == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectRune(key) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SheikahCyan.copy(alpha = 0.25f),
                            selectedLabelColor = SheikahCyanBright
                        ),
                        modifier = Modifier.testTag("rune_tab_$key")
                    )
                }
            }
        }

        // Active Rune Interactive Panel
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            when (selectedRune) {
                "bomb" -> item { RemoteBombSimulator(viewModel) }
                "magnesis" -> item { MagnesisSimulator() }
                "stasis" -> item { StasisSimulator(viewModel) }
                "cryonis" -> item { CryonisSimulator(viewModel) }
                "sensor" -> item { SheikahSensorSimulator(viewModel) }
            }
        }
    }
}

// 1. Remote Bomb Simulator
@Composable
private fun RemoteBombSimulator(viewModel: HyruleViewModel) {
    val bombType by viewModel.bombType.collectAsState()
    val isPlaced by viewModel.isBombPlaced.collectAsState()
    val isExploding by viewModel.bombExploding.collectAsState()

    SheikahCard(borderColor = AncientOrange.copy(alpha = 0.7f)) {
        Text("REMOTE BOMB RUNES", color = AncientAmber, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Energy-based explosives that do not consume inventory arrows or powder. Cooldown timer resets immediately upon detonation.",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Type switcher (Round vs Square)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.switchBombType(BombType.ROUND) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (bombType == BombType.ROUND) SheikahCyan else SheikahSlateCardElevated
                ),
                modifier = Modifier.weight(1f).testTag("bomb_round_button")
            ) {
                Text("🔵 Round Bomb", color = if (bombType == BombType.ROUND) Color.Black else Color.White, fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.switchBombType(BombType.SQUARE) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (bombType == BombType.SQUARE) SheikahCyan else SheikahSlateCardElevated
                ),
                modifier = Modifier.weight(1f).testTag("bomb_square_button")
            ) {
                Text("🟦 Square Bomb", color = if (bombType == BombType.SQUARE) Color.Black else Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bomb Visual Stage Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF070D18))
                .border(1.dp, SheikahSlateBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isExploding) {
                // Explosion Shockwave
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, AncientOrange, GuardianRed, Color.Transparent)
                        ),
                        radius = size.minDimension * 0.45f
                    )
                }
                Text("💥 BOOM!", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            } else if (isPlaced) {
                // Placed Bomb
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = if (bombType == BombType.ROUND) CircleShape else RoundedCornerShape(8.dp),
                        color = SheikahCyan.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(2.dp, SheikahCyanBright)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(if (bombType == BombType.ROUND) "💣" else "🧊", fontSize = 28.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ARMED • READY TO DETONATE", color = SheikahCyanBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Text("Tap 'Deploy Bomb' to materialize explosive", color = Color.Gray, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action controls
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { viewModel.deployBomb() },
                enabled = !isPlaced && !isExploding,
                colors = ButtonDefaults.buttonColors(containerColor = SheikahCyan),
                modifier = Modifier.weight(1f).testTag("deploy_bomb_button")
            ) {
                Text("Deploy Bomb", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.detonateBomb() },
                enabled = isPlaced && !isExploding,
                colors = ButtonDefaults.buttonColors(containerColor = AncientOrange),
                modifier = Modifier.weight(1f).testTag("detonate_bomb_button")
            ) {
                Text("Detonate! 💥", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tactical Guide
        Text("Tactical Characteristics:", color = HylianGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text("• Round Bomb: Rolls down mountain hills, floats down river currents, spherical blast radius.", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
        Text("• Square Bomb: Rigid cubical edges prevent rolling down steep cliffs or being blown by gale winds.", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
        Text("• Damage: 24 (Base) / 12 (Outer edge). Shatters cracked rocks, cuts trees into wood piles, stuns Bokoblins.", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
    }
}

// 2. Magnesis Simulator (Draggable metallic object onto pressure plate)
@Composable
private fun MagnesisSimulator() {
    var objectOffset by remember { mutableStateOf(Offset(80f, 60f)) }
    var plateReached by remember { mutableStateOf(false) }

    SheikahCard(borderColor = Color(0xFFE53935).copy(alpha = 0.7f)) {
        Text("MAGNESIS RUNE", color = Color(0xFFFF5252), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Emits an electromagnetic beam that manipulates metallic objects. Drag the metal chest onto the ancient floor plate below!",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF090F1B))
                .border(1.dp, SheikahSlateBorder, RoundedCornerShape(12.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val plateCenter = Offset(size.width * 0.75f, size.height * 0.5f)

                // Draw Pressure Plate
                drawCircle(
                    color = if (plateReached) SheikahCyan else Color(0xFF2C3E50),
                    radius = 42.dp.toPx(),
                    center = plateCenter
                )
                drawCircle(
                    color = if (plateReached) SheikahCyanBright else Color(0xFF1A252F),
                    radius = 34.dp.toPx(),
                    center = plateCenter
                )

                // Magnesis Magnetic Glow Beam
                drawLine(
                    color = Color(0xFFFF3D00).copy(alpha = 0.65f),
                    start = Offset(40f, size.height * 0.9f),
                    end = objectOffset,
                    strokeWidth = 3.dp.toPx()
                )
            }

            // Draggable Metal Chest
            Box(
                modifier = Modifier
                    .offset(x = (objectOffset.x / 2.5).dp, y = (objectOffset.y / 2.5).dp)
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFB0BEC5))
                    .border(2.dp, Color(0xFFFF3D00), RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val newX = (objectOffset.x + dragAmount.x).coerceIn(40f, 650f)
                            val newY = (objectOffset.y + dragAmount.y).coerceIn(30f, 380f)
                            objectOffset = Offset(newX, newY)

                            // Check if chest reached the plate
                            plateReached = newX > 480f && newY > 150f && newY < 300f
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("📦", fontSize = 26.sp)
            }

            if (plateReached) {
                Surface(
                    color = SheikahCyan.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SheikahCyan),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.align(Alignment.TopCenter).padding(8.dp)
                ) {
                    Text("✓ CIRCUIT ACTIVATED • GATE OPEN", color = SheikahCyanBright, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text("• Magnesis highlights all metallic objects in bright glowing red.", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
        Text("• Can be used as a heavy wrecking ball against Bokoblin encampments for massive crush damage.", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
    }
}

// 3. Stasis Simulator (Freeze + Kinetic force accumulation)
@Composable
private fun StasisSimulator(viewModel: HyruleViewModel) {
    val isActive by viewModel.isStasisActive.collectAsState()
    val hits by viewModel.stasisHits.collectAsState()
    val timer by viewModel.stasisTimerSeconds.collectAsState()
    val isLaunched by viewModel.stasisLaunched.collectAsState()

    val forcePercent = (hits / 15f).coerceIn(0f, 1f)
    val forceColor = when {
        hits >= 10 -> GuardianRed
        hits >= 5 -> AncientOrange
        else -> HylianGold
    }

    SheikahCard(borderColor = HylianGold.copy(alpha = 0.7f)) {
        Text("STASIS+ RUNE", color = HylianGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Freezes an object or monster in space and time. Repeated kinetic blows store energy, launching it at tremendous speeds when released!",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0C1322))
                .border(1.dp, SheikahSlateBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isLaunched) {
                Text("🚀 LAUNCHED! IMPACT FORCE: ${hits * 180}N", color = HylianGold, fontSize = 16.sp, fontWeight = FontWeight.Black)
            } else if (isActive) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(68.dp),
                        shape = CircleShape,
                        color = HylianGold.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(3.dp, forceColor)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🪨", fontSize = 32.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("⏳ STASIS EXPIRES IN: ${timer}s", color = HylianGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Kinetic Hits Accumulated: $hits (Arrow: ${if (hits >= 10) "RED MAX" else if (hits >= 5) "ORANGE" else "YELLOW"})", color = forceColor, fontSize = 11.sp)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🪨", fontSize = 38.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Ancient Boulder (Free to move)", color = Color.Gray, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { viewModel.activateStasis() },
                enabled = !isActive,
                colors = ButtonDefaults.buttonColors(containerColor = HylianGold),
                modifier = Modifier.weight(1f).testTag("activate_stasis_button")
            ) {
                Text("Freeze Object (Stasis)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.hitStasisObject() },
                enabled = isActive,
                colors = ButtonDefaults.buttonColors(containerColor = forceColor),
                modifier = Modifier.weight(1f).testTag("strike_stasis_button")
            ) {
                Text("Strike! ($hits Hits) 🔨", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// 4. Cryonis Simulator
@Composable
private fun CryonisSimulator(viewModel: HyruleViewModel) {
    val pillars by viewModel.cryonisPillars.collectAsState()

    SheikahCard(borderColor = SheikahCyan.copy(alpha = 0.7f)) {
        Text("CRYONIS RUNE", color = SheikahCyanBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Erects solid pillar blocks of frozen ice out of any water surface or waterfall. You can summon up to 3 pillars simultaneously.",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Water surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF051D33), Color(0xFF0D47A1))))
                .border(1.dp, SheikahSlateBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                for (i in 1..3) {
                    val isPresent = i <= pillars
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isPresent) {
                            Surface(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(90.dp),
                                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                                color = SheikahCyan.copy(alpha = 0.6f),
                                border = androidx.compose.foundation.BorderStroke(2.dp, SheikahCyanBright)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🧊", fontSize = 28.sp)
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(20.dp)
                                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Pillar #$i", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { viewModel.spawnPillar() },
                colors = ButtonDefaults.buttonColors(containerColor = SheikahCyan),
                modifier = Modifier.weight(1f).testTag("spawn_pillar_button")
            ) {
                Text("Raise Ice Pillar ($pillars/3)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.shatterPillars() },
                enabled = pillars > 0,
                colors = ButtonDefaults.buttonColors(containerColor = SheikahSlateCardElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, SheikahCyan),
                modifier = Modifier.weight(1f).testTag("shatter_pillars_button")
            ) {
                Text("Shatter Pillars 💥", color = SheikahCyanBright, fontSize = 12.sp)
            }
        }
    }
}

// 5. Sheikah Sensor+ Simulator
@Composable
private fun SheikahSensorSimulator(viewModel: HyruleViewModel) {
    val target by viewModel.sensorTarget.collectAsState()
    val proximity by viewModel.sensorProximity.collectAsState()

    val targets = listOf("Silent Princess", "Hearty Durian", "Silver Lynel", "Treasure Chest", "Ancient Core")

    SheikahCard(borderColor = SheikahCyan.copy(alpha = 0.8f)) {
        Text("SHEIKAH SENSOR+ RADAR", color = SheikahCyanBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Locks onto any recorded Hyrule Compendium entry and emits sonic pulses with visual radar rings that amplify as you approach.",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Select Sensor Target:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(targets.size) { idx ->
                val item = targets[idx]
                FilterChip(
                    selected = target == item,
                    onClick = { viewModel.setSensorTarget(item) },
                    label = { Text(item, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SheikahCyan.copy(alpha = 0.25f),
                        selectedLabelColor = SheikahCyanBright
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Radar Canvas with pulsing rings
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF070F1C))
                .border(1.dp, SheikahSlateBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.minDimension * 0.42f

                // Radar grid rings
                for (r in 1..4) {
                    drawCircle(
                        color = SheikahCyan.copy(alpha = 0.2f),
                        radius = maxRadius * (r / 4f),
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }

                // Radar Crosshairs
                drawLine(SheikahCyan.copy(alpha = 0.2f), Offset(center.x, center.y - maxRadius), Offset(center.x, center.y + maxRadius))
                drawLine(SheikahCyan.copy(alpha = 0.2f), Offset(center.x - maxRadius, center.y), Offset(center.x + maxRadius, center.y))

                // Ping ring expanding
                drawCircle(
                    color = SheikahCyanBright.copy(alpha = proximity),
                    radius = maxRadius * proximity,
                    center = center,
                    style = Stroke(width = 2.5.dp.toPx())
                )

                // Center Beacon
                drawCircle(
                    color = SheikahCyanBright,
                    radius = 6.dp.toPx(),
                    center = center
                )
            }

            Column(
                modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TARGET: $target (${(proximity * 100).toInt()}% Signal)",
                    color = SheikahCyanBright,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("Simulate Distance Proximity:", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
        Slider(
            value = proximity,
            onValueChange = { viewModel.setSensorProximity(it) },
            colors = SliderDefaults.colors(
                thumbColor = SheikahCyanBright,
                activeTrackColor = SheikahCyan,
                inactiveTrackColor = SheikahSlateBorder
            ),
            modifier = Modifier.testTag("sensor_proximity_slider")
        )

        Button(
            onClick = { viewModel.pingRadar() },
            colors = ButtonDefaults.buttonColors(containerColor = SheikahCyan),
            modifier = Modifier.fillMaxWidth().testTag("ping_radar_button")
        ) {
            Text("Ping Sensor Radar (Audio Chime) 📡", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
