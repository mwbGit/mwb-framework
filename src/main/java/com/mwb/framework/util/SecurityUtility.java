package com.mwb.framework.util;

import java.util.UUID;

public class SecurityUtility {

	public static final int EMAIL_EFFECTIVE_MINUTE = 24 * 60;
	public static final int MOBILE_EFFECTIVE_MINUTE_5 = 5;
	public static final int MOBILE_EFFECTIVE_MINUTE_30 = 30;

	public static final String MOBILE_REGISTER_NAME_PREFIX = "MOB_";
	public static final String EMAIL_REGISTER_NAME_PREFIX = "EMA_";

	public static final String SHBJ = "SHBJ";
	public static final String LOGIN_NAME = "LOGIN_NAME";
	public static final String EMAIL = "EMAIL";
	public static final String MOBILE = "MOBILE";

	public static final String VALIDATE = "VALIDATE";
	public static final String MODIFY = "MODIFY";

	public static String getDisplayMobile(String mobile) {
		return mobile.substring(0, 3) + "****"+ mobile.substring(mobile.length() - 4, mobile.length());
	}

	public static String getDisplayEmail(String email) {
		if (!email.contains("@") || email.split("@").length != 2) {
			return "";
		}

		String[] emails = email.split("@");
		String username = emails[0];
		String mail = emails[1];

		if (username.length() < 5) {
			username = "***";
		} else {
			username = email.substring(0, 4) + "****";
		}

		return username + "@" + mail;
	}

	public static String getEmailVerificationCode(String email) {
		return MD5Utility.digest(email).toString() + UUID.randomUUID();
	}

	public static String getResetPasswordEmailHref(String domain,
			String verificationCode) {
		String emailHref = "%s/security/resetPassword.html?code=%s";
		emailHref = String.format(emailHref, domain, verificationCode);
		emailHref = String.format("<a href='%s'>%s</a>", emailHref, emailHref);
		return emailHref;
	}

	public static String getValidateEmailHref(String domain,
			String verificationCode) {
		String emailHref = "%s/security/validateEmail.html?code=%s";
		emailHref = String.format(emailHref, domain, verificationCode);
		emailHref = String.format("<a href='%s'>%s</a>", emailHref, emailHref);
		return emailHref;
	}

	public static String getResetEmailHref(String domain,
			String verificationCode) {
		String emailHref = "%s/security/resetEmail.html?code=%s";
		emailHref = String.format(emailHref, domain, verificationCode);
		emailHref = String.format("<a href='%s'>%s</a>", emailHref, emailHref);
		return emailHref;
	}

}