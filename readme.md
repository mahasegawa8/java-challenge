### How to use this spring-boot project

- Install packages with `mvn package`
- Run `mvn spring-boot:run` for starting the application (or use your IDE)

Application (with the embedded H2 database) is ready to be used ! You can access the url below for testing it :

- Swagger UI : http://localhost:8080/swagger-ui.html
- H2 UI : http://localhost:8080/h2-console

> Don't forget to set the `JDBC URL` value as `jdbc:h2:mem:testdb` for H2 UI.

### Instructions

- download the zip file of this project
- create a repository in your own github named 'java-challenge'
- clone your repository in a folder on your machine
- extract the zip file in this folder
- commit and push

- Enhance the code in any ways you can see, you are free! Some possibilities:
  - Add tests
  - Change syntax
  - Protect controller end points
  - Add caching logic for database calls
  - Improve doc and comments
  - Fix any bug you might find
- Edit readme.md and add any comments. It can be about what you did, what you would have done if you had more time, etc.
- Send us the link of your repository.

#### Restrictions

- use java 8

#### What we will look for

- Readability of your code
- Documentation
- Comments in your code
- Appropriate usage of spring boot
- Appropriate usage of packages
- Is the application running as expected
- No performance issues

#### Your experience in Java

Please let us know more about your Java experience in a few sentences. For example:

- I have 3 years experience in Java and I started to use Spring Boot from last year
- I'm a beginner and just recently learned Spring Boot
- I know Spring Boot very well and have been using it for many years

### My experience in Java

I have a foundation in Java, with hands-on experience in developing and maintaining applications using the Struts framework.

### What I did

- Increased Test Coverage: I added unit tests for the service and controller layers, which verifies that the API endpoints are functioning correctly.
- Implemented API Security: I secured the API endpoints by requiring ADMIN role credentials for all write operations (POST, PUT, DELETE), while keeping read operations (GET) public.
- Added Caching: To optimize performance and reduce database load, I implemented caching for read operations on both individual and lists of employees.
- Robust Error Handling: I implemented a exception handler that distinguishes between "resource not found" (404) errors and other internal server errors (500), making the API more reliable for users.
- Corrected Creation Logic: Addressed a bug in the saveEmployee method to ensure new employee records are created reliably.
- Enhanced Update Functionality: Improved the updateEmployee logic to support partial updates, allowing a client to send only the fields they wish to change without affecting other data.
- Refactoring: Refactored the Employee entity to use Lombok's @NoArgsConstructor for cleaner, more concise, and more maintainable code.
