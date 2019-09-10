[![Build Status](https://travis-ci.com/IgorNB/libby.svg?branch=master)](https://travis-ci.com/IgorNB/Libby)
[![SonarCloud Quality](https://sonarcloud.io/api/project_badges/measure?project=com.lig%3Alibby&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.lig%3Alibby)
[![SonarCloud Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.lig%3Alibby&metric=coverage)](https://sonarcloud.io/dashboard?id=com.lig%3Alibby)
[![SonarCloud CodeSmell](https://sonarcloud.io/api/project_badges/measure?project=com.lig%3Alibby&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.lig%3Alibby)
[![SonarCloud Bug](https://sonarcloud.io/api/project_badges/measure?project=com.lig%3Alibby&metric=bugs)](https://sonarcloud.io/dashboard?id=com.lig%3Alibby)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
# Libby Pet-Project
See demo on [youtube](https://www.youtube.com/watch?v=tGOQ4pxfIx8&feature=youtu.be).

This application is a sample project which illustrates rest-api integration of 
* [Spring Boot](https://github.com/spring-projects/spring-boot) (37k stars backend) 
* [React-admin](https://github.com/marmelab/react-admin) (7.8k stars front-end).

## Prerequisites
* [Node.js](https://nodejs.org/en/)
* java 8
* [git](https://git-scm.com/)

## Getting Started
* get source code
```
git clone https://github.com/IgorNB/Libby.git
```

* install front-end dependencies
```
npm install
```

* Start (run the following commands in two separate terminals to create a blissful development experience where your browser auto-refreshes when files change on your hard drive)
```
./mvnw
npm start
```
 * On Application start liquibase and Spring-Batch load data from csv files. This is real world book data from https://www.kaggle.com/bshirude2/goodreads-content-based-book-recommendation/data. Data has the following amount distribution: 
 ```
 Books ~10.000
 Book_Ratings ~1.000.000
 Users ~1.000.000
 ```
 
### Installing

No installation available. See "Getting started"

## Running the tests
 * tests 
```
./mvnw clean test
```
 * tests coverage (with Jacoco. Min line coverage is auto-checked 70%) 
```
./mvnw clean verify
```


### And coding style tests

#### General rules 
checked with Sonar Quility Gate

#### Architectural rules 
checked with [ArchUnit](https://github.com/TNG/ArchUnit) in ./test-architecture tests bundle.
One of the most popular and general package structure for Spring Web App is selected. For example almost the same package structure is used in [Jhipster](https://github.com/jhipster/generator-jhipster) with the following main changes:
* `dto` and `mapping` packages are moved from `service` to `controller`
* `controller` package is divided to independent groups (called `adapter`) to make it easier to control and lower [code coupling](https://en.wikipedia.org/wiki/Coupling_(computer_programming))

So, architectural unit tests check that 
* any layer (onion) uses only classes from layers with lower #, but not from bigger #. Layers (onions) # are the following:

| #  | layer  |package|
|---|---|---|
|6| adapter  |controller.adapter|
|5| infrastructure  |controller.core|
|4| application  |config|
|3| service  |service, security|
|2| repository  |repository|
|1|domain|domain|

<p align="center">
    <img src="./docs/architecture_onions.svg">
</p>

* any `adapter` does not use classes from any other `adapter`
* there are no circle dependencies

```
```

## Deployment

Not available

## Development
#### Controller
Regular Spring `@RestController` are used. You can use ` implements GenricUIApiController` to check compatibility of your own controller with react-admin api in compile time (this is **optional**).

`@Querydsl` is used in `public Page<> findAll(@QuerydslPredicate(root = ..` endpoints to provide search by any field functionality. This is used for 
* user filtering in UI
* relation-ship queries by react-admin

#### Dto, Mapper
Regular `POJO`'s are used as Dto's. [MapStruct](http://mapstruct.org/) is used as Mapper library. 
#### Service
Regular Spring `@Service`'s are used 
#### Entity
Regular `JPA` entities are used.
#### Migrations
Entity changes are applyed to DB on application start (Liquibase is used). Liquibase changesets are used to illustrate production ready development lifecycle (instead of Hibernate Hbm2dll which is [deprecated](http://docs.jboss.org/tools/4.1.0.Final/en/hibernatetools/html/ant.html#d0e4651) for usage in production).

* To speed up development changeSet should be generated automatically, then reviewed by developer and included in commit. Use next command to auto-generate changeset (note that liquibase.contexts=gen is used to skip test data load, which should be marked with "context: test" in changeSet files):

``` 
mvn clean compile liquibase:clearCheckSums liquibase:dropAll liquibase:update -Dliquibase.contexts=gen liquibase:diff
```

* Review auto-generataed changeSet file, make changes if needed and run next command to check your test data can be loaded in your new DB schema (or just start application and this will be done automatically):

``` 
mvn clean compile liquibase:clearCheckSums liquibase:dropAll liquibase:update liquibase:diff
```

* Changeset will be also automatically applied on start up, but if you want to see DB without starting application run next command:
``` 
mvn liquibase:update
```

* Changeset can be auto-generated in xml, yaml or SQL (database specific) format. To change it, please, change value of the next property in .POM file:
```
<liquibase.changeLogFile.format>
```

## Built With

* [Spring](https://docs.spring.io/spring/docs/5.1.4) - web framework
* [react-admin](https://github.com/marmelab/react-admin) - frontend Framework for building admin applications running in the browser on top of REST/GraphQL APIs, using ES6, React and Material Design
* [MapStruct](http://mapstruct.org/) - mapper library
* [Apache Commons Lang 3](https://commons.apache.org/proper/commons-lang/download_lang.cgi) - helper utilities for the java.lang API
* [Maven](https://maven.apache.org/) - dependency Management
* [Junit5](https://junit.org/junit5/) - testing framework
* [Mockito](https://site.mockito.org/) -  mocking framework for unit tests
* [AssertJ](http://joel-costigliola.github.io/assertj/) - fluent assertions library
* [Jacoco](https://www.eclemma.org/jacoco/) - code coverage library
* [Sonar Cube](https://sonarcloud.io) - Continuous Code Quality provider
* [Travis CI](https://travis-ci.org) - Continuous Integration provider
## Contributing

Not available 


## Authors

* **IgorNB**

## License

This project is licensed under the Apache License - see the [LICENSE.txt](LICENSE.txt) file for details

