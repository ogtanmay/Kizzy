/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2026
 *  *  * DiscordBadgeSpooferEngine.kt is part of Kizzy
 *  *  * 100% Android implementation of revere-group/discord-badge-spoofer
 *  *  * Spoofs Discord Game Time and Game Variety profile badges
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_profile.spoofer

import android.util.Base64
import com.my.kizzy.preference.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

@Serializable
data class DetectableGame(
    val id: String,
    val name: String,
    val exe: String = "game.exe"
)

/**
 * Android implementation replicating revere-group/discord-badge-spoofer (spoofer.py).
 * Posts legitimate launch_game and running_game_heartbeat events to /api/v9/science.
 */
object DiscordBadgeSpooferEngine {

    private const val ME_URL = "https://discord.com/api/v9/users/@me?with_analytics_token=true"
    private const val SCIENCE_URL = "https://discord.com/api/v9/science"
    private const val PROPERTIES_URL = "https://cordapi.dolfi.es/api/v2/properties/windows"
    private const val GAMES_CDN_URL = "https://cdn.discordapp.com/detectables/games.json"

    private const val CLIENT_VERSION = "1.0.9253"
    private const val CLIENT_BUILD_NUMBER = 594031
    private const val NATIVE_BUILD_NUMBER = 88414
    private const val OS_VERSION = "10.0.26200"
    private const val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) discord/1.0.9253 Chrome/148.0.7778.280 " +
        "Electron/42.7.1 Safari/537.36"

    const val BATCH_SIZE = 50
    const val BATCH_DELAY_MS = 300L

    private val json = Json { ignoreUnknownKeys = true }

    // Fallback curated games in case network fetch is unavailable
    val curatedGames = listOf(
        DetectableGame("730", "Counter-Strike 2", "cs2.exe"),
        DetectableGame("700143435472797746", "VALORANT", "VALORANT.exe"),
        DetectableGame("356875548356771840", "League of Legends", "LeagueClient.exe"),
        DetectableGame("432980957394370572", "Minecraft", "javaw.exe"),
        DetectableGame("356869127271448576", "Grand Theft Auto V", "GTA5.exe"),
        DetectableGame("432980957394370560", "Fortnite", "FortniteClient-Win64-Shipping.exe"),
        DetectableGame("432980957394370570", "Roblox", "RobloxPlayerBeta.exe"),
        DetectableGame("535533807239987200", "Apex Legends", "r5apex.exe"),
        DetectableGame("564887309065617428", "Cyberpunk 2077", "Cyberpunk2077.exe"),
        DetectableGame("882670570775588884", "ELDEN RING", "eldenring.exe"),
        DetectableGame("740632646698663996", "Baldur's Gate 3", "bg3.exe"),
        DetectableGame("356875221079425024", "Overwatch 2", "Overwatch.exe"),
        DetectableGame("570", "Dota 2", "dota2.exe"),
        DetectableGame("1178351543159980053", "HELLDIVERS™ 2", "helldivers2.exe"),
        DetectableGame("356875883011686400", "World of Warcraft", "Wow.exe"),
        DetectableGame("1199343338521993216", "Palworld", "Palworld-Win64-Shipping.exe"),
        DetectableGame("432980957394370576", "Rust", "RustClient.exe"),
        DetectableGame("356876378417709056", "Tom Clancy's Rainbow Six Siege", "RainbowSix.exe"),
        DetectableGame("432980957394370568", "Rocket League", "RocketLeague.exe"),
        DetectableGame("432980957394370578", "Destiny 2", "destiny2.exe"),
        DetectableGame("765239994356498452", "Genshin Impact", "GenshinImpact.exe"),
        DetectableGame("1095995408466157648", "Honkai: Star Rail", "StarRail.exe"),
        DetectableGame("356874418654543872", "The Witcher 3: Wild Hunt", "witcher3.exe"),
        DetectableGame("640321289196568586", "Red Dead Redemption 2", "RDR2.exe"),
        DetectableGame("432980957394370584", "Call of Duty", "cod.exe"),
        DetectableGame("440", "Team Fortress 2", "hl2.exe"),
        DetectableGame("105600", "Terraria", "Terraria.exe"),
        DetectableGame("381210", "Dead by Daylight", "DeadByDaylight-Win64-Shipping.exe"),
        DetectableGame("230410", "Warframe", "Warframe.x64.exe"),
        DetectableGame("1172620", "Sea of Thieves", "SoTGame.exe"),
        DetectableGame("945360", "Among Us", "Among Us.exe"),
        DetectableGame("1145360", "Hades", "Hades.exe"),
        DetectableGame("1177690", "Lethal Company", "Lethal Company.exe"),
        DetectableGame("739630", "Phasmophobia", "Phasmophobia.exe"),
        DetectableGame("582010", "Monster Hunter: World", "MonsterHunterWorld.exe"),
        DetectableGame("1364780", "Street Fighter 6", "StreetFighter6.exe"),
        DetectableGame("1778820", "TEKKEN 8", "Polaris-Win64-Shipping.exe"),
        DetectableGame("2073850", "THE FINALS", "Discovery.exe"),
        DetectableGame("264710", "Subnautica", "Subnautica.exe"),
        DetectableGame("367520", "Hollow Knight", "hollow_knight.exe"),
        DetectableGame("413150", "Stardew Valley", "Stardew Valley.exe"),
        DetectableGame("1716740", "Starfield", "Starfield.exe"),
        DetectableGame("377160", "Fallout 4", "Fallout4.exe"),
        DetectableGame("489830", "The Elder Scrolls V: Skyrim Special Edition", "SkyrimSE.exe"),
        DetectableGame("1240440", "Halo Infinite", "HaloInfinite.exe"),
        DetectableGame("1551360", "Forza Horizon 5", "ForzaHorizon5.exe"),
        DetectableGame("218620", "PAYDAY 2", "payday2_win32_release.exe"),
        DetectableGame("4000", "Garry's Mod", "gmod.exe"),
        DetectableGame("550", "Left 4 Dead 2", "left4dead2.exe"),
        DetectableGame("620", "Portal 2", "portal2.exe")
    )

    /**
     * Session identity matching revere-group/discord-badge-spoofer.
     */
    data class SpooferSession(
        val heartbeatSession: String = UUID.randomUUID().toString(),
        val launchSignature: String = UUID.randomUUID().toString(),
        var sequenceNumber: Int = 0
    ) {
        fun nextSeq(): Int {
            sequenceNumber++
            return sequenceNumber
        }
    }

    /**
     * Builds default / fallback client super-properties base64 payload.
     */
    private fun defaultSuperProps(session: SpooferSession): String {
        val clientLaunchId = UUID.randomUUID().toString()
        val jsonStr = """
        {
          "os": "Windows",
          "browser": "Discord Client",
          "release_channel": "stable",
          "client_version": "$CLIENT_VERSION",
          "os_version": "$OS_VERSION",
          "os_arch": "x64",
          "app_arch": "x64",
          "system_locale": "en-GB",
          "has_client_mods": false,
          "browser_user_agent": "$USER_AGENT",
          "browser_version": "42.7.1",
          "os_sdk_version": "26200",
          "client_build_number": $CLIENT_BUILD_NUMBER,
          "native_build_number": $NATIVE_BUILD_NUMBER,
          "client_event_source": null,
          "client_app_state": "focused",
          "client_launch_id": "$clientLaunchId",
          "launch_signature": "${session.launchSignature}",
          "client_heartbeat_session_id": "${session.heartbeatSession}"
        }
        """.trimIndent().replace("\n", "").replace("  ", "")
        return Base64.encodeToString(jsonStr.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    /**
     * Fetches dynamic super properties from cordapi.dolfi.es (matching spoofer.py).
     * Falls back to defaultSuperProps if unavailable.
     */
    suspend fun getSuperProperties(session: SpooferSession): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(PROPERTIES_URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("User-Agent", USER_AGENT)
            conn.connectTimeout = 8000
            conn.readTimeout = 8000

            OutputStreamWriter(conn.outputStream).use { it.write("{}"); it.flush() }
            if (conn.responseCode in 200..299) {
                val resp = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val obj = json.parseToJsonElement(resp).jsonObject
                val props = obj["properties"]?.jsonObject
                if (props != null) {
                    val map = props.toMutableMap()
                    val clientLaunchId = UUID.randomUUID().toString()
                    val rawJson = buildString {
                        append("{")
                        var first = true
                        for ((k, v) in map) {
                            if (!first) append(",")
                            first = false
                            append("\"$k\":$v")
                        }
                        append(",\"client_launch_id\":\"$clientLaunchId\"")
                        append(",\"launch_signature\":\"${session.launchSignature}\"")
                        append(",\"client_heartbeat_session_id\":\"${session.heartbeatSession}\"")
                        append("}")
                    }
                    return@withContext Base64.encodeToString(rawJson.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
                }
            }
        } catch (_: Exception) {}
        defaultSuperProps(session)
    }

    /**
     * Fetches user's analytics token from Discord GET /users/@me?with_analytics_token=true
     */
    suspend fun fetchAnalyticsToken(token: String, superProps: String = ""): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val actualSuperProps = if (superProps.isNotBlank()) superProps else defaultSuperProps(SpooferSession())
            val url = URL(ME_URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Authorization", token)
            conn.setRequestProperty("User-Agent", USER_AGENT)
            conn.setRequestProperty("X-Super-Properties", actualSuperProps)
            conn.connectTimeout = 15000
            conn.readTimeout = 15000

            val code = conn.responseCode
            if (code in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val jsonObject = json.parseToJsonElement(response).jsonObject
                val analyticsToken = jsonObject["analytics_token"]?.jsonPrimitive?.content
                    ?: throw IllegalStateException("analytics_token missing in user profile response")
                Prefs[Prefs.ANALYTICS_TOKEN] = analyticsToken
                analyticsToken
            } else {
                val err = conn.errorStream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
                throw IllegalStateException("Failed to fetch analytics token (HTTP $code): $err")
            }
        }
    }

    /**
     * Fetches detectable games list from Discord official detectables CDN (cdn.discordapp.com/detectables/games.json)
     */
    suspend fun fetchDetectableGames(token: String = ""): List<DetectableGame> = withContext(Dispatchers.IO) {
        try {
            val url = URL(GAMES_CDN_URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "Mozilla/5.0")
            conn.connectTimeout = 15000
            conn.readTimeout = 15000

            if (conn.responseCode in 200..299) {
                val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val array = json.parseToJsonElement(response).jsonArray
                val list = mutableListOf<DetectableGame>()
                val seen = mutableSetOf<String>()
                for (item in array) {
                    val obj = item.jsonObject
                    val id = obj["id"]?.jsonPrimitive?.content ?: continue
                    if (!id.all { it.isDigit() } || seen.contains(id)) continue

                    val name = obj["name"]?.jsonPrimitive?.content ?: "Unknown"
                    val execs = obj["executables"]?.jsonArray
                    var winExe = "game.exe"
                    var hasWin32 = false
                    if (execs != null) {
                        for (e in execs) {
                            val eObj = e.jsonObject
                            if (eObj["os"]?.jsonPrimitive?.content == "win32") {
                                val eName = eObj["name"]?.jsonPrimitive?.content
                                if (!eName.isNullOrBlank()) {
                                    winExe = eName
                                    hasWin32 = true
                                    break
                                }
                            }
                        }
                    }
                    if (hasWin32) {
                        seen.add(id)
                        list.add(DetectableGame(id = id, name = name, exe = winExe))
                    }
                }
                if (list.isNotEmpty()) {
                    return@withContext list
                }
            }
        } catch (_: Exception) {}
        curatedGames
    }

    /**
     * Builds the exact 3-event session structure per game as implemented in spoofer.py:
     * 1. initial running_game_heartbeat (duration_tracked_ms: 0, ts: start)
     * 2. launch_game (ts: now)
     * 3. final running_game_heartbeat (duration_tracked_ms: duration, ts: now)
     */
    fun buildSessionEvents(
        game: DetectableGame,
        durationMs: Long,
        session: SpooferSession,
        fingerprint: String = ""
    ): List<String> {
        val now = System.currentTimeMillis()
        val start = if (now - durationMs > 0) now - durationMs else now
        val gameSessionId = UUID.randomUUID().toString()

        val escapedName = game.name.replace("\\", "\\\\").replace("\"", "\\\"")
        val escapedExe = game.exe.replace("\\", "\\\\").replace("\"", "\\\"")

        // 1. Initial heartbeat (0ms)
        val initialHbSeq = session.nextSeq()
        val initialHeartbeat = """
        {
          "type": "running_game_heartbeat",
          "properties": {
            "client_track_timestamp": $start,
            "client_heartbeat_session_id": "${session.heartbeatSession}",
            "event_sequence_number": $initialHbSeq,
            "game_id": "${game.id}",
            "game_name": "$escapedName",
            "game_metadata": null,
            "game_executable": "$escapedExe",
            "game_detection_enabled": true,
            "initial_heartbeat": true,
            "final_heartbeat": false,
            "game_session_id": "$gameSessionId",
            "duration_tracked_ms": 0,
            "rtc_connection_id": null,
            "media_session_id": null,
            "launch_signature": "${session.launchSignature}",
            "client_app_state": "focused",
            "client_send_timestamp": $start
          }
        }
        """.trimIndent()

        // 2. Launch game event
        val launchSeq = session.nextSeq()
        val fpField = if (fingerprint.isNotBlank()) ",\"executable_fingerprint\": \"$fingerprint\"" else ""
        val launchEvent = """
        {
          "type": "launch_game",
          "properties": {
            "client_track_timestamp": $now,
            "client_heartbeat_session_id": "${session.heartbeatSession}",
            "event_sequence_number": $launchSeq,
            "game": "$escapedName",
            "game_id": "${game.id}",
            "verified": true,
            "elevated": false,
            "is_launcher": false,
            "game_platform": "desktop",
            "detection_method": "verified_game",
            "is_overlay_enabled": false,
            "is_overlay_game_enabled": true,
            "is_overlay_game_source": "OOP_DEFAULT_DATABASE",
            "fullscreen_type": "UNKNOWN",
            "hardware_display_count": 1,
            "overlay_method": "Disabled",
            "activity_status_enabled": true,
            "activity_status_shared_guilds": [],
            "current_user_status": "online",
            "game_detection_enabled": true,
            "executable_path": "$escapedExe",
            "voice_channel_id": null,
            "voice_channel_type": null,
            "voice_channel_bitrate": null,
            "voice_channel_guild_id": null,
            "hidden_by_distributor": false,
            "game_metadata": null,
            "client_performance_cpu": null,
            "client_performance_memory": null,
            "cpu_core_count": null,
            "accessibility_features": 0,
            "rendered_locale": "en-GB",
            "launch_signature": "${session.launchSignature}",
            "client_rtc_state": null,
            "client_app_state": "focused",
            "client_send_timestamp": $now$fpField
          }
        }
        """.trimIndent()

        // 3. Final heartbeat (full duration)
        val finalHbSeq = session.nextSeq()
        val finalHeartbeat = """
        {
          "type": "running_game_heartbeat",
          "properties": {
            "client_track_timestamp": $now,
            "client_heartbeat_session_id": "${session.heartbeatSession}",
            "event_sequence_number": $finalHbSeq,
            "game_id": "${game.id}",
            "game_name": "$escapedName",
            "game_metadata": null,
            "game_executable": "$escapedExe",
            "game_detection_enabled": true,
            "initial_heartbeat": false,
            "final_heartbeat": true,
            "game_session_id": "$gameSessionId",
            "duration_tracked_ms": $durationMs,
            "rtc_connection_id": null,
            "media_session_id": null,
            "launch_signature": "${session.launchSignature}",
            "client_app_state": "focused",
            "client_send_timestamp": $now
          }
        }
        """.trimIndent()

        return listOf(initialHeartbeat, launchEvent, finalHeartbeat)
    }

    /**
     * Posts a batch of science events (HTTP 204 indicates accepted).
     */
    suspend fun postBatch(
        token: String,
        analyticsToken: String,
        superProps: String,
        cfClearance: String,
        eventsJsonList: List<String>
    ): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val url = URL(SCIENCE_URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true

            conn.setRequestProperty("Accept", "*/*")
            conn.setRequestProperty("Accept-Language", "en-GB")
            conn.setRequestProperty("Authorization", token)
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Origin", "https://discord.com")
            conn.setRequestProperty("Referer", "https://discord.com/channels/@me")
            conn.setRequestProperty("User-Agent", USER_AGENT)
            conn.setRequestProperty("X-Debug-Options", "bugReporterEnabled")
            conn.setRequestProperty("X-Discord-Locale", "en-GB")
            conn.setRequestProperty("X-Discord-Timezone", "Europe/Oslo")
            conn.setRequestProperty("X-Super-Properties", superProps)

            if (cfClearance.isNotBlank()) {
                conn.setRequestProperty("Cookie", "cf_clearance=${cfClearance.trim()}")
            }

            conn.connectTimeout = 15000
            conn.readTimeout = 15000

            val payloadBody = buildString {
                append("{\"token\":\"$analyticsToken\",\"events\":[")
                append(eventsJsonList.joinToString(","))
                append("]}")
            }

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(payloadBody)
                writer.flush()
            }

            val code = conn.responseCode
            if (code == 204 || code in 200..299) {
                code
            } else {
                val err = conn.errorStream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
                throw IllegalStateException("Discord Science HTTP $code: $err")
            }
        }
    }

    /**
     * Full execution flow matching revere-group/discord-badge-spoofer:
     * - Batched execution (50 games / request)
     * - Session persistence & game rotation
     * - Live status reporting
     */
    suspend fun spoofGames(
        token: String,
        cfClearance: String,
        fingerprint: String = "",
        games: List<DetectableGame>,
        hours: Float,
        isPlaytimeClaim: Boolean,
        onProgress: (current: Int, total: Int, message: String, isSuccess: Boolean) -> Unit,
        isCancelled: () -> Boolean
    ) = withContext(Dispatchers.IO) {
        val session = SpooferSession()
        onProgress(0, games.size, "Connecting and acquiring super properties...", true)

        val superProps = getSuperProperties(session)
        var analyticsToken = Prefs[Prefs.ANALYTICS_TOKEN, ""]

        // Refresh analytics token
        val tokenRes = fetchAnalyticsToken(token, superProps)
        if (tokenRes.isSuccess) {
            analyticsToken = tokenRes.getOrThrow()
            onProgress(0, games.size, "Analytics token validated (${analyticsToken.take(12)}...)", true)
        } else {
            if (analyticsToken.isBlank()) {
                onProgress(0, games.size, "Auth failed: ${tokenRes.exceptionOrNull()?.message}", false)
                return@withContext
            } else {
                onProgress(0, games.size, "Using cached analytics token", true)
            }
        }

        val durationMs = if (isPlaytimeClaim) (hours * 3600 * 1000).toLong() else 60000L
        val total = games.size
        var sentOk = 0

        val chunks = games.chunked(BATCH_SIZE)
        for ((batchIndex, chunk) in chunks.withIndex()) {
            if (isCancelled()) {
                onProgress(sentOk, total, "Operation cancelled by user", false)
                return@withContext
            }

            val batchNo = batchIndex + 1
            val batchEvents = mutableListOf<String>()
            for (game in chunk) {
                batchEvents.addAll(buildSessionEvents(game, durationMs, session, fingerprint))
            }

            val postRes = postBatch(token, analyticsToken, superProps, cfClearance, batchEvents)
            if (postRes.isSuccess) {
                val code = postRes.getOrThrow()
                sentOk += chunk.size
                val desc = if (isPlaytimeClaim) "${hours}h playtime" else "1m variety played"
                onProgress(
                    sentOk,
                    total,
                    "Batch $batchNo/$chunks.size [HTTP $code]: $sentOk/$total games credited with $desc",
                    true
                )
            } else {
                val err = postRes.exceptionOrNull()?.message ?: "Unknown error"
                onProgress(
                    sentOk,
                    total,
                    "Batch $batchNo/$chunks.size failed: $err",
                    false
                )
            }

            delay(BATCH_DELAY_MS)
        }

        // Save accumulated stats
        val prevTotalGames = Prefs[Prefs.SPOOFER_TOTAL_GAMES, 0]
        val prevTotalHours = Prefs[Prefs.SPOOFER_TOTAL_HOURS, 0f]
        Prefs[Prefs.SPOOFER_TOTAL_GAMES] = prevTotalGames + sentOk
        if (isPlaytimeClaim) {
            Prefs[Prefs.SPOOFER_TOTAL_HOURS] = prevTotalHours + (sentOk * hours)
        }

        onProgress(
            total,
            total,
            "Finished! Successfully spoofed $sentOk/$total games to Discord science telemetry. Profile badges reflect within 1-2 days.",
            sentOk > 0
        )
    }
}
