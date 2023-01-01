plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    runs {
        create("data") {
            inherit(getByName("client"))
            name("Minecraft Data")
            property("fabric-api.datagen")
            property("fabric-api.datagen.modid", rootProject.property("mod_id").toString())
            property("fabric-api.datagen.output-dir", file("src/generated/resources").absolutePath)
            property("fabric-api.datagen.strict-validation")
        }
    }
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    named("developmentFabric").get().extendsFrom(common)
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}+${property("minecraft_version")}")

    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionFabric")) { isTransitive = false }

    modImplementation("appeng:appliedenergistics2-fabric:${property("ae2_version")}")

    modImplementation("curse.maven:macaws-bridges-351725:${property("bridges_fileid")}")
    modImplementation("curse.maven:macaws-roofs-352039:${property("roofs_fileid")}")
    modImplementation("curse.maven:macaws-windows-363569:${property("windows_fileid")}")
    // modImplementation("curse.maven:macaws-doors-378646:${property("doors_fileid")}")
    // modImplementation("curse.maven:macaws-trapdoors-400933:${property("trapdoors_fileid")}")
    // modImplementation("curse.maven:macaws-paintings-438116:${property("paintings_fileid")}")
    modImplementation("curse.maven:macaws-fences-and-walls-453925:${property("fences_fileid")}")
    // modImplementation("curse.maven:macaws-lights-and-lamps-502372:${property("lights_fileid")}")
    modImplementation("curse.maven:macaws-paths-and-pavings-629153:${property("pavings_fileid")}")

    modRuntimeOnly("com.terraformersmc:modmenu:${property("mod_menu_version")}")
    modRuntimeOnly("mezz.jei:jei-${property("minecraft_version")}-fabric:${property("jei_version")}")
    modRuntimeOnly("curse.maven:jade-324717:${property("jade_fileid")}")
}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
            exclude("**/.cache")
        }
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        exclude("architectury.common.json")

        configurations = listOf(shadowCommon)
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
        archiveClassifier.set(null as String?)
    }

    jar {
        archiveClassifier.set("dev")
    }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}
