plugins {
    id 'org.jetbrains.kotlin.multiplatform' version '1.8.0'
}

group = "org.bereft"
version = "1.0"


repositories {

//create mavenlocal at a local maven repository at ~/.m2/repository
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    google()
}

kotlin {
    jvm()
//    android()
//    iosArm64()
//    iosX64()
    linuxX64()

    sourceSets {
        commonMain {
            dependencies {
            implementation implementation('org.bereft:trikeshed:1.0')
            }
        }
        commonTest {
            dependencies {
                implementation kotlin('test-common')
                implementation kotlin('test-annotations-common')
            }
        }

//        androidMain {
//            dependencies {
//                implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
//                implementation "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"
//            }
//        }

        linuxX64Main {
            dependencies {
                implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
                implementation "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"
            }
        }
        linkLinuxX64Test {
            dependencies {
                implementation kotlin('test')
            }
        }
        jvmMain {
            dependencies {
                implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4"
                implementation "org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.4.0"
            }
        }
        jvmTest {
            dependencies {
                implementation kotlin('test')
            }
        }

    }
}