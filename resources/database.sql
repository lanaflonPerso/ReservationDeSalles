-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le :  mer. 27 nov. 2019 à 14:18
-- Version du serveur :  5.7.26
-- Version de PHP :  7.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `salles`
--
drop database if exists salle;
drop database if exists salles;
CREATE DATABASE IF NOT EXISTS `salles` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;

drop user if exists 'UtilisateurSalle'@'%';
CREATE USER 'UtilisateurSalle'@'%' IDENTIFIED by 'password';
GRANT USAGE ON *.* TO 'UtilisateurSalle'@'%';

USE `salles`;

-- --------------------------------------------------------

--
-- Structure de la table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
CREATE TABLE IF NOT EXISTS `reservation`
(
  `id_salle`   int(11)      NOT NULL,
  `mail_user`  varchar(255) NOT NULL,
  `date_debut` date         NOT NULL,
  heure_debut  varchar(255) not null,
  `date_fin`   date         NOT NULL,
  heure_fin    varchar(255) not null,
  PRIMARY KEY (`id_salle`, `date_debut`, heure_debut, date_fin, heure_fin),
  KEY `ForeignUser` (`mail_user`)
);

-- --------------------------------------------------------

--
-- Structure de la table `salle`
--

DROP TABLE IF EXISTS `salle`;
CREATE TABLE IF NOT EXISTS `salle`
(
  `id_salle`  int(11)      NOT NULL AUTO_INCREMENT,
  `nom_salle` varchar(255) NOT NULL,
  PRIMARY KEY (`id_salle`),
  UNIQUE KEY `salle_nom_salle_uindex` (`nom_salle`)
);

--
-- Déchargement des données de la table `salle`
--

INSERT INTO `salle` (`id_salle`, `nom_salle`)
VALUES (1, 'Turing'),
       (2, 'Pascale'),
       (3, 'Boole'),
       (4, 'Lovelace'),
       (5, 'Shannon'),
       (6, 'Von Neumann'),
       (7, 'Chaine de production'),
       (8, 'S 228-229'),
       (9, 'TP Systeme'),
       (10, 'Unix A'),
       (11, 'Unix B'),
       (12, 'Windows A'),
       (13, 'Windows B');

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
CREATE TABLE IF NOT EXISTS `utilisateur`
(
  `mail_user`   varchar(255) NOT NULL,
  `nom_user`    varchar(255) DEFAULT NULL,
  `prenom_user` varchar(255) DEFAULT NULL,
  `password`    varchar(255) DEFAULT NULL,
  PRIMARY KEY (`mail_user`),
  UNIQUE KEY `USER_mail_user_uindex` (`mail_user`)
);

--
-- Déchargement des données de la table `user`
--

INSERT INTO `utilisateur` (`mail_user`, `nom_user`, `prenom_user`, `password`) VALUES
('pierhugocarpentier36@gmail.com', 'Carpentier', 'Pier-Hugo', '6007d824a59a256e20cecdcb01c204f3fece7d804f3ca76e71b2a5e5e91ad068');
COMMIT;

GRANT select on salles.salle to 'UtilisateurSalle'@'%';
grant select, insert on salles.utilisateur to 'UtilisateurSalle'@'%';
grant select, insert, update, delete on salles.reservation to 'UtilisateurSalle'@'%';
commit;

/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
