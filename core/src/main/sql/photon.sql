CREATE SCHEMA `photon`
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_bin;

CREATE TABLE `photon`.`owner` (
  `id`       INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `nickname` VARCHAR(50)  NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`nickname`)
);

CREATE TABLE `photon`.`owner_auth` (
  `auth_id`  VARCHAR(50)  NOT NULL,
  `owner_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`auth_id`),
  FOREIGN KEY (`owner_id`)
  REFERENCES `photon`.`owner` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE `photon`.`frame_access` (
  `owner_id` INT UNSIGNED NOT NULL,
  `frame`    VARCHAR(30)  NOT NULL,
  `access`   TINYINT(3)   NOT NULL,
  PRIMARY KEY (`owner_id`, `frame`),
  FOREIGN KEY (`owner_id`)
  REFERENCES `photon`.`owner` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE `photon`.`node` (
  `id`       INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`     VARCHAR(100) NULL,
  `owner_id` INT UNSIGNED NOT NULL,
  `frame`    VARCHAR(30)  NOT NULL,
  `active`   BOOL         NOT NULL,
  `type`     VARCHAR(30)  NOT NULL,
  `created`  DATETIME     NULL,
  `updated`  DATETIME     NULL,
  `digest`   VARCHAR(255) NULL,
  `content`  TEXT         NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`owner_id`)
  REFERENCES `photon`.`owner` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE `photon`.`arrow` (
  `id`           INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `origin`       INT UNSIGNED NOT NULL,
  `target`       INT UNSIGNED NOT NULL,
  `target_frame` VARCHAR(30)  NOT NULL,
  `active`       BOOL         NOT NULL,
  `type`         VARCHAR(50)  NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`origin`)
  REFERENCES `photon`.`node` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`target`)
  REFERENCES `photon`.`node` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);