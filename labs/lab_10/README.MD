In this lab we will extend the movieServer project to build authentication/ rights-management.
We will see how data evolves along with code in the lifetime of the backend.

To run the database, use ```docker container run -d --name postgres-runner -p 5432:5432 -e POSTGRES_DB=mymoviedatabase -e POSTGRES_USER=busyUser -e POSTGRES_PASSWORD=qwerty postgres```

To inspect the database, use 

```docker container exec -it postgres-runner psql -U busyUser -d mymoviedatabase```

1) Discussion on the current status of the code
Important: Doobie, Flyway

2) Add more movies/comments to the startup data

3) Change the Get-endpoints to return the movies along with all the comments

4) Change the data-model so that tue user has a password set

5) Implement user-authentication with tokens stored in the database

6) All endpoints requie authentication

7) Implement user-registration