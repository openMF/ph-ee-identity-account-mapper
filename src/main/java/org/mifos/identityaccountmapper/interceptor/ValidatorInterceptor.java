package org.mifos.identityaccountmapper.interceptor;

import static org.mifos.connector.common.exception.PaymentHubError.ExtValidationError;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mifos.connector.common.channel.dto.PhErrorDTO;
import org.mifos.connector.common.exception.PaymentHubErrorCategory;
import org.mifos.connector.common.validation.ValidatorBuilder;
import org.mifos.identityaccountmapper.api.implementation.RegisterBeneficiaryApiController;
import org.mifos.identityaccountmapper.api.implementation.UpdateBeneficiaryApiController;
import org.mifos.identityaccountmapper.util.IdentityMapperValidatorsEnum;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class ValidatorInterceptor implements HandlerInterceptor {

    private static final String resource = "ValidatorInterceptor";
    private static final String callbackURL = "X-CallbackURL";
    private static final String registeringInstitutionId = "X-Registering-Institution-ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        log.debug("request at interceptor");

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            if (handlerMethod.getBeanType().equals(RegisterBeneficiaryApiController.class)) {

                // Using ValidatorBuilder for header validation
                final ValidatorBuilder validatorBuilder = new ValidatorBuilder();
                validatorBuilder.reset().resource(resource).parameter(callbackURL).value(request.getHeader(callbackURL))
                        .isNullWithFailureCode(IdentityMapperValidatorsEnum.INVALID_CALLBACK_URL);

                validatorBuilder.reset().resource(resource).parameter(registeringInstitutionId)
                        .value(request.getHeader(registeringInstitutionId))
                        .isNullWithFailureCode(IdentityMapperValidatorsEnum.INVALID_REGISTERING_INSTITUTION_ID);

                // If errors exist, set the response and return false
                if (validatorBuilder.hasError()) {
                    validatorBuilder.errorCategory(PaymentHubErrorCategory.Validation.toString())
                            .errorCode(IdentityMapperValidatorsEnum.IDENTITY_MAPPER_HEADER_VALIDATION_ERROR.getCode())
                            .errorDescription(IdentityMapperValidatorsEnum.IDENTITY_MAPPER_HEADER_VALIDATION_ERROR.getMessage())
                            .developerMessage(IdentityMapperValidatorsEnum.IDENTITY_MAPPER_HEADER_VALIDATION_ERROR.getMessage())
                            .defaultUserMessage(IdentityMapperValidatorsEnum.IDENTITY_MAPPER_HEADER_VALIDATION_ERROR.getMessage());

                    PhErrorDTO.PhErrorDTOBuilder phErrorDTOBuilder = new PhErrorDTO.PhErrorDTOBuilder(ExtValidationError.getErrorCode());
                    phErrorDTOBuilder.fromValidatorBuilder(validatorBuilder);

                    // Converting PHErrorDTO in JSON Format
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonResponse = objectMapper.writeValueAsString(phErrorDTOBuilder.build());

                    // Setting response status and writing the error message
                    response.setHeader("Content-Type", "application/json");
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    response.getWriter().write(jsonResponse);

                    return false;
                }
            } else if (handlerMethod.getBeanType().equals(UpdateBeneficiaryApiController.class)) {
                // Using ValidatorBuilder for header validation
                final ValidatorBuilder validatorBuilder = new ValidatorBuilder();
                validatorBuilder.reset().resource(resource).parameter(callbackURL).value(request.getHeader(callbackURL))
                        .isNullWithFailureCode(IdentityMapperValidatorsEnum.INVALID_CALLBACK_URL)
                        .validateFieldMaxLengthWithFailureCodeAndErrorParams(100, IdentityMapperValidatorsEnum.INVALID_CALLBACK_URL_LENGTH);

                validatorBuilder.reset().resource(resource).parameter(registeringInstitutionId)
                        .value(request.getHeader(registeringInstitutionId))
                        .isNullWithFailureCode(IdentityMapperValidatorsEnum.INVALID_REGISTERING_INSTITUTION_ID)
                        .validateFieldNotBlankAndLengthWithFailureCodeAndErrorParams(20,
                                IdentityMapperValidatorsEnum.INVALID_REGISTERING_INSTITUTION_ID_LENGTH);

                // If errors exist, set the response and return false
                if (validatorBuilder.hasError()) {
                    validatorBuilder.errorCategory(PaymentHubErrorCategory.Validation.toString())
                            .errorCode(IdentityMapperValidatorsEnum.IDENTITY_MAPPER_HEADER_VALIDATION_ERROR.getCode())
                            .errorDescription(IdentityMapperValidatorsEnum.IDENTITY_MAPPER_HEADER_VALIDATION_ERROR.getMessage())
                            .developerMessage(IdentityMapperValidatorsEnum.IDENTITY_MAPPER_HEADER_VALIDATION_ERROR.getMessage())
                            .defaultUserMessage(IdentityMapperValidatorsEnum.IDENTITY_MAPPER_HEADER_VALIDATION_ERROR.getMessage());

                    PhErrorDTO.PhErrorDTOBuilder phErrorDTOBuilder = new PhErrorDTO.PhErrorDTOBuilder(ExtValidationError.getErrorCode());
                    phErrorDTOBuilder.fromValidatorBuilder(validatorBuilder);

                    // Converting PHErrorDTO in JSON Format
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonResponse = objectMapper.writeValueAsString(phErrorDTOBuilder.build());

                    // Setting response status and writing the error message
                    response.setHeader("Content-Type", "application/json");
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    response.getWriter().write(jsonResponse);

                    return false;
                }
            }
        }
        return true;
    }
}
