plugins {
    id("java")
}

group = "dev.badbird.trivia"
version = "1.0.0"

tasks.jar {
    manifest {
        attributes["Main-Class"] = "dev.badbird.trivia.Main"
    }
}