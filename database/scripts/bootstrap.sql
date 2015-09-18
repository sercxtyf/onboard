-- MySQL dump 10.13  Distrib 5.5.44, for debian-linux-gnu (x86_64)
--
-- Host: 192.168.100.10    Database: onboard-opensource
-- ------------------------------------------------------
-- Server version	5.5.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activity`
--

DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) NOT NULL,
  `companyId` int(11) NOT NULL,
  `subject` varchar(20) NOT NULL,
  `target` varchar(200) DEFAULT NULL,
  `content` varchar(500) DEFAULT NULL,
  `creatorId` int(11) NOT NULL,
  `action` varchar(100) NOT NULL,
  `attachId` int(11) NOT NULL,
  `attachType` varchar(30) NOT NULL DEFAULT '',
  `created` datetime NOT NULL,
  `creatorName` varchar(50) NOT NULL,
  `projectName` varchar(50) NOT NULL,
  `trash` tinyint(1) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `projectId` (`projectId`),
  KEY `creatorId` (`creatorId`),
  KEY `FK_activity_company` (`companyId`),
  CONSTRAINT `activity_ibfk_1` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`),
  CONSTRAINT `activity_ibfk_2` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_activity_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=631778 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `attach_todo`
--

DROP TABLE IF EXISTS `attach_todo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attach_todo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attachType` varchar(100) NOT NULL,
  `attachId` int(11) NOT NULL,
  `todoId` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=345 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `attachment`
--

DROP TABLE IF EXISTS `attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `attachment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) NOT NULL,
  `name` varchar(200) NOT NULL DEFAULT '',
  `size` bigint(20) NOT NULL,
  `contentType` varchar(100) NOT NULL DEFAULT '',
  `creatorId` int(11) NOT NULL,
  `attachId` int(11) NOT NULL,
  `attachType` varchar(30) NOT NULL DEFAULT '',
  `created` datetime NOT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `targetType` varchar(100) DEFAULT '',
  `targetId` int(11) DEFAULT NULL,
  `companyId` int(11) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `projectId` (`projectId`),
  KEY `creatorId` (`creatorId`),
  KEY `FK_attachment_company` (`companyId`),
  CONSTRAINT `attachment_ibfk_1` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`),
  CONSTRAINT `attachment_ibfk_2` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_attachment_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36542223 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bug`
--

DROP TABLE IF EXISTS `bug`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bug` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `companyId` int(11) NOT NULL,
  `projectId` int(11) NOT NULL,
  `creatorId` int(11) NOT NULL,
  `creatorName` varchar(100) NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` text,
  `createdTime` datetime NOT NULL,
  `completedTime` datetime DEFAULT NULL,
  `status` int(11) NOT NULL,
  `priority` int(11) NOT NULL,
  `assigneeId` int(11) DEFAULT NULL,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `dueTime` datetime DEFAULT NULL,
  `idInProject` int(11) DEFAULT '0',
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `bug_fk_companyId` (`companyId`),
  KEY `bug_fk_projectId` (`projectId`),
  KEY `bug_fk_creatorId` (`creatorId`),
  CONSTRAINT `bug_fk_companyId` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`),
  CONSTRAINT `bug_fk_creatorId` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`),
  CONSTRAINT `bug_fk_projectId` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `changelog`
--

DROP TABLE IF EXISTS `changelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `changelog` (
  `ID` decimal(20,0) NOT NULL,
  `APPLIED_AT` varchar(25) NOT NULL,
  `DESCRIPTION` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ci_build_trigger`
--

