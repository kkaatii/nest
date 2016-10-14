CREATE SCHEMA `photon`
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_bin;

CREATE TABLE `photon`.`catalog` (
  `cid`     INT UNSIGNED NOT NULL,
  `country` VARCHAR(24)  NULL,
  `created` DATE         NULL,
  `date`    DATE         NULL,
  PRIMARY KEY (`cid`)
);

CREATE TABLE `photon`.`viewlog` (
  `vlid`  INT UNSIGNED     NOT NULL AUTO_INCREMENT,
  `cid`   INT UNSIGNED     NOT NULL,
  `count` TINYINT UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY (`vlid`),
  FOREIGN KEY (`cid`)
  REFERENCES `catalog` (`cid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);