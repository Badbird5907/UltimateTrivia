# Ultimate Trivia

# Starting the program

Execute the jar file with `java -jar UltimateTrivia.jar`

The program loads questions from `trivia.csv` by default.
You can specify your own file when executing the jar: `java -jar UltimateTrivia.jar yourfile.csv`

# Building the program
Use `./gradlew build` to build the program from sources. The output jar file can be found in `./build/libs`

A maven build file is also included. Use `mvn package` to build it. However, I recommended using the gradle build script. (bundled as gradlew.bat)
