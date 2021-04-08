CREATE TABLE code_authority
(
    ID           BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    CODE_ID BIGINT(20) NOT NULL,
    AUTHORITY_ID BIGINT(20) NOT NULL,
    CREATED_BY   VARCHAR(100) NOT NULL,
    CREATED      DATETIME     NOT NULL,
    UPDATED_BY   VARCHAR(100) NOT NULL,
    UPDATED      DATETIME     NOT NULL
);