DROP TABLE IF EXISTS `ci_build_trigger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ci_build_trigger` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `trigger_type` varchar(255) DEFAULT NULL,
  `trigger_value` varchar(255) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_bdl5qcde5hx2aiv4b2ycmhip5` (`project_id`),
  CONSTRAINT `FK_bdl5qcde5hx2aiv4b2ycmhip5` FOREIGN KEY (`project_id`) REFERENCES `ci_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ci_builds`
--

DROP TABLE IF EXISTS `ci_builds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ci_builds` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `build_result` longtext,
  `created` datetime DEFAULT NULL,
  `project_index` int(11) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `test_result` longtext,
  `ci_project_id` int(11) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `buildResultStatus` varchar(255) DEFAULT NULL,
  `jenkinsBuildId` varchar(255) NOT NULL DEFAULT '0' COMMENT 'related jenkins build id',
  `hasTestReport` bit(1) NOT NULL DEFAULT b'0',
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `FK_54coqxagbf799hbuyo7vtqcwp` (`ci_project_id`),
  CONSTRAINT `FK_54coqxagbf799hbuyo7vtqcwp` FOREIGN KEY (`ci_project_id`) REFERENCES `ci_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ci_project`
--

DROP TABLE IF EXISTS `ci_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ci_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `companyId` int(11) DEFAULT NULL,
  `branch` varchar(255) DEFAULT NULL,
  `build_command` longtext,
  `build_version` varchar(255) DEFAULT NULL,
  `docker_container_id` varchar(255) DEFAULT NULL,
  `language_version` varchar(255) DEFAULT NULL,
  `project_type` varchar(255) DEFAULT NULL,
  `repository` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `test_command` longtext,
  `test_version` varchar(255) DEFAULT NULL,
  `last_build_id` int(11) DEFAULT NULL,
  `last_failed_build_id` int(11) DEFAULT NULL,
  `last_success_build_id` int(11) DEFAULT NULL,
  `keepContainerUp` bit(1) NOT NULL DEFAULT b'0',
  `exposedPort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `collection`
--

DROP TABLE IF EXISTS `collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `collection` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `companyId` int(11) NOT NULL,
  `projectName` varchar(50) NOT NULL DEFAULT '',
  `projectId` int(11) NOT NULL,
  `creatorId` int(11) NOT NULL,
  `creatorName` varchar(50) NOT NULL DEFAULT '',
  `attachId` int(11) NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  `title` varchar(200) NOT NULL DEFAULT '',
  `attachType` varchar(20) NOT NULL DEFAULT '',
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` text,
  `creatorId` int(11) NOT NULL,
  `attachId` int(11) NOT NULL,
  `attachType` varchar(30) NOT NULL DEFAULT '',
  `deleted` tinyint(1) NOT NULL,
  `projectId` int(11) NOT NULL DEFAULT '0',
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `creatorName` varchar(50) NOT NULL DEFAULT 'name',
  `companyId` int(11) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `creatorId` (`creatorId`),
  KEY `FK_comment_company` (`companyId`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_comment_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39207996 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commit_review`
--

DROP TABLE IF EXISTS `commit_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commit_review` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` text,
  `creatorId` int(11) NOT NULL,
  `creatorName` varchar(50) NOT NULL,
  `file` varchar(500) NOT NULL,
  `line` int(11) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `projectId` int(11) NOT NULL,
  `commitId` varchar(40) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `line_type` varchar(50) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commit_todo`
--

DROP TABLE IF EXISTS `commit_todo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commit_todo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `commitId` varchar(500) NOT NULL,
  `todoId` int(11) NOT NULL,
  `repoId` int(11) NOT NULL,
  `attachType` varchar(255) DEFAULT 'todo',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1807 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `creatorId` int(11) DEFAULT NULL,
  `created` datetime NOT NULL,
  `updated` datetime DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `privileged` bit(1) NOT NULL DEFAULT b'0' COMMENT '如果没有特权，默认为0。特权团队，比如说onboard，值为1',
  `money` int(11) NOT NULL DEFAULT '0',
  `lastPayTime` datetime DEFAULT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `creatorId` (`creatorId`),
  CONSTRAINT `company_ibfk_1` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1234590 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_application`
--

DROP TABLE IF EXISTS `company_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_application` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `teamName` varchar(255) NOT NULL DEFAULT '',
  `contactEmail` varchar(255) NOT NULL,
  `contactName` varchar(255) DEFAULT NULL,
  `description` varchar(4096) DEFAULT NULL,
  `teamSize` varchar(50) NOT NULL DEFAULT '',
  `codeHost` varchar(255) DEFAULT '',
  `code` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_limit`
--

DROP TABLE IF EXISTS `company_limit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_limit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `companyId` int(11) NOT NULL,
  `projectCount` int(11) DEFAULT NULL,
  `diskSize` int(11) DEFAULT NULL,
  `repositorySize` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_log`
--

DROP TABLE IF EXISTS `company_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `companyId` int(11) NOT NULL,
  `content` int(11) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `creatorId` int(11) DEFAULT NULL,
  `changes` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `company_privilege`
--

