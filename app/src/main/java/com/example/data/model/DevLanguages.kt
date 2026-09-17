package com.example.data.model

/**
 * Natural and Developer Interface Languages supported across the UI.
 */
enum class AppLanguage(
    val code: String,
    val displayName: String,
    val flagOrIcon: String,
    val isDevMode: Boolean = false
) {
    DEV("DEV", "Dev / CLI Jargon", "💻", true),
    EN("EN", "English", "🇺🇸", false),
    ES("ES", "Español", "🇪🇸", false),
    FR("FR", "Français", "🇫🇷", false),
    DE("DE", "Deutsch", "🇩🇪", false),
    JA("JA", "日本語", "🇯🇵", false),
    ZH("ZH", "中文", "🇨🇳", false),
    RU("RU", "Русский", "🇷🇺", false);

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: DEV
        }
    }
}

/**
 * Programming Languages supported in the Developer Code Generator.
 */
enum class ProgrammingLanguage(
    val id: String,
    val displayName: String,
    val fileExtension: String,
    val highlightColorHex: Long
) {
    BASH_CURL("curl", "cURL / Bash", ".sh", 0xFF00FF66),
    PYTHON("python", "Python 3", ".py", 0xFF58A6FF),
    JAVASCRIPT("js", "JavaScript (Node)", ".js", 0xFFF1E05A),
    GO("go", "Go (Golang)", ".go", 0xFF00ADD8),
    RUST("rust", "Rust", ".rs", 0xFFDEA584),
    KOTLIN("kotlin", "Kotlin / Android", ".kt", 0xFFA97BFF),
    PHP("php", "PHP", ".php", 0xFF4F5D95);

    fun generateSnippet(targetUrl: String, quality: String): String {
        val safeUrl = if (targetUrl.isBlank()) "https://www.tiktok.com/@user/video/7284919283" else targetUrl
        return when (this) {
            BASH_CURL -> """
                |#!/usr/bin/env bash
                |# Fetch clean video stream without watermark
                |TARGET_URL="$safeUrl"
                |
                |echo "[*] Resolving clean media endpoint..."
                |RAW_JSON=$(curl -s -X POST "https://api.stream-pull.dev/v1/extract" \
                |  -H "User-Agent: RawStreamCLI/2.5.0 (Android; Linux x86_64)" \
                |  -H "Content-Type: application/json" \
                |  -d "{\"url\":\"${'$'}TARGET_URL\",\"quality\":\"$quality\",\"no_watermark\":true}")
                |
                |STREAM_URL=$(echo "${'$'}RAW_JSON" | grep -o '"stream_url":"[^"]*' | cut -d'"' -f4)
                |echo "[+] Clean stream located: ${'$'}STREAM_URL"
                |
                |echo "[*] Piping stream to local disk -> video_clean.mp4"
                |curl -L -o "video_clean.mp4" "${'$'}STREAM_URL" \
                |  --progress-bar \
                |  -H "Referer: https://www.tiktok.com/"
                |echo "[SUCCESS] Saved clean media file."
            """.trimMargin()

            PYTHON -> """
                |# Python 3 - Clean Stream Puller (No Watermark)
                |import requests
                |import json
                |import sys
                |
                |url = "$safeUrl"
                |headers = {
                |    "User-Agent": "Mozilla/5.0 (X11; Linux x86_64) Python/3.11",
                |    "Accept": "application/json"
                |}
                |
                |print(f"[*] Extracting media without watermark: {url}")
                |payload = {"url": url, "quality": "$quality", "strip_watermark": True}
                |
                |response = requests.post(
                |    "https://api.stream-pull.dev/v1/extract",
                |    headers=headers,
                |    json=payload,
                |    timeout=15
                |)
                |
                |if response.status_code == 200:
                |    stream_data = response.json()
                |    download_url = stream_data.get("stream_url", url)
                |    print(f"[+] Download URL: {download_url}")
                |    
                |    # Stream download directly to file
                |    with requests.get(download_url, stream=True) as r:
                |        r.raise_for_status()
                |        with open("clean_video.mp4", "wb") as f:
                |            for chunk in r.iter_content(chunk_size=8192):
                |                f.write(chunk)
                |    print("[SUCCESS] Output: clean_video.mp4")
                |else:
                |    print(f"[ERROR] HTTP {response.status_code}: {response.text}")
            """.trimMargin()

            JAVASCRIPT -> """
                |// Node.js (v18+) - Native Fetch Video Extractor
                |const targetUrl = "$safeUrl";
                |const fs = require('fs');
                |const { pipeline } = require('stream/promises');
                |
                |async function extractAndDownload() {
                |  console.log(`[INIT] Processing target URL: ${targetUrl}`);
                |  
                |  const res = await fetch('https://api.stream-pull.dev/v1/extract', {
                |    method: 'POST',
                |    headers: { 'Content-Type': 'application/json' },
                |    body: JSON.stringify({
                |      url: targetUrl,
                |      quality: '$quality',
                |      watermark: false
                |    })
                |  });
                |  
                |  const data = await res.json();
                |  console.log('[OK] Resolved stream URL:', data.stream_url);
                |  
                |  // Download binary stream
                |  const mediaStream = await fetch(data.stream_url);
                |  const fileStream = fs.createWriteStream('./clean_media.mp4');
                |  await pipeline(mediaStream.body, fileStream);
                |  console.log('[SUCCESS] Saved clean_media.mp4 (1080p clean)');
                |}
                |
                |extractAndDownload().catch(console.error);
            """.trimMargin()

            GO -> """
                |package main
                |
                |import (
                |	"bytes"
                |	"encoding/json"
                |	"fmt"
                |	"io"
                |	"net/http"
                |	"os"
                |)
                |
                |func main() {
                |	targetUrl := "$safeUrl"
                |	fmt.Println("[GO-CLI] Resolving clean video stream...")
                |
                |	reqBody, _ := json.Marshal(map[string]interface{}{
                |		"url":       targetUrl,
                |		"quality":   "$quality",
                |		"no_wm":     true,
                |	})
                |
                |	resp, err := http.Post("https://api.stream-pull.dev/v1/extract", "application/json", bytes.NewBuffer(reqBody))
                |	if err != nil {
                |		panic(err)
                |	}
                |	defer resp.Body.Close()
                |
                |	var result map[string]interface{}
                |	json.NewDecoder(resp.Body).Decode(&result)
                |	streamURL := result["stream_url"].(string)
                |	fmt.Printf("[+] Stream target: %s\n", streamURL)
                |
                |	// Download to disk
                |	fileResp, _ := http.Get(streamURL)
                |	defer fileResp.Body.Close()
                |	out, _ := os.Create("download_clean.mp4")
                |	defer out.Close()
                |	io.Copy(out, fileResp.Body)
                |	fmt.Println("[SUCCESS] Saved download_clean.mp4")
                |}
            """.trimMargin()

            RUST -> """
                |// Rust (Cargo dependencies: reqwest = { version = "0.11", features = ["json", "blocking"] })
                |use std::fs::File;
                |use std::io::copy;
                |
                |fn main() -> Result<(), Box<dyn std::error::Error>> {
                |    let url = "$safeUrl";
                |    println!("[RUST] Demuxing clean stream from: {}", url);
                |
                |    let client = reqwest::blocking::Client::new();
                |    let res = client.post("https://api.stream-pull.dev/v1/extract")
                |        .json(&serde_json::json!({
                |            "url": url,
                |            "quality": "$quality",
                |            "strip_watermark": true
                |        }))
                |        .send()?
                |        .json::<serde_json::Value>()?;
                |
                |    let stream_url = res["stream_url"].as_str().unwrap_or(url);
                |    println!("[OK] Found clean endpoint: {}", stream_url);
                |
                |    let mut resp = client.get(stream_url).send()?;
                |    let mut dest = File::create("output_clean.mp4")?;
                |    copy(&mut resp, &mut dest)?;
                |    println!("[FINISHED] output_clean.mp4 written successfully.");
                |    Ok(())
                |}
            """.trimMargin()

            KOTLIN -> """
                |// Kotlin / Android OkHttp Stream Pipe
                |import okhttp3.OkHttpClient
                |import okhttp3.Request
                |import okhttp3.RequestBody.Companion.toRequestBody
                |import okhttp3.MediaType.Companion.toMediaType
                |import java.io.File
                |import java.io.FileOutputStream
                |
                |fun downloadCleanStream() {
                |    val targetUrl = "$safeUrl"
                |    val client = OkHttpClient()
                |    
                |    val json = \"\"\"{"url": "${'$'}targetUrl", "no_watermark": true}\"\"\"
                |    val request = Request.Builder()
                |        .url("https://api.stream-pull.dev/v1/extract")
                |        .post(json.toRequestBody("application/json".toMediaType()))
                |        .build()
                |        
                |    client.newCall(request).execute().use { response ->
                |        println("[KOTLIN] Stream resolved: HTTP ${'$'}{response.code}")
                |        val file = File("clean_video.mp4")
                |        response.body?.byteStream()?.use { input ->
                |            FileOutputStream(file).use { output -> input.copyTo(output) }
                |        }
                |        println("[SUCCESS] Written ${'$'}{file.length()} bytes.")
                |    }
                |}
            """.trimMargin()

            PHP -> """
                |<?php
                |// PHP cURL Clean Stream Downloader
                |${'$'}targetUrl = "$safeUrl";
                |${'$'}payload = json_encode([
                |    'url' => ${'$'}targetUrl,
                |    'quality' => '$quality',
                |    'no_watermark' => true
                |]);
                |
                |${'$'}ch = curl_init('https://api.stream-pull.dev/v1/extract');
                |curl_setopt(${'$'}ch, CURLOPT_RETURNTRANSFER, true);
                |curl_setopt(${'$'}ch, CURLOPT_POST, true);
                |curl_setopt(${'$'}ch, CURLOPT_POSTFIELDS, ${'$'}payload);
                |curl_setopt(${'$'}ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
                |${'$'}response = curl_exec(${'$'}ch);
                |curl_close(${'$'}ch);
                |
                |${'$'}data = json_decode(${'$'}response, true);
                |${'$'}streamUrl = ${'$'}data['stream_url'] ?? ${'$'}targetUrl;
                |
                |echo "[*] Downloading: " . ${'$'}streamUrl . PHP_EOL;
                |file_put_contents("clean_video.mp4", fopen(${'$'}streamUrl, 'r'));
                |echo "[SUCCESS] Saved to clean_video.mp4" . PHP_EOL;
                |?>
            """.trimMargin()
        }
    }
}

