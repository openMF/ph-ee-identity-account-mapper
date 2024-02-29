package org.mifos.identityaccountmapper.util;

import org.mifos.connector.common.exception.PaymentHubErrorCategory;
import org.mifos.connector.common.validation.ValidationCodeType;

public enum IdentityMapperValidatorsEnum implements ValidationCodeType {

    IDENTITY_MAPPER_VALIDATION_ERROR("error.msg.header.validation.errors",
            "The headers are invalid"), IDENTITY_MAPPER_SCHEMA_VALIDATION_ERROR("error.msg.schema.validation.errors",
                    "The request is invalid"), INVALID_CALLBACK_URL("error.msg.schema.callback.url.cannot.be.null.or.empty",
                            "Callback URL cannot be null or empty"), INVALID_CALLBACK_URL_LENGTH(
                                    "error.msg.schema.callback.url.length.is.invalid",
                                    "Callback URL length is invalid"), INVALID_REGISTERING_INSTITUTION_ID(
                                            "error.msg.schema.registering.institution.id.cannot.be.null.or.empty",
                                            "Registering Institution Id cannot be null or empty"), INVALID_REGISTERING_INSTITUTION_ID_LENGTH(
                                                    "error.msg.schema.registering.institution.id.length.is.invalid",
                                                    "Registering Institution Id cannot length is invalid"), INVALID_REQUEST_ID(
                                                            "error.msg.schema.request.id.cannot.be.null.or.empty",
                                                            "Request Id cannot be null or empty"), INVALID_REQUEST_ID_LENGTH(
                                                                    "error.msg.schema.request.id.length.is.invalid",
                                                                    "Request Id length is invalid"), INVALID_PAYEE_IDENTITY(
                                                                            "error.msg.schema.payee.identity.cannot.be.null.or.empty",
                                                                            "Payee Identity cannot be null or empty"), INVALID_PAYEE_IDENTITY_LENGTH(
                                                                                    "error.msg.schema.payee.identity.length.is.invalid",
                                                                                    "Payee Identity length is invalid"),

    INVALID_BANKING_INSTITUTION_CODE("error.msg.schema.banking.institution.code.cannot.be.null.or.empty",
            "Banking Institution Code cannot be null or empty"), INVALID_BANKING_INSTITUTION_CODE_LENGTH(
                    "error.msg.schema.banking.institution.code.length.is.invalid",
                    "Banking Institution Code length is invalid"), INVALID_FINANCIAL_ADDRESS(
                            "error.msg.schema.financial.address.cannot.be.null.or.empty",
                            "Financial Address cannot be null or empty"), INVALID_FINANCIAL_ADDRESS_LENGTH(
                                    "error.msg.schema.financial.address.length.is.invalid",
                                    "Financial Address length is invalid"), INVALID_PAYMENT_MODALITY(
                                            "error.msg.schema.payment.modality.cannot.be.null.or.empty",
                                            "Payment Modality cannot be null or empty"), INVALID_PAYMENT_MODALITY_VALUE(
                                                    "error.msg.schema.payment.modality.is.invalid",
                                                    "Payment Modality is invalid"), IDENTITY_MAPPER_HEADER_VALIDATION_ERROR(
                                                            "error.msg.header.validation.errors", "The headers are invalid");

    private final String code;
    private final String category;
    private final String message;

    IdentityMapperValidatorsEnum(String code, String message) {
        this.code = code;
        this.category = PaymentHubErrorCategory.Validation.toString();
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getCategory() {
        return this.category;
    }

    public String getMessage() {
        return message;
    }
}
