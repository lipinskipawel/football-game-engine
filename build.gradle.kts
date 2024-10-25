plugins {
    java
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version ("1.3.0")
}

group = "com.github.lipinskipawel"
version = "6.0.0"
description = "game-engine"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.github.lipinskipawel:football-platform:1.+"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.assertj:assertj-core")
}

java {
    withJavadocJar()
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}

publishing {
    publications.create<MavenPublication>("mavenJava") {
        pom {
            name.set("game-engine")
            description.set("This is engine for 2D football game")
            url.set("https://github.com/lipinskipawel/game-engine")
            licenses {
                license {
                    name.set("MIT License")
                    url.set("http://www.opensource.org/licenses/mit-license.php")
                }
            }
            developers {
                developer {
                    name.set("Pawel Lipinski")
                }
            }
            scm {
                connection.set("scm:git:ssh://git@github.com/lipinskipawel/game-engine.git")
                developerConnection.set("scm:git:ssh://git@github.com/lipinskipawel/game-engine.git")
                url.set("git@github.com:lipinskipawel/game-engine")
            }
        }
        from(components["java"])
    }
}

signing {
    val signingKeyId: String? by project
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }

    wrapper {
        gradleVersion = "7.4.2"
        distributionType = Wrapper.DistributionType.ALL
    }
}
