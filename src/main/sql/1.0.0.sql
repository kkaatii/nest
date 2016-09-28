CREATE SCHEMA `nest` DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_bin;

CREATE TABLE `nest`.`node` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(100) NULL ,
  `type` VARCHAR(20) NULL ,
  `state` CHAR(8) NOT NULL ,
  `timestamp` DATETIME NULL ,
  `digest` VARCHAR(127) NULL ,
  `content` TEXT NULL ,
  PRIMARY KEY (`id`) );
  
CREATE TABLE `nest`.`arrow` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `origin` INT NOT NULL ,
  `target` INT NOT NULL ,
  `type` VARCHAR(30) NOT NULL ,
  `extension` INT NULL ,
  PRIMARY KEY (`id`) );
    
CREATE TABLE `nest`.`extension` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `code` TEXT NULL ,
  PRIMARY KEY (`id`) );