DROP TABLE IF EXISTS `company_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company_privilege` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `userId` int(10) NOT NULL,
  `companyId` int(10) NOT NULL,
  `isAdmin` bit(1) DEFAULT b'0' COMMENT '??????',
  `canCreateProject` bit(1) DEFAULT b'0' COMMENT '???????',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1144 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `department` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `companyId` int(11) NOT NULL,
  `customOrder` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `group_ibfk_1` (`companyId`),
  CONSTRAINT `department_ibfk_1` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `discussion`
--

DROP TABLE IF EXISTS `discussion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `discussion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) NOT NULL,
  `subject` varchar(200) NOT NULL DEFAULT '',
  `content` text,
  `creatorId` int(11) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `bcId` int(11) DEFAULT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `companyId` int(11) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `projectId` (`projectId`),
  KEY `creatorId` (`creatorId`),
  KEY `FK_discussion_company` (`companyId`),
  CONSTRAINT `discussion_ibfk_1` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`),
  CONSTRAINT `discussion_ibfk_2` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_discussion_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1906893 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` mediumtext,
  `creatorId` int(11) NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `companyId` int(11) DEFAULT NULL,
  `isHomePage` tinyint(1) NOT NULL,
  `docType` tinyint(4) DEFAULT '0',
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `projectId` (`projectId`),
  KEY `creatorId` (`creatorId`),
  KEY `FK_document_company` (`companyId`),
  CONSTRAINT `document_ibfk_1` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`),
  CONSTRAINT `document_ibfk_2` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_document_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1012 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `document_history`
--

DROP TABLE IF EXISTS `document_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `documentId` int(11) NOT NULL,
  `version` varchar(64) DEFAULT NULL,
  `updaterId` int(11) NOT NULL,
  `updaterName` varchar(50) NOT NULL,
  `created` datetime NOT NULL,
  `note` tinytext,
  `projectId` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` mediumtext,
  PRIMARY KEY (`id`),
  KEY `FK_document_history_user_idx` (`updaterId`),
  KEY `FK_document_history_document_idx` (`documentId`),
  KEY `FK_document_history_project_idx` (`projectId`)
) ENGINE=MyISAM AUTO_INCREMENT=371864972 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) NOT NULL,
  `summary` varchar(100) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `creatorId` int(11) NOT NULL,
  `startTime` datetime NOT NULL,
  `endTime` datetime NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `companyId` int(11) DEFAULT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `creatorId` (`creatorId`),
  KEY `event_ibfk_1` (`projectId`),
  KEY `FK_event_company` (`companyId`),
  CONSTRAINT `event_ibfk_2` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_event_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `git_user_email`
--

DROP TABLE IF EXISTS `git_user_email`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `git_user_email` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `companyId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `github_info`
--

DROP TABLE IF EXISTS `github_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `github_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `code` varchar(100) DEFAULT NULL,
  `token` varchar(100) DEFAULT NULL,
  `userName` varchar(100) DEFAULT NULL,
  `userEmail` varchar(100) DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  `onboardUserId` int(11) DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `help_tip`
--

DROP TABLE IF EXISTS `help_tip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `help_tip` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `question` text NOT NULL,
  `answer` text NOT NULL,
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invitation`
--

DROP TABLE IF EXISTS `invitation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invitation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `token` varchar(50) NOT NULL DEFAULT '',
  `created` datetime NOT NULL,
  `email` varchar(100) NOT NULL DEFAULT '',
  `companyId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `userId` (`userId`),
  KEY `companyId` (`companyId`),
  CONSTRAINT `invitation_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`id`),
  CONSTRAINT `invitation_ibfk_2` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28426482 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invitation_projects`
--

DROP TABLE IF EXISTS `invitation_projects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `invitation_projects` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `invitationId` int(11) NOT NULL,
  `projectId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `projectId` (`projectId`),
  KEY `invitationId` (`invitationId`),
  CONSTRAINT `invitation_projects_ibfk_1` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`),
  CONSTRAINT `invitation_projects_ibfk_2` FOREIGN KEY (`invitationId`) REFERENCES `invitation` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1412 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `iteration`
--

DROP TABLE IF EXISTS `iteration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iteration` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `status` varchar(45) NOT NULL,
  `startTime` datetime DEFAULT NULL,
  `endTime` datetime DEFAULT NULL,
  `creatorId` int(11) NOT NULL,
  `projectId` int(11) NOT NULL,
  `companyId` int(11) NOT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  `updated` datetime NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=746 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `iteration_object`
--

DROP TABLE IF EXISTS `iteration_object`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iteration_object` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `iterationId` int(11) NOT NULL,
  `objectType` varchar(255) NOT NULL,
  `objectId` int(11) NOT NULL,
  `completed` bit(1) DEFAULT NULL,
  `completedTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=585 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `iteration_story`
--

DROP TABLE IF EXISTS `iteration_story`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iteration_story` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `iterationId` int(11) DEFAULT NULL,
  `storyId` int(11) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `completedTime` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `iteration_todo`
