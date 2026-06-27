# Store Application
The Store application keeps track of customers and orders in a database.

# Task Completion
* Task 1 : Extend the order endpoint to find a specific order, by ID
  * Created the endpoint to get the details by Id.
   
* Task 2 : Extend the customer endpoint to find customers based on a query string to match a substring of one of the words in their name
  * Created the endpoint to find customers by query string, and added order details of the customer in response. Implemented **Redis cache** and added **INDEXING** in DB for better performance.

* Task 3 : Users have complained that in production the GET endpoints can get very slow. The database is unfortunately not co-located with the application server, and there's high latency between the two. Identify if there are any optimisations that can improve performance
  * Refactored the API's with **Redis Cache** & Updated **@EntityGraph, JOIN** query in repository. Added **PAGINATION** for getALL api's for better performance.
  * Made relevant mapper changes.

* Task 4 : Add a new endpoint /products to model products which appear in an order
  * Created POST endpoint to add product, Updated the schema.sql and added index in DB.
  * Created GET ALL products endpoint that returns order details as well. Created PAGINATION and updated with Redis cache.
  * Created GET product by id endpoint that returns order details as well.
  * Updated GET Order endpoint to return prodcut details as well and made relevant mapper changes.

* Bonus features implemented
  * Created docker script to deliver it as an image.
  * Implemented **CICD** pipeline which includes **SonarQube, OWASP, build, Test and deploy**
  * Implemented **HikariCP DB connection pooling**.
  * Added **Log4j** implementation for logging.
  * Created **helm chart** for the deployment
    * Basic commands to trouble shoot and deployment.
        * docker build -t store-main:1.0.0
        * docker run -d -p 8080:8080 --name store-main store-main:1.0.0
        * helm install store-main
        * helm uninstall store-api
        * kubectl get pods
        * kubectl logs <pod-name>
        * kubectl logs store-main-6df9c89b5c-h7jvf
  * Implemented **RATELIMIT** and **CIRCUIT-BREAKER** for the fault tolerance.
  * Added **ACTUATOR** framework endpoints : **health, info,beans, env, metrics, threaddump, circuitbreakers** for PRODUCTION Monitoring purpose.
    * Health → http://localhost:8080/manage/health
    * Environment → http://localhost:8080/manage/env
    * Metrics → http://localhost:8080/manage/metrics
    * Thread dump → http://localhost:8080/manage/threaddump
    * JVM Memory utilization http://localhost:8080/manage/metrics/jvm.memory.used
    * HTTP Requests latency : http://localhost:8080/manage/metrics/http.server.requests
    * Hikaricp connection pool : http://localhost:8080/manage/metrics/hikaricp.connections
    * Circuit breaker status : http://localhost:8080/manage/circuitbreakers
  * Added **Testcases** for all controller and services. it will be executed as a part of CICD.
  * Created **environment specific aplication.yml** files to support all the environments.

# Assumptions
This README assumes you're using a posix environment. It's possible to run this on Windows as well:
* Instead of `./gradlew` use `gradlew.bat`
* The syntax for creating the Docker container is different. You could also install PostgreSQL on bare metal if you prefer


# Prerequisites
This service assumes the presence of a postgresql 16.2 database server running on localhost:5433 (note the non-standard port)
It assumes a username and password `admin:admin` can be used.
It assumes there's already a database called `store`

You can start the PostgreSQL instance like this:
```shell
docker run -d \
  --name postgres \
  --restart always \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=store \
  -v postgres:/var/lib/postgresql/data \
  -p 5433:5432 \
  postgres:16.2 \
  postgres -c wal_level=logical
```

# Running the application
You should be able to run the service using
```shell
./gradlew bootRun
```

The application uses Liquibase to migrate the schema. Some sample data is provided. You can create more data by reading the documentation in utils/README.md

# Data model
An order has an ID, a description, and is associated with the customer which made the order.
A customer has an ID, a name, and 0 or more orders.

# API
Two endpoints are provided:
   * /order
   * /customer

Each of them supports a POST and a GET. The data model is circular - a customer owns a number of orders, and that order necessarily refers back to the customer which owns it.
To avoid loops in the serializer, when writing out a Customer or an Order, they're mapped to CustomerDTO and OrderDTO which contain truncated versions of the dependent object - CustomerOrderDTO and OrderCustomerDTO respectively.

The API is documented in the OpenAPI file OpenAPI.yaml. Note that this spec includes part of one of the tasks below (the new /products endpoint)

# Tasks

1. Extend the order endpoint to find a specific order, by ID
2. Extend the customer endpoint to find customers based on a query string to match a substring of one of the words in their name
3. Users have complained that in production the GET endpoints can get very slow. The database is unfortunately not co-located with the application server, and there's high latency between the two. Identify if there are any optimisations that can improve performance
4. Add a new endpoint /products to model products which appear in an order:
      * A single order contains 1 or more products. 
      * A product has an ID and a description. 
      * Add a POST endpoint to create a product
      * Add a GET endpoint to return all products, and a specific product by ID
        * In both cases, also return a list of the order IDs which contain those products
      * Change the orders endpoint to return a list of products contained in the order

# Bonus points
1. Implement a CI pipeline on the platform of your choice to build the project and deliver it as a Dockerized image

# Notes on the tasks
Assume that the project represents a production application.
Think carefully about the impact on performance when implementing your changes
The specifications of the tasks have been left deliberately vague. You will be required to exercise judgement about what to deliver - in a real world environment, you would clarify these points in refinement, but since this is a project to be completed without interaction, feel free to make assumptions - but be prepared to defend them when asked.
There's no CI pipeline associated with this project, but in reality there would be. Consider the things that you would expect that pipeline to verify before allowing your code to be promoted
Feel free to refactor the codebase if necessary. Bad choices were deliberately made when creating this project.
