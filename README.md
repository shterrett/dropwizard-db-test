# DbTest

This is a minimal replication of an issue we where a temporary database outage lead to a lack of available database connections, even when they shouldn't have been in use.

To reproduce:

First, update the `config.yml` file to specify a database. It is currently configured for a postgres docker container, but you should be able to use any database -- we have seen the issue with  both postgres and oracle databases.

Then run the project:

```
$ mvn clean install
$ java -jar target/dbtest-1.3.5.jar server config.yml
$ curl <path-to-database>/hello-world
```

This should output `{"id":1,"content":"Selected 1; Pool Size: 10; Active: 0; Idle: 10; Waiting: 0"}`

Next -- stop the database to break the connection. Run the `curl` command again to confirm. Then restart the database, and wait for it to reconnect.

Run `$ curl <path-to-database>/hello-world` until successful again, and it will output `{"id":8,"content":"Selected 1; Pool Size: 9; Active: 8; Idle: 1; Waiting: 0"}`

(Note that the actual metrics might be different, though they have been very consistent).

Now, every time `curl` is repeated, there will be a pool size of N, and N-1 of those connections will be "active".