--

DROP TABLE IF EXISTS `iteration_todo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iteration_todo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `iterationId` int(11) NOT NULL,
  `todoId` int(11) NOT NULL,
  `status` varchar(45) NOT NULL,
  `completedTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5451 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `keyword`
--

DROP TABLE IF EXISTS `keyword`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `keyword` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) NOT NULL,
  `companyId` int(11) NOT NULL,
  `keyword` varchar(50) NOT NULL,
  `times` bigint(50) NOT NULL DEFAULT '0',
  `attachType` varchar(30) NOT NULL,
  `attachId` int(11) NOT NULL,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `tfidf` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27296 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `isRead` tinyint(1) NOT NULL,
  `activityId` int(11) NOT NULL,
  `companyId` int(11) NOT NULL,
  `created` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `notification.userId` (`userId`),
  KEY `notification.companyId` (`companyId`),
  KEY `notification.activityId` (`activityId`),
  CONSTRAINT `notification?activityId` FOREIGN KEY (`activityId`) REFERENCES `activity` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `notification?companyId` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `notification?userId` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2029 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `companyId` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `archived` tinyint(1) NOT NULL DEFAULT '0',
  `creatorId` int(11) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `colorId` int(11) NOT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `companyId` (`companyId`),
  KEY `creatorId` (`creatorId`),
  CONSTRAINT `project_ibfk_1` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`),
  CONSTRAINT `project_ibfk_2` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5533076 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_privilege`
--

DROP TABLE IF EXISTS `project_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_privilege` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `userId` int(10) NOT NULL,
  `projectId` int(10) NOT NULL,
  `isAdmin` bit(1) DEFAULT b'0' COMMENT '????????',
  PRIMARY KEY (`id`),
  KEY `projectId` (`projectId`),
  CONSTRAINT `projectId` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1968 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_todo_id`
--

DROP TABLE IF EXISTS `project_todo_id`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_todo_id` (
  `projectId` int(11) NOT NULL,
  `todoId` int(11) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=426 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project_todo_status`
--

DROP TABLE IF EXISTS `project_todo_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project_todo_status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) NOT NULL,
  `status` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=992 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pull_request`
--

DROP TABLE IF EXISTS `pull_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pull_request` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` text,
  `source` varchar(500) NOT NULL,
  `destination` varchar(500) NOT NULL,
  `title` varchar(500) NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0' COMMENT 'declined, merged, open',
  `creatorId` int(11) NOT NULL,
  `todoId` int(11) NOT NULL,
  `creatorName` varchar(50) NOT NULL DEFAULT 'name',
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `projectId` int(11) NOT NULL,
  `scopeId` int(11) NOT NULL DEFAULT '0',
  `sourceHash` varchar(500) NOT NULL,
  `destinationHash` varchar(500) NOT NULL,
  `repositoryId` int(11) NOT NULL,
  `companyId` int(11) NOT NULL DEFAULT '1',
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `creatorId` (`creatorId`),
  KEY `todoId` (`todoId`),
  KEY `projectId` (`projectId`),
  KEY `pf_repository_id` (`repositoryId`),
  KEY `pr_company_id` (`companyId`),
  CONSTRAINT `pf_repository_id` FOREIGN KEY (`repositoryId`) REFERENCES `repository` (`id`),
  CONSTRAINT `pr_company_id` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `pr_creator_id` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`),
  CONSTRAINT `pr_project_id` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`),
  CONSTRAINT `pr_todo_id` FOREIGN KEY (`todoId`) REFERENCES `todo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22106 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pull_request_push`
--

