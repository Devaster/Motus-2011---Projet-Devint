SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `motusdatabase` DEFAULT CHARACTER SET latin1 ;
USE `motusdatabase` ;

-- -----------------------------------------------------
-- Table `motusdatabase`.`Words`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `motusdatabase`.`Words` (
  `id` INT NOT NULL ,
  `name` VARCHAR(45) NOT NULL ,
  `definition` TEXT NULL ,
  `theme` VARCHAR(45) NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


CREATE USER `julien` IDENTIFIED BY 'hutch/06';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
