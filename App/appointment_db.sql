-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 04, 2025 at 01:48 PM
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
-- Database: `appointment_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `appointments`
--

CREATE TABLE `appointments` (
  `id` bigint(20) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `start_time` datetime(6) NOT NULL,
  `end_time` datetime(6) NOT NULL,
  `reminder_sent` tinyint(1) DEFAULT 0,
  `user_id` bigint(20) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `status` enum('CANCELLED','COMPLETED','CONFIRMED','MISSED','SCHEDULED') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `appointments`
--

INSERT INTO `appointments` (`id`, `title`, `description`, `start_time`, `end_time`, `reminder_sent`, `user_id`, `location`, `status`) VALUES
(26, 'fgjv', 'dhf', '2025-05-31 11:25:00.000000', '2025-05-31 14:25:00.000000', 0, 7, '23ew', 'COMPLETED'),
(27, 'dsggskcgh', 'dshfdg', '2025-05-22 05:50:00.000000', '2025-05-22 07:23:00.000000', 1, 9, 'asdhkgj', 'MISSED'),
(28, '253', 'wet', '2025-05-24 05:24:00.000000', '2025-05-24 06:24:00.000000', 0, 9, '23ew', 'COMPLETED'),
(29, 'fhgh', 'tylug', '2025-05-23 05:25:00.000000', '2025-05-24 05:25:00.000000', 0, 9, 'eyr', 'MISSED'),
(30, 'fhgh', 'tylug', '2025-05-23 05:25:00.000000', '2025-05-24 05:25:00.000000', 0, 7, 'eyr', 'COMPLETED'),
(31, 'sdfg', 'r68', '2025-05-26 10:50:00.000000', '2025-05-27 10:35:00.000000', 0, 7, 'gui', 'MISSED'),
(32, 'gf', 'wrh', '2025-06-03 13:44:00.000000', '2025-06-04 13:44:00.000000', 0, 7, '23ew', 'MISSED'),
(33, 'sdfg', 'sdf', '2025-06-03 13:40:00.000000', '2025-06-03 14:40:00.000000', 0, 7, 'reg', 'MISSED'),
(37, 'mn', 'hgj', '2025-06-02 13:37:00.000000', '2025-06-02 14:37:00.000000', 0, 7, '23ew', 'MISSED'),
(38, 'test', 'sdfg', '2025-06-01 11:44:00.000000', '2025-06-01 11:44:00.000000', 0, 7, '23ew', 'MISSED'),
(40, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:01:03.000000', '2025-06-05 00:31:03.000000', 0, 19, 'Phòng B', 'MISSED'),
(41, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:01:03.000000', '2025-06-05 00:31:03.000000', 0, 19, 'Phòng B', 'MISSED'),
(42, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:01:52.000000', '2025-06-05 00:31:52.000000', 0, 20, 'Phòng B', 'MISSED'),
(43, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:01:52.000000', '2025-06-05 00:31:52.000000', 0, 20, 'Phòng B', 'MISSED'),
(45, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:11:23.000000', '2025-06-05 00:41:23.000000', 0, 21, 'Phòng B', 'MISSED'),
(46, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:11:23.000000', '2025-06-05 00:41:23.000000', 0, 21, 'Phòng B', 'MISSED'),
(51, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:33:42.000000', '2025-06-05 01:03:42.000000', 0, 28, 'Phòng B', 'MISSED'),
(52, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:33:42.000000', '2025-06-05 01:03:42.000000', 0, 29, 'Phòng B', 'SCHEDULED');

-- --------------------------------------------------------

--
-- Table structure for table `appointment_requests`
--

CREATE TABLE `appointment_requests` (
  `id` bigint(20) NOT NULL,
  `sender_id` bigint(20) NOT NULL,
  `receiver_id` bigint(20) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `start_time` datetime(6) NOT NULL,
  `end_time` datetime(6) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `status` enum('ACCEPTED','PENDING','REJECTED') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `appointment_requests`
--

INSERT INTO `appointment_requests` (`id`, `sender_id`, `receiver_id`, `title`, `description`, `start_time`, `end_time`, `location`, `status`) VALUES
(8, 7, 7, 'dfgh', 'asdf', '2025-05-21 00:56:22.000000', '2025-05-21 00:56:22.000000', NULL, 'ACCEPTED'),
(9, 8, 7, 'htsr', 'fdcmh', '2025-05-22 01:29:00.000000', '2025-05-23 01:29:00.000000', '23ew', 'REJECTED'),
(10, 9, 7, 'fhgh', 'tylug', '2025-05-23 05:25:00.000000', '2025-05-24 05:25:00.000000', 'eyr', 'ACCEPTED'),
(11, 7, 7, '1235r', 'wery', '2025-05-30 15:47:00.000000', '2025-05-31 15:47:00.000000', '23ew', 'REJECTED'),
(12, 9, 7, 'sdfg', 'etug', '2025-05-31 03:52:00.000000', '2025-05-31 03:52:00.000000', '23ew', 'REJECTED'),
(13, 7, 7, 'qwef', 'adfh', '2025-06-02 08:46:00.000000', '2025-06-02 08:46:00.000000', '23ew', 'REJECTED'),
(14, 7, 7, 'qwe', 'adfg', '2025-06-02 08:47:00.000000', '2025-06-02 08:47:00.000000', '23ew', 'REJECTED'),
(15, 7, 7, 'sdfg', 'asdf', '2025-06-02 08:52:00.000000', '2025-06-02 08:52:00.000000', '23ew', 'REJECTED'),
(16, 7, 7, 'test', 'sdfg', '2025-06-01 11:44:00.000000', '2025-06-01 11:44:00.000000', '23ew', 'ACCEPTED'),
(17, 17, 18, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-03 23:57:38.000000', '2025-06-04 00:27:38.000000', 'Phòng B', 'PENDING'),
(18, 19, 19, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:01:03.000000', '2025-06-05 00:31:03.000000', 'Phòng B', 'ACCEPTED'),
(19, 19, 19, 'Họp từ chối', 'Demo gửi yêu cầu từ chối', '2025-06-05 00:01:03.000000', '2025-06-05 00:31:03.000000', 'Phòng C', 'REJECTED'),
(20, 20, 20, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:01:52.000000', '2025-06-05 00:31:52.000000', 'Phòng B', 'ACCEPTED'),
(21, 20, 20, 'Họp từ chối', 'Demo gửi yêu cầu từ chối', '2025-06-05 00:01:52.000000', '2025-06-05 00:31:52.000000', 'Phòng C', 'REJECTED'),
(22, 21, 21, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:11:23.000000', '2025-06-05 00:41:23.000000', 'Phòng B', 'ACCEPTED'),
(23, 21, 21, 'Họp từ chối', 'Demo gửi yêu cầu từ chối', '2025-06-05 00:11:23.000000', '2025-06-05 00:41:23.000000', 'Phòng C', 'REJECTED'),
(24, 22, 22, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:15:41.000000', '2025-06-05 00:45:41.000000', 'Phòng B', 'PENDING'),
(25, 7, 7, 'sdfg', 'sdfg', '2025-06-06 07:08:00.000000', '2025-06-07 07:08:00.000000', '23ew', 'REJECTED'),
(26, 25, 24, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:28:48.000000', '2025-06-05 00:58:48.000000', 'Phòng B', 'PENDING'),
(27, 26, 26, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:31:34.000000', '2025-06-05 01:01:34.000000', 'Phòng B', 'PENDING'),
(28, 28, 29, 'Họp chấp nhận', 'Demo gửi yêu cầu chấp nhận', '2025-06-05 00:33:42.000000', '2025-06-05 01:03:42.000000', 'Phòng B', 'ACCEPTED'),
(29, 28, 29, 'Họp từ chối', 'Demo gửi yêu cầu từ chối', '2025-06-05 00:33:42.000000', '2025-06-05 01:03:42.000000', 'Phòng C', 'REJECTED'),
(30, 7, 9, 'asdf', 'asdg', '2025-06-05 11:20:00.000000', '2025-06-06 11:20:00.000000', 'fghjs', 'PENDING');

-- --------------------------------------------------------

--
-- Table structure for table `locations`
--

CREATE TABLE `locations` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `locations`
--

INSERT INTO `locations` (`id`, `name`) VALUES
(7, '23ew'),
(5, 'asd'),
(8, 'eyr'),
(9, 'fghjs');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `email`, `password`) VALUES
(7, 'test', 'test@gmail.com', '$2a$10$25HuYagflGzh4U/3AboJYOKRuUEa1EOf75oXxC05nTSFatmfpqu8G'),
(8, 'khanhtoan', 'ktoan@gmail.com', '$2a$10$CLV2ZFjDr2qWPpU3ijqByuCmj3CyEPmvXvtmcw23PWuDMzrmDdVIa'),
(9, 'test1', 'test1@gmail.com', '$2a$10$psuYqFwsY/Sm65IIN9b2bOqJoxMm3IUy0z0ySBdMPPfL.woGGb.vi'),
(12, 'user54455', 'user54455@example.com', '$2a$10$9PlYumiEWK1wI.bC85UPc.JPFTOS7bXekFaFBEVMH4SdXIqAWfmuS'),
(13, 'user97575', 'user97575@example.com', '$2a$10$U/S5em.mu0zVmTB2Afcb1OaYZi1/11hBlAsjlCwHnV17V9nBticOW'),
(14, 'user94323', 'user94323@example.com', '$2a$10$BCztQHzc9qH0l/5nAO89rOzGQBAvNz2wp7MhOSmMQp1lP95kvdr26'),
(15, 'user65369', 'user65369@example.com', '$2a$10$3Nnehy2ZN.H.cm5//O2YCOQLQd0dFWBtpYywCQ85.DUJp3Dga2UVy'),
(16, 'user44193', 'user44193@example.com', '$2a$10$nQMHhifn/.pfciw6pdhbwuCaYBoBc0p27aSQAsD0SytaWFmY4lIqO'),
(17, 'user97318', 'user97318@example.com', '$2a$10$E27coy8ICisU8zxDTxwxruPFr2AMdZGWH.ggWzMrvc45FMF1VC5n2'),
(18, 'user36986', 'user36986@example.com', '$2a$10$gQizv1zucyJKte2ivRJHweu3owE9FE9QZ/swe7K6RmU8FFwHkBxOy'),
(19, 'user18184', 'user18184@example.com', '$2a$10$bsHONhuUVBJaMkFKdPca.uiR4g6P3ua8cbkjLk.22kg6ZKd4ssRYW'),
(20, 'user83647', 'user83647@example.com', '$2a$10$gycaWTvPOki2VT9oqwtbdeez24zGiMihwMtDR2yTuj9XuH7f4Xc6u'),
(21, 'user81266', 'user81266@example.com', '$2a$10$qQVy5RAX7JAgwbqyVDJF1usuLiRSIdeEtbRgQi5Yz/YrMRNkCd5Va'),
(22, 'user9880', 'user9880@example.com', '$2a$10$h/9gnSoJtwXu8SCTyl0CKOfAvwYWTTA5nNTvAfWp33kk0E/dipDfG'),
(23, 'user52420', 'user52420@example.com', '$2a$10$MzhSYSrvH0HoxgtLsflL4.0vhzQJvA2aAOljrtw.NsaUhxBQwDQ1O'),
(24, 'user93698', 'user93698@example.com', '$2a$10$ca9EtgUnM9t990naQXat7.UDlmk8xEC2ABQap9klzv2eM/3JKdbdq'),
(25, 'user44947', 'user44947@example.com', '$2a$10$b8eG7SCAA6cDu/lhjayYbOFdNX07M8.9T/3FIuZB9sunPTkSYWXfy'),
(26, 'user60877', 'user60877@example.com', '$2a$10$ZI8vSDb6GVh/f84DwkLUYemH4cZyi26g3fFBvfRTgY92xWTRUFVMS'),
(27, 'user95302', 'user95302@example.com', '$2a$10$XVIDIMMMLhFa3QrSmUEyqObpsfFd4egDuTMP7xmzBGHmAdnantF1W'),
(28, 'user31101', 'user31101@example.com', '$2a$10$2J6teKl88vsMi.O4f7M5euW1Z2wr2eVMZ/3bDxROvO3G8B2Q4ukBS'),
(29, 'user68170', 'user68170@example.com', '$2a$10$Q.Ycb//cSgA1LHYE0KhG/eoAZGHr76mjK3Ihk8CUqBlUwV4vZgsf.');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `appointments`
--
ALTER TABLE `appointments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `appointment_requests`
--
ALTER TABLE `appointment_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sender_id` (`sender_id`),
  ADD KEY `receiver_id` (`receiver_id`);

--
-- Indexes for table `locations`
--
ALTER TABLE `locations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`),
  ADD UNIQUE KEY `UKqvgktk8bt8v993m1k9ld5036k` (`name`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `appointments`
--
ALTER TABLE `appointments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT for table `appointment_requests`
--
ALTER TABLE `appointment_requests`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `locations`
--
ALTER TABLE `locations`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `appointments`
--
ALTER TABLE `appointments`
  ADD CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `appointment_requests`
--
ALTER TABLE `appointment_requests`
  ADD CONSTRAINT `appointment_requests_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `appointment_requests_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
