plugins {
    id ("kizzy.android.library")
    id ("kizzy.android.library.compose")
    id ("kizzy.android.feature")
    id ("kizzy.android.hilt")
}

android {
    namespace = "com.my.kizzy.feature_profile"
}

dependencies {
    implementation (projects.theme)
    implementation (projects.gateway)
    implementation (projects.common.preference)
    implementation (projects.common.components)
    implementation (projects.common.resources)
    implementation (libs.coil)
    implementation (libs.activity.compose)
    implementation (libs.material.icons.extended)
    implementation (libs.kotlinx.serialization.json)
    implementation (libs.bundles.network.ktor)
    implementation (libs.ktor.content.negotiation)
}