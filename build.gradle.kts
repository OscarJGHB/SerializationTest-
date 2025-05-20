

plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "org"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "21.0.7"
    modules = listOf( "javafx.controls", "javafx.fxml")
}

application {
    mainClass.set( "org.Book.Main")
}

val jfxVersion = "21.0.7"

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    implementation("com.thoughtworks.xstream:xstream:1.4.21")
    implementation("org.openjfx:javafx-controls:$jfxVersion")
    implementation("org.openjfx:javafx-fxml:$jfxVersion")
}

tasks.test {
    useJUnitPlatform()
}
