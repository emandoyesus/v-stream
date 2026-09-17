package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Programmer & Terminal Palette (GitHub Dark / Tokyo Night / CRT Matrix inspired)
val StudioBlack = Color(0xFF0A0D12)
val StudioCard = Color(0xFF131822)
val StudioCardElevated = Color(0xFF1C2331)
val StudioBorder = Color(0xFF252F40)
val StudioBorderActive = Color(0xFF58A6FF)

// Terminal & IDE Syntax Highlight Colors
val MatrixGreen = Color(0xFF00FF66)       // Prompt $, Success, Clean Stream
val MatrixGreenMuted = Color(0xFF0B2915)
val CodeCyan = Color(0xFF58A6FF)          // Identifiers, cURL, methods
val CodeCyanMuted = Color(0xFF112640)
val CodePurple = Color(0xFFBC8CFF)        // Keywords, imports, classes
val CodeYellow = Color(0xFFE3B341)        // Annotations, warnings, numbers
val CodeOrange = Color(0xFFFFA657)        // Strings, parameters
val CodeRed = Color(0xFFFF7B72)           // Errors, exit != 0, deletions
val CodeComment = Color(0xFF8B949E)       // Monospace comments // ...

// Terminal Window Controls (macOS/Linux terminal header dots)
val TermDotClose = Color(0xFFFF5F56)
val TermDotMin = Color(0xFFFFBD2E)
val TermDotZoom = Color(0xFF27C93F)

// Hero Accents
val AcidCitron = Color(0xFF00FF66)        // Matrix Neon Green as primary hero
val AcidCitronMuted = Color(0xFF0B2915)
val AcidCitronText = Color(0xFF051508)

// Secondary Accents
val SignalRose = Color(0xFFFF7B72)
val SignalCyan = Color(0xFF58A6FF)
val SignalAmber = Color(0xFFE3B341)

// Text Tiers
val TextPrimary = Color(0xFFF0F6FC)
val TextSecondary = Color(0xFF8B949E)
val TextTertiary = Color(0xFF6E7681)

// Gradients
val MatrixGlowGradient = Brush.horizontalGradient(
    listOf(Color(0xFF00FF66), Color(0xFF00DD82))
)

val CitronShine = MatrixGlowGradient

val DarkGlassGradient = Brush.verticalGradient(
    listOf(Color(0xFF161B22), Color(0xFF0D1117))
)

val HeroHeaderGradient = Brush.linearGradient(
    listOf(Color(0xFF161E2E), Color(0xFF0D1117), Color(0xFF101622))
)

