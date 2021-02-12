# RequestManagement
Request management is a simple java project to handle many requests each day that need to be processed. To avoid all the incoming requests which were processed regardless of
their validity, leading to slower processing. So to filter out invalid requests (badly formatted, containing invalid values, missing required fields, …) but keep a history on a day-by-day basis so that
to properly charge customers for the traffic they send.

Prerequisites : 
Install IntelliJ IDEA, MySQL, Postman(Swagger also available), RabbitMQ 
Enable Lombok plugin from IntelliJ market place

Running & Testing : 
Run the SQL script by creating a database named 'adex'(All the property files were included in the resources folder and mapped to classpath url by default)
Build the maven project by clean & install
Run the RequestManagementApplication main class resides in the base package
Use postman to send request and recieve response.
Or use swagger for testing purpose on browser using url : localhost:8086/swagger-ui.html#

Version :
Java version 1.8

Author : 
Vipin Kolathodiyil


