package com.appdeveloperblog.app.ws.shared;

public class EmailConstants {
	public static final String FROM = "ornui.99.of@gmail.com"; //this address must be verified with AWS SES
	
	//Email Verification
	public static final String EMAIL_VERIFICATION_SUBJECT = "One last step to complete your registration with RestApp";
	public static final String EMAIL_VERIFICATION_HTML_BODY = "<h1>Please verify your email address</h1>"
			+ "<p>Thank you for registering with our mobile app. To complete registration process and be able to log in,"
			+ " click on the following link: "
			+ "<a href='http://localhost:8080/verification-service/email-verification.html?token=$tokenValue'>"
			+ "Final step to complete your registration" + "</a><br/><br/>"
			+ "Thank you! And we are waiting for you inside!";
	public static final String EMAIL_VERIFICATION_TEXT_BODY = "Please verify your email address. "
			+ "Thank you for registering with our mobile app. To complete registration process and be able to log in,"
			+ " open then the following URL in your browser window: "
			+ " http://localhost:8080/verification-service/email-verification.html?token=$tokenValue"
			+ " Thank you! And we are waiting for you inside!";
	
	//Password reset
	
	public static final String PASSWORD_RESET_SUBJECT = "Password reset request";
	public static final String PASSWORD_RESET_HTML_BODY = "<h1>A request to reset your password</h1>"
		      + "<p>Hi, $firstName!</p> "
		      + "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it."
		      + " otherwise please click on the link below to set a new password: " 
		      + "<a href='http://localhost:8080/verification-service/password-reset.html?token=$tokenValue'>"
		      + " Click this link to Reset Password"
		      + "</a><br/><br/>"
		      + "Thank you!";
	public static final String PASSWORD_RESET_TEXT_BODY = "A request to reset your password "
		      + "Hi, $firstName! "
		      + "Someone has requested to reset your password with our project. If it were not you, please ignore it."
		      + " otherwise please open the link below in your browser window to set a new password:" 
		      + " http://localhost:8080/verification-service/password-reset.html?token=$tokenValue"
		      + " Thank you!";
}
