import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.31"
    kotlin("kapt") version "1.3.31"
    id("com.github.ben-manes.versions") version "0.21.0"
}

group = "com.github.ryansname"
version = "1.0-SNAPSHOT"

val coroutinesVersion = "1.2.0"
val koinVersion = "2.0.1"
val lanternaVersion = "3.0.1"
val moshiVersion = "1.8.0"
val retrofitVersion = "2.6.0"
val retrofitCoroutineAdapterVersion = "0.9.2"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    implementation("com.googlecode.lanterna:lanterna:$lanternaVersion")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:$retrofitCoroutineAdapterVersion")
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-adapters:$moshiVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("org.koin:koin-core:$koinVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.coroutines.FlowPreview"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.coroutines.ObsoleteCoroutinesApi"
}

application {
    mainClassName = "com.github.ryansname.own_tempo.MainKt"
}
