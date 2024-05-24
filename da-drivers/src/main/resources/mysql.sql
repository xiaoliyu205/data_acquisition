USE `data_acquisition_dev`;

-- -----------------------------------------------------
-- Table `opcua_address`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `opcua_address` ;
CREATE TABLE IF NOT EXISTS `opcua_address` (
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `dpt`            VARCHAR(20) NULL DEFAULT NULL COMMENT 'DataPointType',
    `datapoint`      VARCHAR(20) NULL DEFAULT NULL COMMENT 'DataPoint',
    `plc_name`       VARCHAR(20) NULL DEFAULT NULL COMMENT 'plc名',
    `opcua_address`  VARCHAR(60) NULL DEFAULT NULL COMMENT 'Opc Ua地址',
    `url`            VARCHAR(50) NULL DEFAULT NULL COMMENT 'EndPointUrl地址',
    `namespace`      TINYINT NULL DEFAULT 0 COMMENT 'namespace index',
    `user_name`      VARCHAR(20) NULL DEFAULT NULL COMMENT '账号',
    `password`       VARCHAR(20) NULL DEFAULT NULL COMMENT '密码',
    `create_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'create_time',
    `update_time`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'update_time',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uniq_id_UNIQUE` (`id` ASC) VISIBLE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci
    COMMENT = 'OPC UA地址表';