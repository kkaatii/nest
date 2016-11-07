CREATE SCHEMA `tube`
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_bin;

CREATE TABLE `tube`.`node` (
  `id`      INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`    VARCHAR(100) NULL,
  `frame`   VARCHAR(100) NOT NULL,
  `active`  BOOL         NOT NULL,
  `type`    VARCHAR(30)  NOT NULL,
  `created` DATETIME     NULL,
  `updated` DATETIME     NULL,
  `digest`  VARCHAR(255) NULL,
  `content` TEXT         NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `tube`.`arrow` (
  `id`     INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `origin` INT UNSIGNED NOT NULL,
  `target` INT UNSIGNED NOT NULL,
  `active` BOOL         NOT NULL,
  `type`   VARCHAR(50)  NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`origin`)
  REFERENCES `tube`.`node` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (`target`)
  REFERENCES `tube`.`node` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);