plugins {
    id("com.android.library")
    kotlin("android")
    id("de.mannodermaus.android-junit5")
    id("maven-publish")
    signing
    id("org.jetbrains.dokka")
}

group = "app.moviebase"
version = Versions.versionName

dependencies {
    implementation(Libs.Kotlin.kotlin)
    implementation(Libs.Kotlin.kotlinReflect)

    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.preferenceKtx)
    implementation(Libs.AndroidX.work)
    implementation(Libs.AndroidX.recyclerView)
    implementation(Libs.AndroidX.constraintLayout)
    implementation(Libs.AndroidX.navigationUi)
    implementation(Libs.AndroidX.viewPager2)

    implementation(Libs.Google.material)

    testImplementation(Libs.Testing.truth)
    testImplementation(Libs.Testing.jupiter)
    testRuntimeOnly(Libs.Testing.jupiterEngine)
    testImplementation(Libs.Testing.mockito)
}

android {
    compileSdkVersion(Versions.compileSdk)
    buildToolsVersion = Versions.buildTools
    defaultConfig {
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.targetSdk)
        versionCode = Versions.versionCode
        versionName = Versions.versionName
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    dexOptions {
        preDexLibraries = true
        javaMaxHeapSize = "12g"
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
        animationsDisabled = true
        unitTests.isIncludeAndroidResources = true
    }
    lintOptions {
        isIgnoreTestSources = true
        isWarningsAsErrors = true
        isCheckDependencies = true
        isAbortOnError = false
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}


val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

artifacts {
    add("archives", sourcesJar)
}

afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "sonatype"
                if(Versions.versionName.endsWith("-SNAPSHOT"))
                    setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                else
                    setUrl("https://s01.oss.sonatype.org/service/local/")

                credentials {
                    username = findProperty("SONATYPE_USER") as String?
                    password = findProperty("SONATYPE_PASSWORD") as String?
                }
            }
        }
        publications {
            create<MavenPublication>("mavenJava") {
                groupId = "app.moviebase"
                artifactId = "android-ktx"
                version = Versions.versionName

                artifact(sourcesJar)
                from(components.getByName("release"))

                pom {
                    name.set("Android Kotlin Extensions")
                    description.set("Kotlin extensions for Android.")
                    url.set("https://github.com/MoviebaseApp/android-ktx")
                    inceptionYear.set("2021")

                    developers {
                        developer {
                            id.set("chrisnkrueger")
                            name.set("Christian Krüger")
                            email.set("christian.krueger@moviebase.app")
                        }
                    }
                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    issueManagement {
                        system.set("GitHub Issues")
                        url.set("https://github.com/MoviebaseApp/android-ktx/issues")
                    }
                    scm {
                        connection.set("scm:git:https://github.com/MoviebaseApp/android-ktx.git")
                        developerConnection.set("scm:git:git@github.com:MoviebaseApp/android-ktx.git")
                        url.set("https://github.com/MoviebaseApp/android-ktx")
                    }
                }
            }
        }
    }
    signing {
        sign(publishing.publications)
    }
}