DROP TABLE IF EXISTS `pull_request_push`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pull_request_push` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pullRequestId` int(11) NOT NULL,
  `pushId` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=204 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pull_request_reviewer`
--

DROP TABLE IF EXISTS `pull_request_reviewer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pull_request_reviewer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pullRequestId` int(11) NOT NULL,
  `reviewerId` int(11) NOT NULL,
  `projectId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pullRequestId` (`pullRequestId`),
  KEY `reviewerId` (`reviewerId`),
  KEY `projectId` (`projectId`),
  CONSTRAINT `prr_project_id` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`),
  CONSTRAINT `prr_pull_request_id` FOREIGN KEY (`pullRequestId`) REFERENCES `pull_request` (`id`),
  CONSTRAINT `prr_reviewer_id` FOREIGN KEY (`reviewerId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3303 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `push`
--

DROP TABLE IF EXISTS `push`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `push` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `since` varchar(500) DEFAULT NULL,
  `until` varchar(500) DEFAULT NULL,
  `creatorId` int(11) NOT NULL,
  `creatorName` varchar(50) NOT NULL,
  `repositoryId` int(11) DEFAULT NULL,
  `branchName` varchar(500) DEFAULT NULL,
  `projectId` int(11) DEFAULT NULL,
  `companyId` int(11) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5711 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repo_branch_privilege`
--

DROP TABLE IF EXISTS `repo_branch_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repo_branch_privilege` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `companyId` int(11) NOT NULL,
  `projectId` int(11) NOT NULL,
  `repositoryId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `refName` varchar(100) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repository`
--

DROP TABLE IF EXISTS `repository`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repository` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `slug` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(128) COLLATE utf8_bin NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  `projectId` int(11) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `updatedBranch` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `companyId` int(11) NOT NULL DEFAULT '1',
  `lastCommitId` varchar(128) COLLATE utf8_bin DEFAULT '1',
  `lastCommitShortMessage` varchar(4096) COLLATE utf8_bin DEFAULT NULL,
  `lastCommitUsername` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `lastCommitTimestamp` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_slug_project_id` (`slug`,`projectId`),
  KEY `idx_repository_project_id` (`projectId`),
  KEY `fk_repository_company` (`companyId`),
  CONSTRAINT `fk_repository_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_repository_project` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=222 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repository_privilege`
--

DROP TABLE IF EXISTS `repository_privilege`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repository_privilege` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `companyId` int(11) NOT NULL,
  `projectId` int(11) NOT NULL,
  `repositoryId` int(11) NOT NULL,
  `defaultOwner` int(11) NOT NULL,
  `action` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=234 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repository_privilege_user`
--

DROP TABLE IF EXISTS `repository_privilege_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repository_privilege_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `repositoryRrivilegeId` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `review` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` text,
  `creatorId` int(11) NOT NULL,
  `creatorName` varchar(50) NOT NULL,
  `file` varchar(500) NOT NULL,
  `line` int(11) NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  `projectId` int(11) NOT NULL,
  `pullRequestId` int(11) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `line_type` varchar(50) DEFAULT NULL,
  `since` varchar(500) DEFAULT NULL,
  `until` varchar(500) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `creatorId` (`creatorId`),
  KEY `projectId` (`projectId`),
  KEY `pullRequestId` (`pullRequestId`),
  CONSTRAINT `review_creator_id` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`),
  CONSTRAINT `review_project_id` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`),
  CONSTRAINT `review_pull_request_id` FOREIGN KEY (`pullRequestId`) REFERENCES `pull_request` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29032010 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sshkey`
--

DROP TABLE IF EXISTS `sshkey`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sshkey` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `keytext` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `title` varchar(255) NOT NULL DEFAULT '',
  `md5` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `userId` (`userId`),
  CONSTRAINT `sshkey_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `step`
--

DROP TABLE IF EXISTS `step`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `step` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attachType` varchar(255) NOT NULL,
  `attachId` int(11) NOT NULL,
  `content` varchar(255) NOT NULL,
  `dueDate` datetime DEFAULT NULL,
  `createdTime` datetime DEFAULT NULL,
  `updatedTime` datetime DEFAULT NULL,
  `creatorId` int(11) DEFAULT NULL,
  `creatorName` varchar(255) DEFAULT NULL,
  `assigneeId` int(11) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `startTime` datetime DEFAULT NULL,
  `completedTime` datetime DEFAULT NULL,
  `completerId` int(11) DEFAULT NULL,
  `idInProject` int(11) DEFAULT '0',
  `projectId` int(11) DEFAULT '0',
  `companyId` int(11) DEFAULT '0',
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `story`
--

DROP TABLE IF EXISTS `story`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `story` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) NOT NULL,
  `companyId` int(11) NOT NULL,
  `pre` text,
  `post` text,
  `deleted` tinyint(1) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `completedTime` datetime DEFAULT NULL,
  `completed` tinyint(1) NOT NULL,
  `creatorId` int(11) NOT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `acceptanceLevel` text,
  `priority` int(11) NOT NULL DEFAULT '1',
  `parentStoryId` int(11) NOT NULL DEFAULT '0',
  `completable` tinyint(1) NOT NULL DEFAULT '1',
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=240 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `story_todo`
--

DROP TABLE IF EXISTS `story_todo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `story_todo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `storyId` int(11) NOT NULL,
  `todoId` int(11) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subscriber`
