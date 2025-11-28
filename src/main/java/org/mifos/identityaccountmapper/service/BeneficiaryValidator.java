package org.mifos.identityaccountmapper.service;

import static org.mifos.connector.common.exception.PaymentHubError.ExtValidationError;
import static org.mifos.identityaccountmapper.util.PaymentModalityEnum.ACCOUNT_ID;
import static org.mifos.identityaccountmapper.util.PaymentModalityEnum.MSISDN;
import static org.mifos.identityaccountmapper.util.PaymentModalityEnum.VIRTUAL_ADDRESS;
import static org.mifos.identityaccountmapper.util.PaymentModalityEnum.VOUCHER;
import static org.mifos.identityaccountmapper.util.PaymentModalityEnum.WALLET_ID;

import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.connector.common.exception.PaymentHubErrorCategory;
import org.mifos.connector.common.validation.ValidatorBuilder;
import org.mifos.identityaccountmapper.data.RequestDTO;
import org.mifos.identityaccountmapper.util.IdentityMapperValidatorsEnum;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryValidator {

    private static final String resource = "beneficiaryValidator";
    private static final String requestId = "requestID";
    private static final int expectedRequestIdLength = 12;
    private static final String payeeIdentity = "payeeIdentity";
    private static final int expectedPayeeIdentityLength = 20;
    private static final String financialAddress = "financialAddress";
    private static final int expectedFinancialAddressLength = 12;
    private static final String bankingInstitutionCode = "bankingInstitutionCode";
    private static final int expectedBankingInstitutionCodeLength = 11;
    private static final String paymentModality = "paymentModality";
    private static final int expectedPaymentModalityLength = 2;

    public PhErrorDTO validateCreateBeneficiary(RequestDTO request) {
        final ValidatorBuilder validatorBuilder = new ValidatorBuilder();
        validatorBuilder.reset().resource(resource).parameter(requestId).value(request.getRequestID())
                .isNullWithFailureCode(IdentityMapperValidatorsEnum.INVALID_REQUEST_ID).validateFieldMaxLengthWithFailureCodeAndErrorParams(
                        expectedRequestIdLength, IdentityMapperValidatorsEnum.INVALID_REQUEST_ID_LENGTH);

        if (validatorBuilder.hasError()) {
            validatorBuilder.errorCategory(PaymentHubErrorCategory.Validation.toString())
                    .errorCode(IdentityMapperValidatorsEnum.IDENTITY_MAPPER_SCHEMA_VALIDATION_ERROR.getCode())
                    .errorDescription(IdentityMapperValidatorsEnum.IDENTITY_MAPPER_SCHEMA_VALIDATION_ERROR.getMessage())
                    .developerMessage(IdentityMapperValidatorsEnum.IDENTITY_MAPPER_SCHEMA_VALIDATION_ERROR.getMessage())
                    .defaultUserMessage(IdentityMapperValidatorsEnum.IDENTITY_MAPPER_SCHEMA_VALIDATION_ERROR.getMessage());

            PhErrorDTO.PhErrorDTOBuilder phErrorDTOBuilder = new PhErrorDTO.PhErrorDTOBuilder(ExtValidationError.getErrorCode());
            phErrorDTOBuilder.fromValidatorBuilder(validatorBuilder);
            return phErrorDTOBuilder.build();
        }

        return null;
    }

    public Boolean validatePaymentModality(String paymentModality) {
        if (paymentModality != null && !(paymentModality.equals(ACCOUNT_ID.getValue()) || paymentModality.equals(MSISDN.getValue())
                || paymentModality.equals(VIRTUAL_ADDRESS.getValue()) || paymentModality.equals(WALLET_ID.getValue())
                || paymentModality.equals(VOUCHER.getValue()))) {
            return false;
        }
        return true;

    }

    public Boolean validateBankingInstitutionCode(String paymentModality, String bankingInstitutionCode) {
        if ((paymentModality != null
                && (paymentModality.equals(ACCOUNT_ID.getValue()) || paymentModality.equals(MSISDN.getValue())
                        || paymentModality.equals(WALLET_ID.getValue()))
                && ((bankingInstitutionCode == null || bankingInstitutionCode.isEmpty())
                        || ((bankingInstitutionCode != null && bankingInstitutionCode.length() > 11))))) {
            return false;
        }
        return true;

    }

    public Boolean validatePayeeIdentity(String payeeIdentity) {
        if (payeeIdentity == null || payeeIdentity.isEmpty() || (!payeeIdentity.isEmpty() && payeeIdentity.length() > 20)) {
            return false;
        }
        return true;
    }

    public Boolean validateFinancialAddress(String financialAddress) {
        if (financialAddress != null && financialAddress.length() > 30) {
            return false;
        }
        return true;
    }
}
