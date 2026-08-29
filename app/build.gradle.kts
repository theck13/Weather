import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.3.9"
    id("com.google.dagger.hilt.android")
    id("androidx.room")
    kotlin("plugin.serialization") version "2.3.21"
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

val keystore = file(localProps.getProperty("KEYSTORE_LOCATION") ?: System.getenv("KEYSTORE_LOCATION") ?: "../keystore/release.jks")

val developer_domain = localProps.getProperty("DEVELOPER_DOMAIN") ?: System.getenv("DEVELOPER_DOMAIN") ?: "com.developer"
val developer_email = localProps.getProperty("DEVELOPER_EMAIL") ?: System.getenv("DEVELOPER_EMAIL") ?: "developer@email.com"
val developer_name = localProps.getProperty("DEVELOPER_NAME") ?: System.getenv("DEVELOPER_NAME") ?: "Developer"

val source_accu_weather = localProps.getProperty("SOURCE_ACCU_WEATHER") ?: System.getenv("SOURCE_ACCU_WEATHER") ?: ""
val source_geo_username = localProps.getProperty("SOURCE_GEO_USERNAME") ?: System.getenv("SOURCE_GEO_USERNAME") ?: ""
val source_google_pixel = localProps.getProperty("SOURCE_GOOGLE_PIXEL") ?: System.getenv("SOURCE_GOOGLE_PIXEL") ?: ""
val source_meteo_france = localProps.getProperty("SOURCE_METEO_FRANCE") ?: System.getenv("SOURCE_METEO_FRANCE") ?: ""
val source_open_weather = localProps.getProperty("SOURCE_OPEN_WEATHER") ?: System.getenv("SOURCE_OPEN_WEATHER") ?: ""
val source_pirate_booty = localProps.getProperty("SOURCE_PIRATE_BOOTY") ?: System.getenv("SOURCE_PIRATE_BOOTY") ?: ""

val package_name = "${developer_domain}.weather"
val version_code = (project.property("VERSION_CODE") as String).toInt()
val version_name = project.property("VERSION_NAME") as String

android {
    android.buildFeatures.buildConfig = true
    namespace = package_name

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = package_name
        minSdk = 26
        targetSdk = 37
        versionCode = version_code
        versionName = version_name

        buildConfigField(
            "String",
            "DEVELOPER_DOMAIN",
            "\"$developer_domain\""
        )

        buildConfigField(
            "String",
            "DEVELOPER_EMAIL",
            "\"$developer_email\""
        )

        buildConfigField(
            "String",
            "DEVELOPER_NAME",
            "\"$developer_name\""
        )

        buildConfigField(
            "String",
            "SOURCE_ACCU_WEATHER",
            "\"$source_accu_weather\""
        )

        buildConfigField(
            "String",
            "SOURCE_GEO_USERNAME",
            "\"$source_geo_username\""
        )

        buildConfigField(
            "String",
            "SOURCE_GOOGLE_PIXEL",
            "\"$source_google_pixel\""
        )

        buildConfigField(
            "String",
            "SOURCE_METEO_FRANCE",
            "\"$source_meteo_france\""
        )

        buildConfigField(
            "String",
            "SOURCE_OPEN_WEATHER",
            "\"$source_open_weather\""
        )

        buildConfigField(
            "String",
            "SOURCE_PIRATE_BOOTY",
            "\"$source_pirate_booty\""
        )
    }

    signingConfigs {
        if (keystore.exists()) {
            create("release") {
                storeFile = keystore
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }

        release {
            isMinifyEnabled = false

            if (keystore.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
    bundle {
        language {
            enableSplit = false
        }
    }
}

androidComponents {
    onVariants { variant ->
        variant.outputs.onEach { output ->
            if (output is com.android.build.api.variant.impl.VariantOutputImpl) {
                output.outputFileName = "${package_name}-${version_code}(${version_name}).apk"
            }
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.materialKolor)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.common.suncalc)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)

//    coreLibraryDesugaring(libs.desugar.jdk.libs) // Not sure but it causes timezone crashes on some devices
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.converter.jaxb)
    ksp(libs.room.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compiler)
    implementation(libs.reorderable)
    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.animation.graphics)
    implementation(libs.core.splashscreen)

    implementation(libs.androidx.work.runtime)
    implementation(libs.hilt.work)
    ksp(libs.hilt.metadata.jvm)

    implementation(libs.androidx.glance)

    implementation(libs.androidx.glance.appwidget)

    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