--

DROP TABLE IF EXISTS `subscriber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscriber` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `subscribeId` int(11) NOT NULL,
  `subscribeType` varchar(30) NOT NULL DEFAULT '',
  `companyId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `only_one_subscriber` (`userId`,`subscribeId`,`subscribeType`),
  KEY `userId` (`userId`),
  KEY `FK_subscriber_company` (`companyId`),
  CONSTRAINT `FK_subscriber_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`),
  CONSTRAINT `subscriber_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47835 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `suggestion`
--

DROP TABLE IF EXISTS `suggestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `suggestion` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `content` varchar(2000) DEFAULT NULL,
  `email` varchar(200) DEFAULT NULL,
  `created` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tagname` varchar(50) NOT NULL,
  `projectId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `tag_projectId` (`projectId`),
  CONSTRAINT `tag_projectId` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tag_attach`
--

DROP TABLE IF EXISTS `tag_attach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tag_attach` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tagId` int(11) NOT NULL,
  `attachId` int(11) NOT NULL,
  `attachType` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `tagId` (`tagId`),
  CONSTRAINT `tagId` FOREIGN KEY (`tagId`) REFERENCES `tag` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=198 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `todo`
--

DROP TABLE IF EXISTS `todo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `todo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) NOT NULL DEFAULT '0',
  `todolistId` int(11) DEFAULT NULL,
  `content` varchar(200) NOT NULL,
  `position` double NOT NULL DEFAULT '0',
  `completed` tinyint(1) NOT NULL,
  `dueDate` datetime DEFAULT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `creatorId` int(11) NOT NULL,
  `assigneeId` int(11) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `companyId` int(11) DEFAULT NULL,
  `doing` tinyint(1) NOT NULL DEFAULT '0',
  `projectTodoId` int(11) DEFAULT NULL,
  `todoType` varchar(45) NOT NULL DEFAULT 'task',
  `description` text,
  `estimate` int(11) DEFAULT NULL,
  `spendTime` int(11) DEFAULT NULL,
  `priority` int(11) NOT NULL DEFAULT '1',
  `status` varchar(45) DEFAULT NULL,
  `startTime` datetime DEFAULT NULL,
  `completeTime` datetime DEFAULT NULL,
  `completerId` int(11) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `todoListId` (`todolistId`),
  KEY `creatorId` (`creatorId`),
  KEY `assigneeId` (`assigneeId`),
  KEY `FK_todo_company` (`companyId`),
  KEY `projectId` (`projectId`),
  CONSTRAINT `FK_todo_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`),
  CONSTRAINT `todo_ibfk_1` FOREIGN KEY (`todolistId`) REFERENCES `todolist` (`id`),
  CONSTRAINT `todo_ibfk_2` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`),
  CONSTRAINT `todo_ibfk_3` FOREIGN KEY (`assigneeId`) REFERENCES `user` (`id`),
  CONSTRAINT `todo_ibfk_4` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54331252 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `todo_duration`
--

DROP TABLE IF EXISTS `todo_duration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `todo_duration` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) DEFAULT NULL,
  `companyId` int(11) NOT NULL,
  `todoId` int(11) DEFAULT NULL,
  `creatorId` int(11) NOT NULL,
  `startTime` datetime NOT NULL,
  `endTime` datetime NOT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `todolist`
--

DROP TABLE IF EXISTS `todolist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `todolist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `position` double NOT NULL DEFAULT '0',
  `creatorId` int(11) NOT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `bcId` int(11) DEFAULT NULL,
  `companyId` int(11) DEFAULT NULL,
  `archived` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'todolist归档',
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `projectId` (`projectId`),
  KEY `creatorId` (`creatorId`),
  KEY `FK_todolist_company` (`companyId`),
  CONSTRAINT `FK_todolist_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`),
  CONSTRAINT `todolist_ibfk_1` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`),
  CONSTRAINT `todolist_ibfk_2` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3458942 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `topic`
