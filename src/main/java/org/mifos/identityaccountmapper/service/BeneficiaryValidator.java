package org.mifos.identityaccountmapper.service;

import static org.mifos.connector.common.exception.PaymentHubError.ExtValidationError;

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
}
