# ATM Application

ATM app that takes balance and withdrawal requests from users through APIs

This is a pretty typical spring-boot microservice.  You can build and run it like:

```mvn clean install```

```mvn spring-boot:run```


#### To build and run the docker container
```docker build --tag=atm-application:latest .```

```docker run -p8080:8080 atm-application:latest```



### Curls

#####  Get Balance
```
curl --location --request GET 'localhost:8080/user/balance?pin={{user-pin}}'
```
###### example:  
```
curl --location --request GET 'localhost:8080/user/balance?pin=1234'
```

#####  Withdraw
```
curl --location --request PUT 'localhost:8080/user/withdraw?pin={{user-pin}}&amount={{withdrawal-amount}}'
```

###### example:
```
curl --location --request PUT 'localhost:8080/user/withdraw?pin=1234&amount=685'
```

### Config
Test data is loaded into the database in through the RepoConfiguration class under: com.daphne.zincworks.atm.repo;
