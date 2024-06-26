import org.wisepersist.gradle.plugins.gwt.GwtDev

plugins {
    id("java")
    id("org.wisepersist.gwt") version "1.1.19"
    id("maven-publish")
}
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/sayaya1090/maven")
        credentials {
            username = project.findProperty("github_username") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("github_password") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
    mavenCentral()
    mavenLocal()
}
group = "net.sayaya"
version = "3.0"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

dependencies {
    implementation("net.sayaya:ui:material3-1.4.0")
    implementation("org.jboss.elemento:elemento-core:1.4.2")
    implementation("org.gwtproject:gwt-user:2.11.0")
    compileOnly("org.gwtproject:gwt-dev:2.11.0")
    implementation("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

tasks {
    withType<Delete> { doFirst { delete("build/") } }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    gwt {
        minHeapSize = "1024M"
        maxHeapSize = "2048M"
        sourceLevel = "auto"
    }
    compileGwt {
        val lombok: File = project.configurations.annotationProcessor.get().filter { it.name.startsWith("lombok") }.single()
        extraJvmArgs = listOf("-XX:ReservedCodeCacheSize=512M", "-javaagent:${lombok}=ECJ")
    }
}

if(project.gradle.startParameter.taskNames.contains("gwtDev")) {
    apply(plugin="gwt")
    apply(plugin="war")
    tasks {
        gwt {
            gwt.modules = listOf("net.sayaya.Test")
        }
        named<GwtDev>("gwtDev") {
            minHeapSize = "4096M"
            maxHeapSize = "4096M"
            sourceLevel = "auto"
            val lombok: File = project.configurations.annotationProcessor.get().filter { it.name.startsWith("lombok") }.single()
            extraJvmArgs = listOf("-XX:ReservedCodeCacheSize=512M", "-javaagent:${lombok}=ECJ")
            port = 8888
            war = File("src/test/webapp")
        }
        java.sourceSets["main"].java {
            srcDir("src/test/java")
        }
        withType<War> {
            duplicatesStrategy = DuplicatesStrategy.WARN
        }
    }
} else {
    apply(plugin="gwt-base")
    tasks {
        jar {
            from(sourceSets.main.get().allSource)
        }
        publishing {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/sayaya1090/maven")
                    credentials {
                        username = project.findProperty("github_username") as String? ?: System.getenv("GITHUB_USERNAME")
                        password = project.findProperty("github_password") as String? ?: System.getenv("GITHUB_TOKEN")
                    }
                }
            }
            publications {
                register("maven", MavenPublication::class) {
                    groupId = "net.sayaya"
                    artifactId = "chart"
                    version = "3.0"
                    from(project.components["java"])
                }
            }
        }
    }
}