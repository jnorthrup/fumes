#!/bin/bash

cat << EOF > build.gradle
plugins {
    id 'org.jetbrains.kotlin.multiplatform' version '1.8.0'
}


repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
    google()
}

kotlin {
    jvm()
    android()
    iosArm64()
    iosX64()
    linuxX64()

    sourceSets {
        commonMain {
            dependencies {
                implementation platform('org.bereft:trikeshed:1.0')
            }
        }

        androidMain {
            dependencies {
                implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
                implementation "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"
            }
        }

        iosMain {
            dependencies {
                implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
                implementation "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"
            }
        }

        jvmMain {
            dependencies {
                implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4"
                implementation "org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.4.0"
            }
        }
    }
}
EOF

cat << EOF > settings.gradle
rootProject.name = 'new-project'
EOF
