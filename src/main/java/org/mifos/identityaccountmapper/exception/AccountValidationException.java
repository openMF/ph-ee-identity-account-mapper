package org.mifos.identityaccountmapper.exception;

import java.text.MessageFormat;

public class AccountValidationException extends RuntimeException{
    public AccountValidationException(String message) {
        super(message);
    }

    public static AccountValidationException accountvalidationFailed(final String payeeIdentity) {
        String stringWithPlaceHolder = "Payee Identity {0} account validation failed!";
        return new AccountValidationException(MessageFormat.format(stringWithPlaceHolder,payeeIdentity));
    }
}
