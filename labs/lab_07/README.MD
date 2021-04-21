This lab is concerned with seting up infrastructure of a project. We will work our way to building + deploying a project on an AWS server and present some alternatives.

1) Pull the current version of the repo and import the project simpleMovieServer into Idea. 

1.1) Build the simpleMovieServer by running 

`sbt clean update compile assembly`

Note: To simplify building we are using an sbt plugin called `sbt-assembly` (see `./project/assembly.sbt`)

Ensure you have the `./target/scala-2.12/simpleMovieServer-assembly-0.1.jar` created. This is your deployment artefact (for now).

Run the newly created jar with `java -cp ./target/scala-2.12/simpleMovieServer-assembly-0.1.jar labs.simplemovieserver.Main`

1.2) Issue of configuration
It is very important that we separate the configuration from the code. Some variables that influence runtime behavior (eg: initial user, port we bind to) should be config-keys.

For configuration of the process, we use a library called [Typesafe-config](https://github.com/lightbend/config).
See inside the `Main` object how these are used. Typesafe-config uses the following conventions:
	- All config values MUST be given default values inside the reference.conf file (see the resources folder)
	- All config-parameters that you want specified at runtime must be specified in a file called applicaiton.conf that is specified to the process like this: 
	`java -Dconfig.file=./application.conf -cp ./target/scala-2.12/simpleMovieServer-assembly-0.1.jar labs.simplemovieserver.Main`

Please note that only the config keys which were specified inisde application.conf were overridden, the rest kept their default values from reference.conf.

There's another way of overriding default values, without necessarily using the `application.conf` at runtime. We use environment variables for this. Observe the lines like:
```
server.port = 3001
server.port = ${?SERVER_PORT}
```

What this does is say "the default value is 3001 but it can be overridden if the environment variable SERVER_PORT is set"

For example, use the following: `export INITIALUSER_AGE=23` and then run the server normally.


2) Creating an EC2 instance on AWS and ssh into it
ssh -i <> ubuntu@<IP_OF_SERVER>

```
sudo apt-get update -y
sudo apt-get install apache2 -y
sudo service apache2 start
```

2.1) Deploying on an aws instance using scp and starting the server normally.
`scp -i <PEM FILE> ./target/scala-2.12/simpleMovieServer-assembly-0.1.jar ubuntu@<IP_OF_SERVER>:simpleMovieServer-assembly-0.1.jar`
`
```
sudo apt-get install openjdk-8-jre -y
java -cp ~/simpleMovieServer-assembly-0.1.jar labs.simplemovieserver.Main
```

3) Docker Intro
```
docker build . -t simpletestserver:test -f ./Dockerfile
docker container run -d --name postgres-runner -p 5432:5432 -e POSTGRES_DB=mymoviedatabase -e POSTGRES_USER=mihai -e POSTGRES_PASSWORD=qwerty postgres
docker container exec -it postgres-runner psql -U busystudent -d mymoviedatabase
docker container run -p 3001:3001 -e PSQL_HOST=192.168.0.136:5432 simpletestserver:test

```

4) Creating our own docker images

5) Deploying on aws using Docker