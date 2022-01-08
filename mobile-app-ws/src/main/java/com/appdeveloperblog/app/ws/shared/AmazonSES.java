package com.appdeveloperblog.app.ws.shared;

import java.util.Objects;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.appdeveloperblog.app.ws.shared.dto.UserDTO;

public class AmazonSES {

	public void verifyEmail(UserDTO user) {
		
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceAsyncClientBuilder.standard().withRegion(Regions.SA_EAST_1).build();
		
		String htmlBody = EmailConstants.EMAIL_VERIFICATION_HTML_BODY.replace("$tokenValue", user.getEmailVerificationToken()); 
		String textBody = EmailConstants.EMAIL_VERIFICATION_TEXT_BODY.replace("$tokenValue", user.getEmailVerificationToken());
		
		SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses(user.getEmail()))
							.withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBody))
							.withText(new Content().withCharset("UTF-8").withData(textBody)))
							.withSubject(new Content().withCharset("UTF-8").withData(EmailConstants.EMAIL_VERIFICATION_SUBJECT)))
							.withSource(EmailConstants.FROM);
		
		client.sendEmail(request);
	}
	
	public Boolean sendPasswordResetRequest(String firstName, String email, String token) {
		
		Boolean returnValue = Boolean.FALSE; 
		
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceAsyncClientBuilder.standard().withRegion(Regions.SA_EAST_1).build();
		
		String htmlBody = EmailConstants.PASSWORD_RESET_HTML_BODY.replace("$tokenValue", token); 
		htmlBody = htmlBody.replace("$firstName", firstName);
		
		String textBody = EmailConstants.PASSWORD_RESET_TEXT_BODY.replace("$tokenValue", token);
		textBody = textBody.replace("$firstName", firstName);
		
		SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses(email))
							.withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBody))
							.withText(new Content().withCharset("UTF-8").withData(textBody)))
							.withSubject(new Content().withCharset("UTF-8").withData(EmailConstants.PASSWORD_RESET_SUBJECT)))
							.withSource(EmailConstants.FROM);
		
		SendEmailResult result = client.sendEmail(request);
		
		if(Objects.nonNull(result) && (Objects.nonNull(result.getMessageId()) && !result.getMessageId().isEmpty())) {
			returnValue = Boolean.TRUE;
		}
		
		return returnValue;
	}
}
