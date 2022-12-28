architectury {
    common(property("enabled_platforms").toString().split(','))
}

loom {
    accessWidenerPath.set(file("src/main/resources/${property("mod_id")}.accesswidener"))
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")

    modCompileOnly("appeng:appliedenergistics2-fabric:${property("ae2_version")}")

    val fabric = project(":fabric").dependencyProject
    modCompileOnly("curse.maven:macaws-bridges-351725:${fabric.property("bridges_fileid")}")
    modCompileOnly("curse.maven:macaws-roofs-352039:${fabric.property("roofs_fileid")}")
    modCompileOnly("curse.maven:macaws-windows-363569:${fabric.property("windows_fileid")}")
    // modCompileOnly("curse.maven:macaws-doors-378646:${fabric.property("doors_fileid")}")
    // modCompileOnly("curse.maven:macaws-trapdoors-400933:${fabric.property("trapdoors_fileid")}")
    // modCompileOnly("curse.maven:macaws-paintings-438116:${fabric.property("paintings_fileid")}")
    modCompileOnly("curse.maven:macaws-fences-and-walls-453925:${fabric.property("fences_fileid")}")
    // modCompileOnly("curse.maven:macaws-lights-and-lamps-502372:${fabric.property("lights_fileid")}")
    modCompileOnly("curse.maven:macaws-paths-and-pavings-629153:${fabric.property("pavings_fileid")}")
}
