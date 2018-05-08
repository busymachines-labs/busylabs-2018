This lab is concerned with seting up infrastructure of a project. We will work our way to building + deploying a project on an AWS server and present some alternatives.

1) Pull the current version of the repo and import the project simpleMovieServer into Idea. 

1.1) Build the simpleMovieServer by running 

`sbt clean update compile assembly`

Note: To simplify building we are using an sbt plugin called `sbt-assembly` (see `./project/assembly.sbt`)

Ensure you have the `./target/scala-2.12/simpleMovieServer-assembly-0.1.jar` created. This is your deployment artefact (for now).

Run the newly created jar with `java -cp ./target/scala-2.12/simpleMovieServer-assembly-0.1.jar com.busymachines.labs.simplemovieserver.Main`

1.2) Issue of configuration
It is very important that we separate the configuration from the code. Some variables that influence runtime behavior (eg: initial user, port we bind to) should be config-keys.

For configuration of the process, we use a library called [Typesafe-config](https://github.com/lightbend/config).
See inside the `Main` object how these are used. Typesafe-config uses the following conventions:
	- All config values MUST be given default values inside the reference.conf file (see the resources folder)
	- All config-parameters that you want specified at runtime must be specified in a file called applicaiton.conf that is specified to the process like this: 
	`java -Dconfig.file=./application.conf -cp ./target/scala-2.12/simpleMovieServer-assembly-0.1.jar com.busymachines.labs.simplemovieserver.Main`

Please note that only the config keys which were specified inisde application.conf were overridden, the rest kept their default values from reference.conf.

There's another way of overriding default values, without necessarily using the `application.conf` at runtime. We use environment variables for this. Observe the lines like:
```
server.port = 3001
server.port = ${?SERVER_PORT}
```

What this does is say "the default value is 3001 but it can be overridden if the environment variable SERVER_PORT is set"

For example, use the following: `export INITIALUSER_AGE=23` and then run the server normally

