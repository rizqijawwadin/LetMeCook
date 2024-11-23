-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 23, 2024 at 12:46 PM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `mydb`
--

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `category_id` int NOT NULL,
  `category_name` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Table structure for table `category_has_recipe`
--

CREATE TABLE `category_has_recipe` (
  `category_id_category` int NOT NULL,
  `recipe_id_recipe` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Table structure for table `ingeredient`
--

CREATE TABLE `ingeredient` (
  `ingredient_id` int NOT NULL,
  `ingredient_name` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Table structure for table `inventory`
--

CREATE TABLE `inventory` (
  `user_id_user` int NOT NULL,
  `ingredient_id_ingredient` int NOT NULL,
  `id_inventory` int NOT NULL,
  `ingredient_pic` varchar(100) DEFAULT NULL,
  `buy_date` date DEFAULT NULL,
  `stock` int DEFAULT NULL,
  `unit` varchar(20) DEFAULT NULL,
  `place` varchar(20) DEFAULT NULL,
  `expiry_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Table structure for table `recipe`
--

CREATE TABLE `recipe` (
  `recipe_id` int NOT NULL,
  `name_recipe` varchar(45) DEFAULT NULL,
  `cooking_method` varchar(200) DEFAULT NULL,
  `image` varchar(100) DEFAULT NULL,
  `prep_time` varchar(20) DEFAULT NULL,
  `serves` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Table structure for table `recipe_has_ingredient`
--

CREATE TABLE `recipe_has_ingredient` (
  `recipe_id_recipe` int NOT NULL,
  `ingredient_id_ingredient` int NOT NULL,
  `unit` varchar(20) DEFAULT NULL,
  `stock` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int NOT NULL,
  `name` varchar(70) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `profile_picture` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `category_has_recipe`
--
ALTER TABLE `category_has_recipe`
  ADD PRIMARY KEY (`category_id_category`,`recipe_id_recipe`),
  ADD KEY `fk_kategori_has_resep_resep1_idx` (`recipe_id_recipe`),
  ADD KEY `fk_kategori_has_resep_kategori1_idx` (`category_id_category`);

--
-- Indexes for table `ingeredient`
--
ALTER TABLE `ingeredient`
  ADD PRIMARY KEY (`ingredient_id`);

--
-- Indexes for table `inventory`
--
ALTER TABLE `inventory`
  ADD PRIMARY KEY (`id_inventory`),
  ADD KEY `fk_user_has_bahan_bahan1_idx` (`ingredient_id_ingredient`),
  ADD KEY `fk_user_has_bahan_user1_idx` (`user_id_user`);

--
-- Indexes for table `recipe`
--
ALTER TABLE `recipe`
  ADD PRIMARY KEY (`recipe_id`);

--
-- Indexes for table `recipe_has_ingredient`
--
ALTER TABLE `recipe_has_ingredient`
  ADD PRIMARY KEY (`recipe_id_recipe`,`ingredient_id_ingredient`),
  ADD KEY `fk_resep_has_bahan_bahan1_idx` (`ingredient_id_ingredient`),
  ADD KEY `fk_resep_has_bahan_resep_idx` (`recipe_id_recipe`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `category_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ingeredient`
--
ALTER TABLE `ingeredient`
  MODIFY `ingredient_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `inventory`
--
ALTER TABLE `inventory`
  MODIFY `id_inventory` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `recipe`
--
ALTER TABLE `recipe`
  MODIFY `recipe_id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `category_has_recipe`
--
ALTER TABLE `category_has_recipe`
  ADD CONSTRAINT `fk_kategori_has_resep_kategori1` FOREIGN KEY (`category_id_category`) REFERENCES `category` (`category_id`),
  ADD CONSTRAINT `fk_kategori_has_resep_resep1` FOREIGN KEY (`recipe_id_recipe`) REFERENCES `recipe` (`recipe_id`);

--
-- Constraints for table `inventory`
--
ALTER TABLE `inventory`
  ADD CONSTRAINT `fk_user_has_bahan_bahan1` FOREIGN KEY (`ingredient_id_ingredient`) REFERENCES `ingeredient` (`ingredient_id`),
  ADD CONSTRAINT `fk_user_has_bahan_user1` FOREIGN KEY (`user_id_user`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `recipe_has_ingredient`
--
ALTER TABLE `recipe_has_ingredient`
  ADD CONSTRAINT `fk_resep_has_bahan_bahan1` FOREIGN KEY (`ingredient_id_ingredient`) REFERENCES `ingeredient` (`ingredient_id`),
  ADD CONSTRAINT `fk_resep_has_bahan_resep` FOREIGN KEY (`recipe_id_recipe`) REFERENCES `recipe` (`recipe_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
