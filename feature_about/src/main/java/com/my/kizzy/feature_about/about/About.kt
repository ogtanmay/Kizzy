/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2026
 *  *  * About.kt is part of NeroX
 *  *  * Dev: Tanmay (ig: tanmahy, dc: tanmahy)
 *  *  * Repository: https://github.com/ogtanmay/Kizzy.git
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_about.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ContactSupport
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.kizzy.feature_about.BuildConfig
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.LiquidGlassCard
import com.my.kizzy.ui.components.SettingItem
import com.my.kizzy.ui.components.Subtitle
import com.my.kizzy.ui.components.preference.PreferencesHint

const val github_Repository = "https://github.com/ogtanmay/Kizzy.git"
const val github_Release = "https://github.com/ogtanmay/Kizzy/releases"
const val github_Issues = "https://github.com/ogtanmay/Kizzy/issues"
const val github_privacy_policy = "https://github.com/ogtanmay/Kizzy/blob/master/TERMS_OF_SERVICE.md"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(
    onBackPressed: () -> Unit,
    navigateToCredits: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    fun openUrl(url: String) {
        uriHandler.openUri(url)
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.about),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton { onBackPressed() } },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Developer & About Me Card in Clean Liquid Glass
            item {
                LiquidGlassCard(
                    shape = RoundedCornerShape(26.dp),
                    glowColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = "Developer",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Tanmay",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Lead Developer • NeroX",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Social Badges: IG & DC
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Instagram badge
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                                    .clickable { uriHandler.openUri("https://instagram.com/tanmahy") }
                                    .padding(vertical = 10.dp, horizontal = 12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Instagram",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "@tanmahy",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Discord handle badge
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                                    .padding(vertical = 10.dp, horizontal = 12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Discord Tag",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "tanmahy",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // GitHub & Repository Section in Liquid Glass
            item {
                LiquidGlassCard(
                    shape = RoundedCornerShape(24.dp),
                    glowColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Repository & Links",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        SettingItem(
                            title = "GitHub Repository",
                            description = "https://github.com/ogtanmay/Kizzy.git",
                            icon = Icons.Outlined.Code
                        ) {
                            openUrl(github_Repository)
                        }

                        SettingItem(
                            title = stringResource(id = R.string.github_readme),
                            description = stringResource(id = R.string.github_readme_desc),
                            icon = Icons.Outlined.Description
                        ) {
                            openUrl(github_Repository)
                        }

                        SettingItem(
                            title = stringResource(id = R.string.github_latest_release),
                            description = stringResource(id = R.string.github_latest_release_desc),
                            icon = Icons.Outlined.NewReleases
                        ) {
                            openUrl(github_Release)
                        }

                        SettingItem(
                            title = stringResource(id = R.string.github_issue),
                            description = stringResource(id = R.string.github_issue_desc),
                            icon = Icons.AutoMirrored.Outlined.ContactSupport
                        ) {
                            openUrl(github_Issues)
                        }
                    }
                }
            }

            // App Information & Credits in Liquid Glass
            item {
                LiquidGlassCard(
                    shape = RoundedCornerShape(24.dp),
                    glowColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "App Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        SettingItem(
                            title = stringResource(id = R.string.credits),
                            description = stringResource(id = R.string.credits_desc),
                            icon = Icons.Outlined.AutoAwesome
                        ) {
                            navigateToCredits()
                        }

                        SettingItem(
                            title = stringResource(id = R.string.privacy_policy),
                            description = stringResource(id = R.string.privacy_policy_desc),
                            icon = Icons.Outlined.PrivacyTip
                        ) {
                            uriHandler.openUri(github_privacy_policy)
                        }

                        SettingItem(
                            title = "Version",
                            description = "${BuildConfig.VERSION_NAME} (NeroX)",
                            icon = Icons.Outlined.Info
                        ) {}
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
