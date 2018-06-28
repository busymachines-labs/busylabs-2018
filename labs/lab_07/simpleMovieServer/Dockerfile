FROM ubuntu:18.04

RUN apt-get update

RUN apt-get install openjdk-8-jre -y

COPY ./target/scala-2.12/simpleMovieServer-assembly-0.1.jar /simplemovieserver/simpleMovieServer-assembly-0.1.jar

EXPOSE 3001

CMD java -cp /simplemovieserver/simpleMovieServer-assembly-0.1.jar labs.simplemovieserver.Main