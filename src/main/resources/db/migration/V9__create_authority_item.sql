CREATE TABLE authority_item
(
    ID           BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    AUTHORITY_ID BIGINT(20) NOT NULL,
    MENU_ID BIGINT(20) NOT NULL,
    DISPLAY_ORDER INT(10)       NOT NULL,
    `TYPES_JSON` VARCHAR(100) NOT NULL,
    CREATED_BY   VARCHAR(100) NOT NULL,
    CREATED      DATETIME     NOT NULL,
    UPDATED_BY   VARCHAR(100) NOT NULL,
    UPDATED      DATETIME     NOT NULL
);
