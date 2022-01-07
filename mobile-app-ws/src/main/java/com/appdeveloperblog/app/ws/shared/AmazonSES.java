package com.appdeveloperblog.app.ws.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.appdeveloperblog.app.ws.shared.dto.UserDTO;

public class AmazonSES {
	final String FROM = "ornui.99.of@gmail.com"; //this address must be verified with AWS SES
	final String SUBJECT = "One last step to complete your registration with RestApp";
	final String HTMLBODY = "<h1>Please verify your email address</h1>"
			+ "<p>Thank you for registering with our mobile app. To complete registration process and be able to log in,"
			+ " click on the following link: "
			+ "<a href='http://ec2-54-67-70-58.us-west-1.compute.amazonaws.com:8080/verification-service/email-verification.html?token=$tokenValue'>"
			+ "Final step to complete your registration" + "</a><br/><br/>"
			+ "Thank you! And we are waiting for you inside!";

	// The email body for recipients with non-HTML email clients.
	final String TEXTBODY = "Please verify your email address. "
			+ "Thank you for registering with our mobile app. To complete registration process and be able to log in,"
			+ " open then the following URL in your browser window: "
			+ " http://ec2-54-67-70-58.us-west-1.compute.amazonaws.com:8080/verification-service/email-verification.html?token=$tokenValue"
			+ " Thank you! And we are waiting for you inside!";
	
	public void verifyEmail(UserDTO user) {
		System.setProperty("aws.accessKeyId", "AKIA6PQOKEHXL3SIRRST "); 	
		System.setProperty("aws.secretKey", "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"); 
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceAsyncClientBuilder.standard().withRegion(Regions.SA_EAST_1).build();
		
		String htmlBody = HTMLBODY.replace("$tokenValue", user.getEmailVerificationToken()); 
		String textBody = TEXTBODY.replace("$tokenValue", user.getEmailVerificationToken());
		
		SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses(user.getEmail()))
							.withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBody))
							.withText(new Content().withCharset("UTF-8").withData(textBody)))
							.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
							.withSource(FROM);
		
		client.sendEmail(request);
		System.out.print("Email Sent!");
	}
}