--

DROP TABLE IF EXISTS `topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) NOT NULL,
  `title` varchar(50) NOT NULL,
  `excerpt` varchar(200) NOT NULL,
  `lastUpdatorId` int(11) NOT NULL,
  `lastUpdatorName` varchar(50) DEFAULT NULL,
  `refId` int(11) NOT NULL,
  `refType` varchar(50) NOT NULL DEFAULT '',
  `deleted` tinyint(1) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `bcId` int(11) DEFAULT NULL,
  `companyId` int(11) DEFAULT NULL,
  `stick` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `projectId` (`projectId`),
  KEY `lastUpdatorId` (`lastUpdatorId`),
  KEY `FK_topic_company` (`companyId`),
  CONSTRAINT `FK_topic_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`),
  CONSTRAINT `topic_ibfk_1` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`),
  CONSTRAINT `topic_ibfk_2` FOREIGN KEY (`lastUpdatorId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4535125 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trash`
--

DROP TABLE IF EXISTS `trash`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trash` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attachType` varchar(100) NOT NULL,
  `attachId` int(11) NOT NULL,
  `companyId` int(11) NOT NULL,
  `projectId` int(11) NOT NULL,
  `deletedTime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `upload`
--

DROP TABLE IF EXISTS `upload`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `upload` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `projectId` int(11) NOT NULL,
  `content` varchar(200) DEFAULT NULL,
  `creatorId` int(11) NOT NULL,
  `deleted` tinyint(1) NOT NULL,
  `created` datetime NOT NULL,
  `updated` datetime NOT NULL,
  `creatorName` varchar(50) DEFAULT NULL,
  `companyId` int(11) DEFAULT NULL,
  `creatorAvatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  PRIMARY KEY (`id`),
  KEY `projectId` (`projectId`),
  KEY `creatorId` (`creatorId`),
  KEY `FK_upload_company` (`companyId`),
  CONSTRAINT `FK_upload_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`),
  CONSTRAINT `upload_ibfk_1` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`),
  CONSTRAINT `upload_ibfk_2` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7443637 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `email` varchar(50) NOT NULL,
  `activated` tinyint(1) NOT NULL,
  `avatar` varchar(200) NOT NULL DEFAULT '/avatar/default.png',
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `password` varchar(50) NOT NULL DEFAULT '',
  `newPassword` varchar(80) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `isManager` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=793998 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_company`
--

DROP TABLE IF EXISTS `user_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_company` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `companyId` int(11) NOT NULL,
  `groupId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userId` (`userId`),
  KEY `companyId` (`companyId`),
  CONSTRAINT `user_company_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`id`),
  CONSTRAINT `user_company_ibfk_2` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1232 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_project`
--

DROP TABLE IF EXISTS `user_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `projectId` int(11) NOT NULL,
  `companyId` int(11) DEFAULT NULL,
  `customOrder` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `userId` (`userId`,`projectId`),
  KEY `projectId` (`projectId`),
  KEY `FK_user_project_company` (`companyId`),
  CONSTRAINT `FK_user_project_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`),
  CONSTRAINT `user_project_ibfk_2` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2909 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `worklog`
--

DROP TABLE IF EXISTS `worklog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `worklog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `todoId` int(11) DEFAULT NULL,
  `projectId` int(11) NOT NULL,
  `startTime` datetime NOT NULL,
  `endTime` datetime NOT NULL,
  `description` text NOT NULL,
  `creatorId` int(11) NOT NULL,
  `companyId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `worklog_ibfk_1` (`todoId`),
  KEY `worklog_ibfk_2` (`projectId`),
  KEY `worklog_ibfk_3` (`creatorId`),
  KEY `FK_worklog_company` (`companyId`),
  CONSTRAINT `FK_worklog_company` FOREIGN KEY (`companyId`) REFERENCES `company` (`id`),
  CONSTRAINT `worklog_ibfk_1` FOREIGN KEY (`todoId`) REFERENCES `todo` (`id`),
  CONSTRAINT `worklog_ibfk_2` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`),
  CONSTRAINT `worklog_ibfk_3` FOREIGN KEY (`creatorId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-09-17 13:02:10
