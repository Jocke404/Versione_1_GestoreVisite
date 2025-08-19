-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 19, 2025 at 11:14 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gestione_visite`
--

-- --------------------------------------------------------

--
-- Table structure for table `configuratori`
--

CREATE TABLE `configuratori` (
  `nome` varchar(255) NOT NULL,
  `cognome` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `password_modificata` tinyint(4) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `configuratori`
--

INSERT INTO `configuratori` (`nome`, `cognome`, `email`, `password`, `password_modificata`) VALUES
('Admin', 'Configuratore', 'admin@example.com', 'admin123', 0),
('Super', 'User', 'superuser@example.com', 'super456', 0);

-- --------------------------------------------------------

--
-- Table structure for table `credenziali_temporanee`
--

CREATE TABLE `credenziali_temporanee` (
  `id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `credenziali_temporanee`
--

INSERT INTO `credenziali_temporanee` (`id`, `username`, `password`) VALUES
(1, 'tempuser1', 'temppass1'),
(2, 'tempuser2', 'temppass2'),
(3, 'tempuser3', 'temppass3');

-- --------------------------------------------------------

--
-- Table structure for table `luoghi`
--

CREATE TABLE `luoghi` (
  `nome` varchar(255) NOT NULL,
  `descrizione` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `luoghi`
--

INSERT INTO `luoghi` (`nome`, `descrizione`) VALUES
('Acquario Marino', 'Un acquario con una grande varietà di specie marine.'),
('Base Aeronautica', 'LoremIpsum'),
('Brescia', 'Citt� fantastica'),
('Buffalora', 'LoremIpsumLoremIpsum'),
('Castello di Brescia', 'Alla scoperta del fantastico Castello di Brescia'),
('Castello Storico', 'Un castello medievale ben conservato.'),
('Ghedi', 'Bellissima citta'),
('Montichiari', 'LoremIpsum'),
('Museo d\'Arte Moderna', 'Un museo con una vasta collezione di opere d\'arte moderna.'),
('Parco Naturale', 'Un parco con sentieri escursionistici e fauna selvatica.'),
('Teatro Antico', 'Un teatro romano ancora in uso per spettacoli.');

-- --------------------------------------------------------

--
-- Table structure for table `utenti_unificati`
--

CREATE TABLE `utenti_unificati` (
  `nome` varchar(255) NOT NULL,
  `cognome` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `tipo_utente` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_modificata` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `utenti_unificati`
--

INSERT INTO `utenti_unificati` (`nome`, `cognome`, `email`, `password`, `tipo_utente`, `password_modificata`) VALUES
('Admin', 'Configuratore', 'admin@example.com', 'admin123', 'Configuratore', 0),
('Alessandro', 'Neri', 'alessandro.neri@example.com', 'password321', 'Volontario', 0),
('Francesca', 'Gialli', 'francesca.gialli@example.com', 'password654', 'Volontario', 0),
('Giulia', 'Verdi', 'giulia.verdi@example.com', 'passmodificata789', 'Volontario', 1),
('Luisa', 'Bianchi', 'luisa.bianchi@example.com', 'passmodificata456', 'Volontario', 1),
('Mario', 'Rossi', 'mario.rossi@example.com', 'passmodificata123', 'Volontario', 1),
('Super', 'User', 'superuser@example.com', 'super456', 'Configuratore', 0),
('Nome Temporaneo', 'Cognome Temporaneo', 'tempuser1', 'temppass1', 'TEMP', 1),
('Nome Temporaneo', 'Cognome Temporaneo', 'tempuser2', 'temppass2', 'TEMP', 1),
('Nome Temporaneo', 'Cognome Temporaneo', 'tempuser3', 'temppass3', 'TEMP', 1),
('TestAdd', 'Add', 'Test@gmail.com', '12345', 'Volontario', 0);

-- --------------------------------------------------------

--
-- Table structure for table `visite`
--

CREATE TABLE `visite` (
  `id` int(11) NOT NULL,
  `luogo` varchar(255) DEFAULT NULL,
  `tipo_visita` varchar(255) DEFAULT NULL,
  `volontario` varchar(255) DEFAULT NULL,
  `data` date DEFAULT NULL,
  `max_persone` int(11) DEFAULT 10,
  `stato` varchar(255) NOT NULL DEFAULT 'Proposta'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `visite`
--

INSERT INTO `visite` (`id`, `luogo`, `tipo_visita`, `volontario`, `data`, `max_persone`, `stato`) VALUES
(1, 'Museo d\'Arte Moderna', 'Arte Moderna', 'Mario Rossi', '2025-03-16', 50, 'Effettuata'),
(2, 'Parco Naturale', 'Escursione', 'Luisa Bianchi', '2025-03-16', 50, 'Confermata'),
(3, 'Castello Storico', 'Storia Medievale', 'Giulia Verdi', '2025-03-16', 50, 'Proposta'),
(4, 'Acquario Marino', 'Biologia Marina', 'Alessandro Neri', '2025-03-16', 50, 'Proposta'),
(5, 'Teatro Antico', 'Spettacolo Teatrale', 'Francesca Gialli', '2025-03-16', 50, 'Proposta'),
(6, 'Acquario Marino', 'Biologia Marina', 'Mario Rossi', '2025-03-16', 50, 'Proposta'),
(7, 'Acquario Marino', 'Biologia Marina', 'Mario Rossi', '2025-06-05', 50, 'Proposta'),
(8, 'Castello Storico', 'Storia Medievale', 'Giulia Verdi', '2025-06-30', 50, 'Proposta'),
(9, 'Castello di Brescia', 'Storico Medievale', 'Francesca Gialli', '2025-03-31', 50, 'Proposta'),
(10, 'Montichiari', 'Visita citta', 'Mario Rossi', '2025-03-31', 50, 'Proposta'),
(11, 'Montichiari', 'Caccia al tesoro Cittadina', 'luisa.bianchi@example.com', '2025-03-31', 50, 'Proposta'),
(12, 'Brescia', 'Caccia al tesoro', 'Mario Rossi', '2025-03-31', 50, 'Proposta'),
(13, 'Ghedi', 'Aeronautica', 'Mario Rossi', '2025-03-31', 50, 'Proposta'),
(14, 'Base Aeronautica', 'Scuola di volo', 'Mario Rossi', '2025-03-31', 50, 'Completa'),
(15, 'Base Aeronautica', 'scuola di volo', 'Mario Rossi', '2025-05-12', 50, 'Cancellata'),
(16, 'Buffalora', 'Museo Metro', 'Alessandro Neri', '2025-08-01', 50, 'Effettuata'),
(17, 'Brescia', 'museo', 'TestAdd Add', '2025-11-03', 20, 'Confermata');

-- --------------------------------------------------------

--
-- Table structure for table `volontari`
--

CREATE TABLE `volontari` (
  `nome` varchar(255) NOT NULL,
  `cognome` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `tipi_di_visite` text DEFAULT NULL,
  `password_modificata` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `volontari`
--

INSERT INTO `volontari` (`nome`, `cognome`, `email`, `password`, `tipi_di_visite`, `password_modificata`) VALUES
('Alessandro', 'Neri', 'alessandro.neri@example.com', 'password321', 'Arte, Scienza', 0),
('Francesca', 'Gialli', 'francesca.gialli@example.com', 'password654', 'Natura, Storia', 0),
('Giulia', 'Verdi', 'giulia.verdi@example.com', 'passmodificata789', 'Storia, Natura', 1),
('Luisa', 'Bianchi', 'luisa.bianchi@example.com', 'passmodificata456', 'Natura, Scienza', 1),
('Mario', 'Rossi', 'mario.rossi@example.com', 'passmodificata123', 'Arte, Storia', 1),
('TestAdd', 'Add', 'Test@gmail.com', '12345', '', 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `configuratori`
--
ALTER TABLE `configuratori`
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `credenziali_temporanee`
--
ALTER TABLE `credenziali_temporanee`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `luoghi`
--
ALTER TABLE `luoghi`
  ADD PRIMARY KEY (`nome`);

--
-- Indexes for table `utenti_unificati`
--
ALTER TABLE `utenti_unificati`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `visite`
--
ALTER TABLE `visite`
  ADD PRIMARY KEY (`id`),
  ADD KEY `luogo` (`luogo`);

--
-- Indexes for table `volontari`
--
ALTER TABLE `volontari`
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `credenziali_temporanee`
--
ALTER TABLE `credenziali_temporanee`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `visite`
--
ALTER TABLE `visite`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `visite`
--
ALTER TABLE `visite`
  ADD CONSTRAINT `visite_ibfk_1` FOREIGN KEY (`luogo`) REFERENCES `luoghi` (`nome`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
