package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.ui.theme.CodeComment
import com.example.ui.theme.CodeCyan
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.StudioBlack
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.TermDotClose
import com.example.ui.theme.TermDotMin
import com.example.ui.theme.TermDotZoom
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LanguageSelectorModal(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .testTag("language_selector_modal"),
            shape = RoundedCornerShape(12.dp),
            color = StudioBlack,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TermDotClose))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TermDotMin))
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(TermDotZoom))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "export LANG=...",
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MatrixGreen
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_language_modal")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "// SELECT INTERFACE LOCALE OR DEV MODE",
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    color = CodeComment
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val isSelected = lang == currentLanguage

                        Surface(
                            onClick = {
                                onLanguageSelected(lang)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) StudioCardElevated else StudioCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) MatrixGreen else StudioBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("select_lang_${lang.code}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = lang.flagOrIcon,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = lang.displayName,
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = if (isSelected) MatrixGreen else TextPrimary
                                        )
                                        Text(
                                            text = "locale_id: ${lang.code}",
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontSize = 10.sp,
                                            color = CodeComment
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Selected",
                                        tint = MatrixGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
