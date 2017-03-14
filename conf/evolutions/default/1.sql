# --- !Ups

CREATE TABLE "user" (
  "userID"    VARCHAR NOT NULL PRIMARY KEY,
  "firstName" VARCHAR,
  "lastName"  VARCHAR,
  "email"     VARCHAR
);
CREATE TABLE "logininfo" (
  "id"          BIGSERIAL NOT NULL PRIMARY KEY,
  "providerID"  VARCHAR   NOT NULL,
  "providerKey" VARCHAR   NOT NULL
);
CREATE TABLE "userlogininfo" (
  "userID"      VARCHAR NOT NULL,
  "loginInfoId" BIGINT  NOT NULL
);
CREATE TABLE "passwordinfo" (
  "hasher"      VARCHAR NOT NULL,
  "password"    VARCHAR NOT NULL,
  "salt"        VARCHAR,
  "loginInfoId" BIGINT  NOT NULL
);


# --- !Downs

DROP TABLE "passwordinfo";
DROP TABLE "userlogininfo";
DROP TABLE "logininfo";
DROP TABLE "user";