/**
 * Internationalized UI Strings for all supported languages.
 */
data class UiStrings(
    val appTitle: String,
    val appSubtitle: String,
    val tabExtractor: String,
    val tabVault: String,
    val tabProtocol: String,
    val inputHint: String,
    val btnPaste: String,
    val btnExtract: String,
    val btnDownloading: String,
    val btnDownloadClean: String,
    val btnCancel: String,
    val sampleTiktok: String,
    val sampleInstagram: String,
    val statusReady: String,
    val statusCleanBadge: String,
    val audioTrackLabel: String,
    val resolutionLabel: String,
    val vaultEmptyTitle: String,
    val vaultEmptyDesc: String,
    val btnViewVault: String,
    val btnCodeSnippet: String,
    val filterAll: String,
    val filterTiktok: String,
    val filterInstagram: String,
    val terminalHeader: String
) {
    val tabDownloader: String get() = tabExtractor
    val vaultTitle: String get() = tabVault
    val vaultEmptySubtitle: String get() = vaultEmptyDesc
    val guideTitle: String get() = "man 1 extract-stream"
    val guideSubtitle: String get() = "CLI parameters, options, and SDK integration guide."
}


object LanguageDictionary {
    fun getStrings(lang: AppLanguage): UiStrings {
        return when (lang) {
            AppLanguage.DEV -> UiStrings(
                appTitle = "STREAM_DEMUX_CLI",
                appSubtitle = "bash // no-watermark // h264_aac_1080p",
                tabExtractor = "$ ./EXTRACT",
                tabVault = "/var/vault",
                tabProtocol = "man 1 protocol",
                inputHint = "user@box:~$ ./pull --url <paste_link> --no-wm",
                btnPaste = "$ STDIN PASTE",
                btnExtract = "↵ EXEC / PARSE",
                btnDownloading = "SYNCING BUFFER...",
                btnDownloadClean = "⬇ PIPE TO DISK (NO-WM)",
                btnCancel = "SIGINT / KILL",
                sampleTiktok = "--test tiktok",
                sampleInstagram = "--test reels",
                statusReady = "STREAM_READY // 200 OK",
                statusCleanBadge = "NULL_WATERMARK",
                audioTrackLabel = "AUDIO_STREAM",
                resolutionLabel = "BITRATE & CODEC",
                vaultEmptyTitle = "404: /var/vault EMPTY",
                vaultEmptyDesc = "Zero media streams downloaded in local filesystem storage buffer.",
                btnViewVault = "$ cd /var/vault",
                btnCodeSnippet = "</> CODE GENERATOR",
                filterAll = "FILTER: ALL",
                filterTiktok = "PLATFORM: TIKTOK",
                filterInstagram = "PLATFORM: INSTAGRAM",
                terminalHeader = "root@v-stream:~ (bash - 80x24 - utf8)"
            )

            AppLanguage.EN -> UiStrings(
                appTitle = "Stream Extractor",
                appSubtitle = "TikTok & Reels // HD Watermark-Free",
                tabExtractor = "EXTRACTOR",
                tabVault = "VAULT",
                tabProtocol = "GUIDE",
                inputHint = "Paste TikTok or Instagram Reel link here...",
                btnPaste = "PASTE",
                btnExtract = "RESOLVE STREAM",
                btnDownloading = "DOWNLOADING...",
                btnDownloadClean = "DOWNLOAD (NO WATERMARK)",
                btnCancel = "CANCEL",
                sampleTiktok = "TikTok Sample",
                sampleInstagram = "Instagram Sample",
                statusReady = "STREAM PARSED & READY",
                statusCleanBadge = "NO WATERMARK",
                audioTrackLabel = "AUDIO TRACK",
                resolutionLabel = "AVAILABLE STREAMS",
                vaultEmptyTitle = "No Videos in Vault",
                vaultEmptyDesc = "Download videos without watermark to store them in your offline vault.",
                btnViewVault = "GO TO VAULT",
                btnCodeSnippet = "CODE GENERATOR",
                filterAll = "ALL MEDIA",
                filterTiktok = "TIKTOK",
                filterInstagram = "INSTAGRAM",
                terminalHeader = "terminal - stream-puller v2.5"
            )

            AppLanguage.ES -> UiStrings(
                appTitle = "Extractor de Video",
                appSubtitle = "TikTok e Instagram // Sin Marca de Agua",
                tabExtractor = "EXTRACTOR",
                tabVault = "BAÚL",
                tabProtocol = "GUÍA",
                inputHint = "Pega aquí el enlace de TikTok o Instagram...",
                btnPaste = "PEGAR",
                btnExtract = "ANALIZAR ENLACE",
                btnDownloading = "DESCARGANDO...",
                btnDownloadClean = "DESCARGAR SIN MARCA",
                btnCancel = "CANCELAR",
                sampleTiktok = "Ejemplo TikTok",
                sampleInstagram = "Ejemplo Reels",
                statusReady = "STREAM LISTO (200 OK)",
                statusCleanBadge = "SIN MARCA",
                audioTrackLabel = "PISTA DE AUDIO",
                resolutionLabel = "RESOLUCIONES",
                vaultEmptyTitle = "Baúl Vacío",
                vaultEmptyDesc = "Descarga videos limpios para guardarlos en tu almacenamiento sin conexión.",
                btnViewVault = "IR AL BAÚL",
                btnCodeSnippet = "GENERAR CÓDIGO",
                filterAll = "TODOS",
                filterTiktok = "TIKTOK",
                filterInstagram = "INSTAGRAM",
                terminalHeader = "consola - stream-puller v2.5"
            )

            AppLanguage.FR -> UiStrings(
                appTitle = "Extracteur de Flux",
                appSubtitle = "TikTok & Instagram // Sans Filigrane",
                tabExtractor = "EXTRACTEUR",
                tabVault = "COFFRE",
                tabProtocol = "GUIDE",
                inputHint = "Collez le lien TikTok ou Instagram ici...",
                btnPaste = "COLLER",
                btnExtract = "ANALYSER LE LIEN",
                btnDownloading = "TÉLÉCHARGEMENT...",
                btnDownloadClean = "TÉLÉCHARGER SANS FILIGRANE",
                btnCancel = "ANNULER",
                sampleTiktok = "Exemple TikTok",
                sampleInstagram = "Exemple Reels",
                statusReady = "FLUX PRÊT (200 OK)",
                statusCleanBadge = "SANS FILIGRANE",
                audioTrackLabel = "PISTE AUDIO",
                resolutionLabel = "RÉSOLUTIONS",
                vaultEmptyTitle = "Coffre Vide",
                vaultEmptyDesc = "Téléchargez des vidéos sans filigrane pour les conserver hors ligne.",
                btnViewVault = "VOIR LE COFFRE",
                btnCodeSnippet = "GÉNÉRATEUR DE CODE",
                filterAll = "TOUT",
                filterTiktok = "TIKTOK",
                filterInstagram = "INSTAGRAM",
                terminalHeader = "terminal - stream-puller v2.5"
            )

            AppLanguage.DE -> UiStrings(
                appTitle = "Stream Extractor",
                appSubtitle = "TikTok & Reels // Ohne Wasserzeichen",
                tabExtractor = "EXTRAKTOR",
                tabVault = "TRESOR",
                tabProtocol = "ANLEITUNG",
                inputHint = "TikTok oder Instagram Reel Link hier einfügen...",
                btnPaste = "EINFÜGEN",
                btnExtract = "STREAM LADEN",
                btnDownloading = "DOWNLOAD LÄUFT...",
                btnDownloadClean = "OHNE WASSERZEICHEN SPEICHERN",
                btnCancel = "ABBRECHEN",
                sampleTiktok = "TikTok Demo",
                sampleInstagram = "Reels Demo",
                statusReady = "STREAM BEREIT (200 OK)",
                statusCleanBadge = "OHNE WASSERZEICHEN",
                audioTrackLabel = "AUDIO-SPUR",
                resolutionLabel = "AUFLÖSUNGEN",
                vaultEmptyTitle = "Tresor ist Leer",
                vaultEmptyDesc = "Keine Videos im lokalen Offline-Speicher vorhanden.",
                btnViewVault = "ZUM TRESOR",
                btnCodeSnippet = "CODE-GENERATOR",
                filterAll = "ALLE",
                filterTiktok = "TIKTOK",
                filterInstagram = "INSTAGRAM",
                terminalHeader = "konsole - stream-puller v2.5"
            )

            AppLanguage.JA -> UiStrings(
                appTitle = "ストリーム抽出器",
                appSubtitle = "TikTok & リール // 透かしなしHD保存",
                tabExtractor = "抽出コンソール",
                tabVault = "保存庫",
                tabProtocol = "仕様書",
                inputHint = "TikTokまたはInstagramのURLを貼り付け...",
                btnPaste = "貼り付け",
                btnExtract = "解析実行 [EXEC]",
                btnDownloading = "ダウンロード中...",
                btnDownloadClean = "透かしなしで保存 (HD)",
                btnCancel = "中断",
                sampleTiktok = "TikTok サンプル",
                sampleInstagram = "Reels サンプル",
                statusReady = "ストリーム準備完了 (200 OK)",
                statusCleanBadge = "透かし除去済",
                audioTrackLabel = "オーディオトラック",
                resolutionLabel = "利用可能な品質",
                vaultEmptyTitle = "保存庫が空です",
                vaultEmptyDesc = "透かしなしの動画をダウンロードしてオフラインで保存します。",
                btnViewVault = "保存庫を見る",
                btnCodeSnippet = "コード生成器",
                filterAll = "すべて",
                filterTiktok = "TIKTOK",
                filterInstagram = "INSTAGRAM",
                terminalHeader = "ターミナル - stream-puller v2.5"
            )

            AppLanguage.ZH -> UiStrings(
                appTitle = "媒体流解析器",
                appSubtitle = "TikTok 与 Reels // 高清无水印提取",
                tabExtractor = "解析器",
                tabVault = "存储库",
                tabProtocol = "使用指南",
                inputHint = "在此粘贴 TikTok 或 Instagram 链接...",
                btnPaste = "粘贴",
                btnExtract = "解析视频 [EXEC]",
                btnDownloading = "正在下载流...",
                btnDownloadClean = "下载无水印高清视频",
                btnCancel = "终止",
                sampleTiktok = "TikTok 示例",
                sampleInstagram = "Reels 示例",
                statusReady = "流解析成功 (200 OK)",
                statusCleanBadge = "无水印纯净流",
                audioTrackLabel = "独立音轨",
                resolutionLabel = "清晰度选项",
                vaultEmptyTitle = "本地库为空",
                vaultEmptyDesc = "下载无水印视频将保存在此处，支持离线随时观看。",
                btnViewVault = "查看存储库",
                btnCodeSnippet = "多语言代码生成",
                filterAll = "全部",
                filterTiktok = "TIKTOK",
                filterInstagram = "INSTAGRAM",
                terminalHeader = "终端控制台 - stream-puller v2.5"
            )

            AppLanguage.RU -> UiStrings(
                appTitle = "Stream Extractor",
                appSubtitle = "TikTok и Reels // Без водяного знака",
                tabExtractor = "ЭКСТРАКТОР",
                tabVault = "ХРАНИЛИЩЕ",
                tabProtocol = "ПРОТОКОЛ",
                inputHint = "Вставьте ссылку на TikTok или Instagram Reel...",
                btnPaste = "ВСТАВИТЬ",
                btnExtract = "АНАЛИЗ ССЫЛКИ",
                btnDownloading = "ЗАГРУЗКА...",
                btnDownloadClean = "СКАЧАТЬ БЕЗ ВОДЯНОГО ЗНАКА",
                btnCancel = "ОТМЕНА",
                sampleTiktok = "TikTok Пример",
                sampleInstagram = "Reels Пример",
                statusReady = "ПОТОК ГОТОВ (200 OK)",
                statusCleanBadge = "БЕЗ ВОДЯНОГО ЗНАКА",
                audioTrackLabel = "АУДИО ДОРОЖКА",
                resolutionLabel = "РАЗРЕШЕНИЯ",
                vaultEmptyTitle = "Хранилище пусто",
                vaultEmptyDesc = "Скачивайте видео без водяных знаков для офлайн-просмотра.",
                btnViewVault = "В ХРАНИЛИЩЕ",
                btnCodeSnippet = "ГЕНЕРАТОР КОДА",
                filterAll = "ВСЕ",
                filterTiktok = "TIKTOK",
                filterInstagram = "INSTAGRAM",
                terminalHeader = "терминал - stream-puller v2.5"
            )
        }
    }
}
