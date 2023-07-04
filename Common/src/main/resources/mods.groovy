ModsDotGroovy.make {
    modLoader = 'javafml'
    loaderVersion = '[40,)'

    license = 'MIT'

    mod {
        modId = 'hostilenetworks'
        displayName = 'Hostile Neural Networks'

        version = '1.0.0'

        description = 'Very hostile'
        authors = [
                'Shadows'
        ]

        dependencies {
//            minecraft = ">=1.20.1"
//
//            onFabric {
//                fabricLoader = ">=${this.fabricLoaderVersion}"
//                mod('fabric') {
//                    mandatory = true
//                    versionRange = ">=${this.buildProperties.fabric_version}"
//                }
//            }
//
//            onForge {
//                forge = ">=${this.forgeVersion}"
//            }
//
//            mod('placebo') {
//                mandatory = true
//                versionRange = '>=1.0.0'
//            }
        }

        entrypoints {
            placeboInit = 'shadows.hostilenetworks.fabric.HostileNetworksFabric'
            placeboClientInit = 'shadows.hostilenetworks.fabric.client.HostileNetworksFabricClient'
            jei_mod_plugin = 'shadows.hostilenetworks.jei.HostileJeiPlugin'
        }

        onFabric {
            accessWidener = 'hostilenetworks.accesswidener'
        }
    }

    mixin = "${buildProperties.mod_id}.mixins.json"
    onFabric {
        mixin = "hostilenetworks_fabric.mixins.json"
    }
}