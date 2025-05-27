

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

val jfxVersion = "21.0.7"

application{
    mainClass.set("org.BookManager.gui.MainApp")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
    implementation("org.openjfx:javafx-controls:$jfxVersion")
    implementation("org.openjfx:javafx-fxml:$jfxVersion")
    implementation(project(":BookManagerCore"))
}

tasks.test {
    useJUnitPlatform()
}
