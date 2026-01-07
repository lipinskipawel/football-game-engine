plugins {
    java
    `maven-publish`
    signing
}

description = "football-game-engine-ai"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("com.github.lipinskipawel:football-platform:(1.0.0, 2.0.0)"))

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

//publishing {
//    publications.create<MavenPublication>("mavenJava") {
//        pom {
//            name.set("football-game-engine-ai")
//
//            group = project.group.toString()
//            artifactId = "football-game-engine-ai"
//            version = project.version.toString()
//
//            description.set("This is AI for 2D football game")
//            url.set("https://github.com/lipinskipawel/football-game-engine")
//            licenses {
//                license {
//                    name.set("MIT License")
//                    url.set("http://www.opensource.org/licenses/mit-license.php")
//                }
//            }
//            developers {
//                developer {
//                    name.set("Pawel Lipinski")
//                }
//            }
//            scm {
//                connection.set("scm:git:ssh://git@github.com/lipinskipawel/football-game-engine.git")
//                developerConnection.set("scm:git:ssh://git@github.com/lipinskipawel/football-game-engine.git")
//                url.set("https://github.com/lipinskipawel/football-game-engine.git")
//            }
//        }
//        from(components["java"])
//    }
//}
//
//signing {
//    val signingKeyId: String? by project
//    val signingKey: String? by project
//    val signingPassword: String? by project
//    useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
//    sign(publishing.publications["mavenJava"])
//}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "io.github.lipinskipawel.board.Main"
        }
        dependsOn("benchmark")
    }

    // asprof -d 10 -o flamegraph -f flamegraph.html <pid>
    register<Jar>("benchmark") {
        archiveBaseName = "benchmark"
        manifest {
            attributes["Main-Class"] = "io.github.lipinskipawel.board.Main"
        }
        from(sourceSets.main.get().output)
        standardOutputCapture.start()
    }

    register<JavaExec>("runBenchmark") {
        classpath = files(project.tasks.getByName("benchmark"))
        dependsOn("benchmark")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }
}
