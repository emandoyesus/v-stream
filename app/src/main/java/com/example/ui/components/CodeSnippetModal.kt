package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ProgrammingLanguage
import com.example.ui.theme.CodeComment
import com.example.ui.theme.CodeCyan
import com.example.ui.theme.CodeOrange
import com.example.ui.theme.CodePurple
import com.example.ui.theme.CodeYellow
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.StudioBlack
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioBorderActive
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.TermDotClose
import com.example.ui.theme.TermDotMin
import com.example.ui.theme.TermDotZoom
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun CodeSnippetModal(
    targetUrl: String,
    quality: String = "1080p",
    onDismiss: () -> Unit
) {
    var selectedLanguage by remember { mutableStateOf(ProgrammingLanguage.BASH_CURL) }
    val context = LocalContext.current
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()

    val codeText = remember(selectedLanguage, targetUrl, quality) {
        selectedLanguage.generateSnippet(targetUrl, quality)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .testTag("code_snippet_modal"),
            shape = RoundedCornerShape(12.dp),
            color = StudioBlack,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Terminal Title Bar Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Terminal macOS dots
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TermDotClose))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TermDotMin))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TermDotZoom))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "code_generator${selectedLanguage.fileExtension}",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MatrixGreen
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_code_modal")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "// PROGRAMMING LANGUAGES (API SDK SNIPPETS)",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = CodeComment,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Language Selector Tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ProgrammingLanguage.entries) { lang ->
                        val isSelected = lang == selectedLanguage
                        Surface(
                            onClick = { selectedLanguage = lang },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) StudioCardElevated else StudioCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) MatrixGreen else StudioBorder
                            ),
                            modifier = Modifier.testTag("lang_tab_${lang.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(lang.highlightColorHex))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = lang.displayName,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(lang.highlightColorHex) else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Code Editor Container with Line Numbers
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp, max = 340.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(StudioCard)
                        .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                ) {
                    val lines = remember(codeText) { codeText.lines() }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(verticalScroll)
                    ) {
                        // Gutter: Line numbers
                        Column(
                            modifier = Modifier
                                .background(StudioCardElevated.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 12.dp)
                        ) {
                            lines.indices.forEach { index ->
                                Text(
                                    text = (index + 1).toString().padStart(2, '0'),
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 11.sp,
                                    color = TextTertiary,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        // Code Body (Horizontal scrollable)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .horizontalScroll(horizontalScroll)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = formatCodeSyntax(codeText),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 11.sp,
                                lineHeight = 18.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Bar: Copy Code Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FORMAT: UTF-8 // LF",
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 10.sp,
                        color = TextTertiary
                    )

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("${selectedLanguage.displayName} snippet", codeText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MatrixGreen,
                            contentColor = StudioBlack
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("copy_code_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "COPY ${selectedLanguage.displayName.uppercase()}",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Lightweight syntax highlighter applying IDE colors to code lines.
 */
private fun formatCodeSyntax(rawCode: String) = buildAnnotatedString {
    val lines = rawCode.lines()
    lines.forEachIndexed { i, line ->
        val trimmed = line.trimStart()
        when {
            trimmed.startsWith("#") || trimmed.startsWith("//") || trimmed.startsWith("/*") -> {
                withStyle(SpanStyle(color = CodeComment)) {
                    append(line)
                }
            }
            trimmed.startsWith("import ") || trimmed.startsWith("package ") || trimmed.startsWith("const ") ||
                    trimmed.startsWith("func ") || trimmed.startsWith("fn ") || trimmed.startsWith("val ") ||
                    trimmed.startsWith("var ") || trimmed.startsWith("let ") || trimmed.startsWith("echo ") ||
                    trimmed.startsWith("async ") || trimmed.startsWith("if ") || trimmed.startsWith("with ") -> {
                val firstWord = line.takeWhile { !it.isWhitespace() }
                withStyle(SpanStyle(color = CodePurple, fontWeight = FontWeight.Bold)) {
                    append(firstWord)
                }
                val rest = line.removePrefix(firstWord)
                // Color strings in rest
                highlightStrings(rest)
            }
            else -> {
                highlightStrings(line)
            }
        }
        if (i < lines.size - 1) append("\n")
    }
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.highlightStrings(text: String) {
    var inQuote = false
    var quoteChar = '"'
    val buffer = StringBuilder()

    for (ch in text) {
        if (!inQuote && (ch == '"' || ch == '\'')) {
            if (buffer.isNotEmpty()) {
                append(buffer.toString())
                buffer.clear()
            }
            inQuote = true
            quoteChar = ch
            buffer.append(ch)
        } else if (inQuote && ch == quoteChar) {
            buffer.append(ch)
            withStyle(SpanStyle(color = CodeOrange)) {
                append(buffer.toString())
            }
            buffer.clear()
            inQuote = false
        } else {
            buffer.append(ch)
        }
    }
    if (buffer.isNotEmpty()) {
        if (inQuote) {
            withStyle(SpanStyle(color = CodeOrange)) {
                append(buffer.toString())
            }
        } else {
            append(buffer.toString())
        }
    }
}
