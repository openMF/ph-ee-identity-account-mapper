CREATE TABLE identity_details (
  id                      BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  master_id               VARCHAR(200)    NOT NULL,
  created_on              TIMESTAMP                                       NOT NULL,
  source_bb_id            VARCHAR(200)                                     NOT NULL,
  payee_identity     VARCHAR(200)                                      NOT NULL
);

CREATE TABLE payment_modality_details (
id                      BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  master_id               VARCHAR(200)                                       NOT NULL,
  modality                BIGINT(20)                                      NULL DEFAULT NULL,
  destination_account     VARCHAR(200)                                      NULL DEFAULT NULL,
  institution_code        VARCHAR(200)                                       NULL DEFAULT NULL
);

CREATE TABLE error_tracking (
    id                      BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  request_id               VARCHAR(200)                                     NOT NULL,
  payee_identity        VARCHAR(200)                           NOT NULL,
  modality                BIGINT(20)                                      NULL DEFAULT NULL,
  error_description     VARCHAR(200)                                       NOT NULL

);