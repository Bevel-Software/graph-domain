import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.render.TextReportRenderer
import com.github.jk1.license.filter.LicenseBundleNormalizer

// Add buildscript dependency for the Nexus plugin
buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.github.gradle-nexus:publish-plugin:2.0.0")
    }
}

plugins {
    kotlin("jvm") version "2.0.10"
    `maven-publish`
    id("com.github.jk1.dependency-license-report") version "2.9"
    signing
}

// Check if this is a standalone project build
if (rootProject.name == "graph-domain") {
    // Apply the Nexus plugin when built as a standalone project
    apply {
        plugin("io.github.gradle-nexus.publish-plugin")
    }
    
    // Access the extension directly after applying the plugin
    configure<io.github.gradlenexus.publishplugin.NexusPublishExtension> {
        repositories {
            sonatype {
                nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
                snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            }
        }
    }
}

licenseReport {
    renderers = arrayOf(
        TextReportRenderer()
    )
    filters = arrayOf<DependencyFilter>(LicenseBundleNormalizer())
}

repositories {
    mavenCentral()
}

// Define group and version based on root project or use defaults for standalone
val projectGroup = "software.bevel"
val projectVersion = "1.0.0"

group = projectGroup
version = projectVersion

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.slf4j:slf4j-api:2.0.17")
    
    // Handle file-system-domain dependency differently based on whether we're in standalone or multi-project mode
    if (rootProject.name == "graph-domain") {
        // In standalone mode, use the published Maven artifact
        implementation("$projectGroup:file-system-domain:$projectVersion")
    } else {
        // In multi-project mode, use the project dependency
        implementation(project(":file-system-domain"))
    }
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            groupId = projectGroup
            artifactId = "graph-domain"
            version = projectVersion
            
            pom {
                name.set("Graph Domain")
                description.set("Graph domain library for Bevel")
                url.set("https://bevel.software")
                
                licenses {
                    license {
                        name.set("Mozilla Public License 2.0")
                        url.set("https://www.mozilla.org/en-US/MPL/2.0/")
                    }
                }

                developers {
                    developer {
                        name.set("Razvan-Ion Radulescu")
                        email.set("razvan.radulescu@bevel.software")
                        organization.set("Bevel Software")
                        organizationUrl.set("https://bevel.software")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/Bevel-Software/graph-domain.git")
                    developerConnection.set("scm:git:ssh://git@github.com:Bevel-Software/graph-domain.git")
                    url.set("https://github.com/Bevel-Software/graph-domain")
                }
            }
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

// Configure signing
signing {
    sign(publishing.publications["maven"])
}