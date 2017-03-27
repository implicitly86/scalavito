# --- !Ups

CREATE TABLE IF NOT EXISTS "user" (
  "id"        BIGSERIAL NOT NULL PRIMARY KEY,
  "firstName" VARCHAR   NOT NULL,
  "lastName"  VARCHAR   NOT NULL,
  "email"     VARCHAR   NOT NULL
);
CREATE TABLE IF NOT EXISTS "logininfo" (
  "id"          BIGSERIAL NOT NULL PRIMARY KEY,
  "providerID"  VARCHAR   NOT NULL,
  "providerKey" VARCHAR   NOT NULL
);
CREATE TABLE IF NOT EXISTS "userlogininfo" (
  "userID"      BIGINT NOT NULL,
  "loginInfoId" BIGINT NOT NULL
);
CREATE TABLE IF NOT EXISTS "passwordinfo" (
  "hasher"      VARCHAR NOT NULL,
  "password"    VARCHAR NOT NULL,
  "salt"        VARCHAR,
  "loginInfoId" BIGINT  NOT NULL
);
CREATE TABLE IF NOT EXISTS "adverts" (
  "id"          BIGSERIAL NOT NULL PRIMARY KEY,
  "authorID"    BIGINT    NOT NULL,
  "title"       VARCHAR   NOT NULL,
  "description" VARCHAR   NOT NULL
);


# --- !Downs

DROP TABLE "adverts";
DROP TABLE "passwordinfo";
DROP TABLE "userlogininfo";
DROP TABLE "logininfo";
DROP TABLE "user";