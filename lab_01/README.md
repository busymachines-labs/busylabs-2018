## 1) Create Github  account

Create [SSH Keys](https://help.github.com/articles/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/) then [add them to your github account](https://help.github.com/articles/adding-a-new-ssh-key-to-your-github-account/).

## 2) Install git locally
```
sudo apt-get update
sudo apt-get install git
```

##### install vim

```
sudo apt-get install vim
```

## 3) Clone busylabs repo

```
git clone https://github.com/busymachines/busylabs
```

## 4) Install java 1.8

[https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04)

Check instalation with:
```
java -version
```

## 5) Install sbt

[https://www.scala-sbt.org/1.0/docs/Setup.html](https://www.scala-sbt.org/1.0/docs/Setup.html)

Then you can have `sbt` generate a [template project](https://www.scala-sbt.org/1.0/docs/sbt-new-and-Templates.html) for you with:

```scala
sbt new scala/scala-seed.g8
```

## 6) Install docker

[https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-16-04](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-16-04)

Do not skip `Step 2` even though it is marked as "optional". It is highly crucial for a pleasant user experience with docker.

## 7) Install Intellij

Download the (free) community build:
[https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/)

Tip:
Always ["import" an already defined `sbt` project](https://www.jetbrains.com/help/idea/sbt-support.html#import_project) into IntelliJ. Never create an IntelliJ project by yourselves. The `sbt` build is the single source of truth regarding our project structure.

## 8) Create AWS account + MFA

Test first ssh into an ec2 image

## 9) git stuff

Useful [global configs](https://github.com/lorandszakacs/config/blob/master/git/.gitconfig) to put in `~/.gitconfig`. Aliases are by far the best part of it.
