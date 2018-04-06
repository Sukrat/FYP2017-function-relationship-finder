# UCD Final year project 2017 (Function relationship finder)
The project nicknamed as functfinder. functfinder is Java based application which uses ArangoDB backend. This project helps finding non-existence of functional relationship in the dataset provided. It also helps in analysing the consistency of linear models on the dataset. 

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing puposes.

### Prerequisites

1. [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 
2. [ArangoDB v3.3](https://www.arangodb.com/)
3. [Gradle v4.6](https://gradle.org/)
4. Optional [IntelliJ](https://www.jetbrains.com/idea/)

### Building

#### Go to bash or cmd

Clone the repository

```
git clone https://github.com/Sukrat/FYP2017-function-relationship-finder.git
```

Go to the directory

```
cd FYP2017-function-relationship-finder/functfinder
```

Build the project

```
gradle build
```

Build the project without running the test

```
gradle build -x
```

#### Web application
To run the Web application

```
java -jar webapp/build/libs/functfinder-web-2.0.jar
```

Go to browser

```
http://localhost:8080
```

For changing the arangodb settings like password

```
Change the settings in the file functfinder/webapp/src/main/resources/arangodb.properties
```

Similarly for changing arangodb setting like password for the test

```
Change the settings in the file functfinder/webapp/src/test/resources/arangodb.properties
```

#### Command line 

To display the usages or help of the command line

```
java -jar cmdapp/build/libs/functfinder-cmd-2.0.jar -?
```

