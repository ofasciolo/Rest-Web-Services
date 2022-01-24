<br />
<div align="center">
  <a href="https://github.com/ofasciolo/Rest-Web-Services">
    <img src="logo.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">README</h3>

</div>

### About This Proyect

This proyect is for learning about REST Web Services. It implements a Login, a Password Reset and an Email Verification. This project can be deployed to AWS with tomcat. It can also use H2 database. The emails used in this project need to be verified by AWS.

### Built With

- [Java 8](https://www.java.com/)
- [SpringBoot 2.5.6](https://spring.io/projects/spring-boot)
- [AWS](https://aws.amazon.com/)
- [Tomcat 9](https://tomcat.apache.org/)
- [MySQL](https://www.mysql.com/)

### How It Works

1. On EmailConstants class, change FROM to your email verified by AWS
   - If you want to deploy this on AWS, you'll need to change the other constants to you EC2 instance of AWS instead of localhost:8080
   - You will need to add your keys of your EC2 instance in AmazonSES with System.setProperty
2. Create a user with a mail verified by AWS
3. Verify the user recently created
4. Login with that user
5. Do whatever you like :)
   - Remember: use the token for whatever endoint you want to use!

### Postman

Here you will find the collection of endpoints that this proyect needs: [Collection](https://documenter.getpostman.com/view/7002421/UVXqGZTz).
