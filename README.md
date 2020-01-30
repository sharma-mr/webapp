# webapp
Web application for CSYE6225

Contact Information-
Mrinal Sharma
NUID 001448287
Email- sharma.mr@husky.neu.edu

This web application is a java based web application developed on Spring Boot Framework

Prerequisites for running this application
You should have a running java version 1.8.0_202
Mysql version 8.0.19
You need to have Postman or any other API development tool
Framework used is Spring Boot version 2.2.3.RELEASE
It can be deployed on apache tomcat 9.0.30

There are various API's exposed which can be consumed according

GET /v1​/user​/self Get User Information
PUT /v1​/user​/self Update user information
POST /v1​/bill​/ Create a new bill
GET /v1​/bills Get all bills.
DELETE /v1​/bill​/{id} Delete a bill
GET /v1​/bill​/{id} Get a bill
PUT /v1​/bill​/{id} Update a bill

API available without authentication

POST /v1​/user Create a user
