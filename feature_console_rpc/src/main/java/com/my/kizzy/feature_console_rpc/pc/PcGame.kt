/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2026
 *  *  * PcGame.kt is part of Kizzy
 *  *  * Predefined PC Games catalog & platforms
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_console_rpc.pc

import com.my.kizzy.data.rpc.Constants
import com.my.kizzy.domain.model.Game

object PcGamesProvider {
    val platforms = listOf(
        "All",
        Constants.STEAM,
        Constants.EPIC_GAMES,
        Constants.RIOT_GAMES,
        Constants.BATTLENET,
        Constants.EA_APP,
        Constants.UBISOFT,
        Constants.GOG_GALAXY,
        Constants.XBOX_PC
    )

    fun platformIcon(platform: String): String = when (platform) {
        Constants.STEAM -> Constants.STEAM_LINK
        Constants.EPIC_GAMES -> Constants.EPIC_GAMES_LINK
        Constants.RIOT_GAMES -> Constants.RIOT_GAMES_LINK
        Constants.BATTLENET -> Constants.BATTLENET_LINK
        Constants.EA_APP -> Constants.EA_APP_LINK
        Constants.UBISOFT -> Constants.UBISOFT_LINK
        Constants.GOG_GALAXY -> Constants.GOG_GALAXY_LINK
        Constants.XBOX_PC -> Constants.XBOX_PC_LINK
        else -> Constants.STEAM_LINK
    }

    val defaultGames: List<Game> = listOf(
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/730/header.jpg",
            game_title = "Counter-Strike 2"
        ),
        Game(
            platform = Constants.RIOT_GAMES,
            small_image = Constants.RIOT_GAMES_LINK,
            large_image = "https://images.contentstack.io/v3/assets/bltb6530b271fddd0b1/blt060599540b798b31/6463eb4c60205561a0d8ad70/Valorant_KeyArt.jpg",
            game_title = "VALORANT"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/1091500/header.jpg",
            game_title = "Cyberpunk 2077"
        ),
        Game(
            platform = Constants.RIOT_GAMES,
            small_image = Constants.RIOT_GAMES_LINK,
            large_image = "https://images.contentstack.io/v3/assets/blt731acb42bb3d1659/blt7d2eb29b3ae3eb28/62e08cb080b4f74d0263f698/LOL_Lux_Header.jpg",
            game_title = "League of Legends"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/1245620/header.jpg",
            game_title = "ELDEN RING"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/1086940/header.jpg",
            game_title = "Baldur's Gate 3"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/271590/header.jpg",
            game_title = "Grand Theft Auto V"
        ),
        Game(
            platform = Constants.EPIC_GAMES,
            small_image = Constants.EPIC_GAMES_LINK,
            large_image = "https://cdn1.epicgames.com/offer/fn/Blade_2560x1440_2560x1440-ae5586191b22303c734daab57cc8e792",
            game_title = "Fortnite"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/570/header.jpg",
            game_title = "Dota 2"
        ),
        Game(
            platform = Constants.BATTLENET,
            small_image = Constants.BATTLENET_LINK,
            large_image = "https://blz-contentstack-images.akamaized.net/v3/assets/blt9c12f249ac15c7ec/bltb23ecf769a633dc3/6318e47be9f52f0853cfc5c9/OW2_Season1_KeyArt.jpg",
            game_title = "Overwatch 2"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/1172470/header.jpg",
            game_title = "Apex Legends"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/553850/header.jpg",
            game_title = "HELLDIVERS™ 2"
        ),
        Game(
            platform = Constants.BATTLENET,
            small_image = Constants.BATTLENET_LINK,
            large_image = "https://blz-contentstack-images.akamaized.net/v3/assets/blt9c12f249ac15c7ec/blt6d55bc81ca2901db/660c6d7fa0081d4e062292f7/WoW_TheWarWithin_KeyArt.jpg",
            game_title = "World of Warcraft"
        ),
        Game(
            platform = Constants.BATTLENET,
            small_image = Constants.BATTLENET_LINK,
            large_image = "https://blz-contentstack-images.akamaized.net/v3/assets/blt9c12f249ac15c7ec/bltfb9c47e8b6ef22e8/64627d3299723049aa8276f5/D4_KeyArt.jpg",
            game_title = "Diablo IV"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/1623730/header.jpg",
            game_title = "Palworld"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/292030/header.jpg",
            game_title = "The Witcher 3: Wild Hunt"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/1174180/header.jpg",
            game_title = "Red Dead Redemption 2"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/1145360/header.jpg",
            game_title = "Hades II"
        ),
        Game(
            platform = Constants.XBOX_PC,
            small_image = Constants.XBOX_PC_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/1551360/header.jpg",
            game_title = "Forza Horizon 5"
        ),
        Game(
            platform = Constants.XBOX_PC,
            small_image = Constants.XBOX_PC_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/1716740/header.jpg",
            game_title = "Starfield"
        ),
        Game(
            platform = Constants.UBISOFT,
            small_image = Constants.UBISOFT_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/359550/header.jpg",
            game_title = "Tom Clancy's Rainbow Six Siege"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/252490/header.jpg",
            game_title = "Rust"
        ),
        Game(
            platform = Constants.EPIC_GAMES,
            small_image = Constants.EPIC_GAMES_LINK,
            large_image = "https://cdn1.epicgames.com/offer/9773875e08404d749e44c7fb07346619/EGS_RocketLeague_PsyonixLLC_S1_2560x1440-ae2e03bf3221b6d9da49e15f237ebc0b",
            game_title = "Rocket League"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/1085660/header.jpg",
            game_title = "Destiny 2"
        ),
        Game(
            platform = Constants.STEAM,
            small_image = Constants.STEAM_LINK,
            large_image = "https://cdn.akamai.steamstatic.com/steam/apps/440/header.jpg",
            game_title = "Team Fortress 2"
        )
    )
}
