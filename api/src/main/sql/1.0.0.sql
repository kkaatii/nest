CREATE SCHEMA `mfw`
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_bin;

CREATE TABLE `mfw`.`catalog` (
  `article_id` INT UNSIGNED NOT NULL,
  `country`    VARCHAR(24)  NULL,
  `created`    DATE         NULL,
  `date`       DATE         NULL,
  PRIMARY KEY (`article_id`)
);

CREATE TABLE `mfw`.`viewlog` (
  `viewlog_id` INT UNSIGNED     NOT NULL AUTO_INCREMENT,
  `article_id` INT UNSIGNED     NOT NULL,
  `user_id`    INT UNSIGNED     NOT NULL,
  `count`      TINYINT UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY (`viewlog_id`),
  FOREIGN KEY (`article_id`)
  REFERENCES `catalog` (`article_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE `mfw`.`user` (
  `user_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name`    VARCHAR(64)  NULL,
  PRIMARY KEY (`user_id`)
);

CREATE DEFINER =`sa`@`%` PROCEDURE `get_rands`(IN cnt INT)
  BEGIN
    DROP TEMPORARY TABLE IF EXISTS rands;
    CREATE TEMPORARY TABLE rands (
      rand_id INT UNSIGNED
    );
    loop_me: LOOP
      IF cnt < 1
      THEN
        LEAVE loop_me;
      END IF;
      INSERT INTO rands
        SELECT r1.article_id
        FROM viewlog AS r1
          JOIN
          (SELECT (rand() *
                   (SELECT max(viewlog_id)
                    FROM viewlog)) AS id)
            AS r2
        WHERE r1.viewlog_id >= r2.id
        ORDER BY r1.viewlog_id ASC
        LIMIT 1;
      SET cnt = cnt - 1;
    END LOOP loop_me;
    SELECT *
    FROM rands;
  END