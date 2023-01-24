package com.pubnub.components.buildsrc

object Versions {
    const val ktLint = "0.40.0"
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.4.0"
    const val ktLint = "com.pinterest:ktlint:${Versions.ktLint}"
    const val jdkDesugar = "com.android.tools:desugar_jdk_libs:2.0.0"
    const val gradleMavenPublishPlugin = "com.vanniktech:gradle-maven-publish-plugin:0.23.2"
    const val gradleVersionsPlugin = "com.github.ben-manes:gradle-versions-plugin:0.44.0"

    object Build {
        object Android {
            const val compileSdk = 33
            const val minSdk = 21
            const val targetSdk = 33

            const val buildToolsVersion = "30.0.3"
            const val versionCode = 1
            const val versionName = "0.6.2-rc12"
        }

        object Kotlin {
            const val jvmTarget = "1.8"
        }
    }

    object Accompanist {
        private const val version = "0.28.0"

        const val swipeRefresh = "com.google.accompanist:accompanist-swiperefresh:$version"
        const val placeholder = "com.google.accompanist:accompanist-placeholder:$version"
        const val navigation = "com.google.accompanist:accompanist-navigation-animation:$version"
        const val flowLayout = "com.google.accompanist:accompanist-flowlayout:$version"
    }

    object Coil {
        private const val version = "2.2.2"

        const val coil = "io.coil-kt:coil-compose:$version"
        const val coilGif = "io.coil-kt:coil-gif:$version"
        const val coilSvg = "io.coil-kt:coil-svg:$version"
        const val coilVideo = "io.coil-kt:coil-video:$version"
    }

    object Kotlin {
        private const val version = "1.8.0"

        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
        const val test = "org.jetbrains.kotlin:kotlin-test"

        object Coroutines {
            private const val version = "1.6.4"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
            const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        }
    }

    object Google {
        const val gson = "com.google.code.gson:gson:2.10.1"
    }

    object AndroidX {
        object Activity {
            const val activityCompose = "androidx.activity:activity-compose:1.6.1"
        }

        const val appcompat = "androidx.appcompat:appcompat:1.6.0"

        const val navigation = "androidx.navigation:navigation-compose:2.4.0"

        const val splashscreen = "androidx.core:core-splashscreen:1.0.0"

        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"

        object Compose {
            const val snapshot = ""
            const val compilerVersion = "1.4.0"

            const val bom = "androidx.compose:compose-bom:2023.01.00"
            const val runtime = "androidx.compose.runtime:runtime"
            const val runtimeLivedata = "androidx.compose.runtime:runtime-livedata"
            const val material = "androidx.compose.material:material"
            const val iconsCore = "androidx.compose.material:material-icons-core"
            const val iconsExtended = "androidx.compose.material:material-icons-extended"
            const val foundation = "androidx.compose.foundation:foundation"
            const val layout = "androidx.compose.foundation:foundation-layout"
            const val tooling = "androidx.compose.ui:ui-tooling"
            const val animation = "androidx.compose.animation:animation"
            const val uiTest = "androidx.compose.ui:ui-test-junit4"
        }

        object Lifecycle {
            private const val version = "2.5.1"
            const val viewModelCompose =
                "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
            const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }

        object Paging {
            private const val version = "3.1.1"
            const val runtime = "androidx.paging:paging-runtime-ktx:$version"
            const val extension = "androidx.paging:paging-compose:1.0.0-alpha14"
        }

        object Room {
            private const val version = "2.5.0"
            const val runtime = "androidx.room:room-runtime:$version"
            const val compiler = "androidx.room:room-compiler:$version"
            const val paging = "androidx.room:room-paging:$version"
            const val ktx = "androidx.room:room-ktx:$version"

            object Test {
                const val testing = "androidx.room:room-testing:$version"
            }
        }

        object Test {
            private const val version = "1.5.0"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"
            const val core = "androidx.test:core-ktx:$version"

            object Ext {
                const val junit = "androidx.test.ext:junit-ktx:1.1.5"
            }

            const val espressoCore = "androidx.test.espresso:espresso-core:3.5.1"
        }
    }

    object Hilt {
        private const val version = "2.44.2"

        const val gradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$version"
        const val android = "com.google.dagger:hilt-android:$version"
        const val compiler = "com.google.dagger:hilt-compiler:$version"
        const val testing = "com.google.dagger:hilt-android-testing:$version"
    }

    object JUnit {
        private const val version = "4.13.2"
        const val junit = "junit:junit:$version"
    }

    object PubNub {
        private const val version = "0.2.1"
        const val bom = "com.pubnub:pubnub-kotlin-bom:$version"
        const val kotlin = "com.pubnub:pubnub-kotlin"
        const val memberships = "com.pubnub:pubnub-memberships"
    }

    object JakeWharton {
        private const val version = "5.0.1"
        const val timber = "com.jakewharton.timber:timber:$version"
    }

    object Mockito {
        private const val version = "3.1.0"
        const val mockito = "org.mockito.kotlin:mockito-kotlin:$version"
    }

    object MockK {
        private const val version = "1.13.3"
        const val unit = "io.mockk:mockk:$version"
        const val instrumented = "io.mockk:mockk-android:$version"
    }

    object Awaitility {
        private const val version = "4.2.0"
        const val kotlin = "org.awaitility:awaitility-kotlin:$version"
    }

    object Hamcrest {
        private const val version = "2.1"
        const val hamcrest = "org.hamcrest:hamcrest:$version"
    }

    object Retrofit {
        private const val version = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$version"
    }
}

object Urls {
    const val mavenCentralSnapshotRepo = "https://oss.sonatype.org/content/repositories/snapshots/"
    const val composeSnapshotRepo = "https://androidx.dev/snapshots/builds/" +
            "${Libs.AndroidX.Compose.snapshot}/artifacts/repository/"
}
