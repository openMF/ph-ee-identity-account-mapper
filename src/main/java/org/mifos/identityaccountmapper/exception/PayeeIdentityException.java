package org.mifos.identityaccountmapper.exception;

import java.text.MessageFormat;

public class PayeeIdentityException extends RuntimeException{
    public PayeeIdentityException(String message) {
        super(message);
    }

    public static PayeeIdentityException payeeIdentityNotFound(final String payeeIdentity) {
        String stringWithPlaceHolder = "Payee Identity with {0} does not exist!";
        return new PayeeIdentityException(MessageFormat.format(stringWithPlaceHolder,payeeIdentity));
    }
}
