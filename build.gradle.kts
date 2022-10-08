plugins {
    kotlin("jvm") version "1.7.20"
    id("fabric-loom") version "1.0-SNAPSHOT"
    id("io.github.juuxel.loom-quiltflower") version "1.7.+"
    id("maven-publish")
}

version = project.properties["mod_version"].toString()
group = project.properties["maven_group"].toString()


loom {
    // Split minecraft sources
    //splitEnvironmentSourceSets()

    runtimeOnlyLog4j.set(true)
}

repositories {
    maven("https://maven.kosmx.dev/")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
    mavenCentral()
    maven("https://api.modrinth.com/maven"){
        name = "Modrinth"
        content {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.properties["minecraft_version"]}")

    // Yarn is better documented than mojmap.
    mappings("net.fabricmc:yarn:${project.properties["yarn_mappings"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.properties["loader_version"]}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.properties["fabric_version"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.properties["fabric_kotlin"]}")

    // We might want advanced player animations
    modImplementation("dev.kosmx.player-anim:player-animation-lib-fabric:${project.properties["player_anim"]}")

    modLocalRuntime("com.terraformersmc:modmenu:${project.properties["mod_menu"]}")
    modApi("me.shedaniel.cloth:cloth-config-fabric:${project.properties["cloth_config"]}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    compileOnly("io.github.kosmx.emotes:emotesMain:${project.properties["emotecraft"]}")

    modImplementation("maven.modrinth:replaymod:${project.properties["replaymod"]}")

    modLocalRuntime("maven.modrinth:emotecraft:${project.properties["emotecraft"]}-MC1.19.2-fabric")
}

base {
    archivesName.set(project.properties["archive_base_name"].toString())
}

tasks {

    processResources {
        inputs.property("version", project.version)
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to project.version))
        }
    }

    val targetJavaVersion = 17
    withType<JavaCompile> {
        // ensure that the encoding is set to UTF-8, no matter what the system default is
        // this fixes some edge cases with special characters not displaying correctly
        // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
        // If Javadoc is generated, this must be specified in that task too.
        options.encoding = "UTF-8"
        options.release.set(targetJavaVersion)
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = targetJavaVersion.toString()
    }

    java {
        val javaVersion = JavaVersion.toVersion(targetJavaVersion)
        toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))

        // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
        // if it is present.
        // If you remove this line, sources will not be generated.
        withSourcesJar()
    }


    jar {
        from("LICENSE") {
            rename { "${it}_${base.archivesName}" }
        }
    }

}
// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
