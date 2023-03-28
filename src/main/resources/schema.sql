
CREATE TABLE IF NOT EXISTS PUBLIC.RATING (
	ID                 INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
	NAME               CHARACTER VARYING (255)  NOT NULL,
	CONSTRAINT RATING_PK PRIMARY KEY ( ID ),
	CONSTRAINT RATING_UNIQUE UNIQUE ( NAME )
 );

CREATE TABLE IF NOT EXISTS PUBLIC.FILM (
	ID                 INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
	NAME               CHARACTER VARYING (255)  NOT NULL,
	DESCRIPTION        CHARACTER VARYING (255)  NOT NULL,
	RELEASE_DATE       DATE   NOT NULL,
	DURATION           INTEGER   NOT NULL,
	LIKE_QUANTITY      INTEGER   NOT NULL,
	RATING_ID          INTEGER   ,
	CONSTRAINT FILM_PKEY PRIMARY KEY ( ID )
 );

CREATE INDEX IF NOT EXISTS FKI_FILM_RATING_ID_FKEY ON PUBLIC.FILM ( RATING_ID );

ALTER TABLE PUBLIC.FILM ADD CONSTRAINT IF NOT EXISTS fk_film_rating FOREIGN KEY ( RATING_ID ) REFERENCES PUBLIC.RATING( ID ) ON DELETE SET NULL ON UPDATE CASCADE;

CREATE TABLE IF NOT EXISTS PUBLIC.GENRE (
	ID                 INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
	NAME               CHARACTER VARYING (255)  NOT NULL,
	CONSTRAINT GENRE_PK PRIMARY KEY ( ID ),
	CONSTRAINT GENRE_UNIQUE UNIQUE ( NAME )
 );

CREATE TABLE IF NOT EXISTS PUBLIC.FILM_X_GENRE (
	FILM_ID            INTEGER   NOT NULL,
	GENRE_ID           INTEGER   NOT NULL,
	CONSTRAINT FILM_X_GENRE_PK PRIMARY KEY ( FILM_ID, GENRE_ID )
 );

CREATE INDEX IF NOT EXISTS idx_film_x_genre_genre_id ON PUBLIC.FILM_X_GENRE ( GENRE_ID );

CREATE INDEX IF NOT EXISTS idx_film_x_genre_film_id ON PUBLIC.FILM_X_GENRE ( FILM_ID );

ALTER TABLE PUBLIC.FILM_X_GENRE ADD CONSTRAINT IF NOT EXISTS fk_film_x_genre_genre FOREIGN KEY ( GENRE_ID ) REFERENCES PUBLIC.GENRE( ID ) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE PUBLIC.FILM_X_GENRE ADD CONSTRAINT IF NOT EXISTS fk_film_x_genre_film FOREIGN KEY ( FILM_ID ) REFERENCES PUBLIC.FILM( ID ) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE IF NOT EXISTS PUBLIC.USERS (
	ID                   INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL,
	EMAIL                CHARACTER VARYING (255)  NOT NULL,
	LOGIN                CHARACTER VARYING (255)  NOT NULL,
	NAME                 CHARACTER VARYING (255)  NOT NULL,
	BIRTHDAY             DATE   NOT NULL,
	CONSTRAINT pk_user PRIMARY KEY ( ID )
 );

CREATE TABLE IF NOT EXISTS PUBLIC.LIKES (
	FILM_ID            INTEGER   NOT NULL,
	USER_ID            INTEGER   NOT NULL,
	CONSTRAINT LIKES_PK PRIMARY KEY ( FILM_ID, USER_ID )
 );

CREATE INDEX IF NOT EXISTS idx_likes_film_id ON PUBLIC.LIKES ( FILM_ID );

CREATE INDEX IF NOT EXISTS idx_likes_user_id ON PUBLIC.LIKES ( USER_ID );

ALTER TABLE PUBLIC.LIKES ADD CONSTRAINT IF NOT EXISTS fk_likes_film FOREIGN KEY ( FILM_ID ) REFERENCES PUBLIC.FILM( ID ) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE PUBLIC.LIKES ADD CONSTRAINT IF NOT EXISTS fk_likes_user FOREIGN KEY ( USER_ID ) REFERENCES PUBLIC.USERS( ID ) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE IF NOT EXISTS PUBLIC.FRIENDS (
	USER_ID_1            INTEGER   NOT NULL,
	USER_ID_2            INTEGER   NOT NULL,
	CONSTRAINT pk_friends PRIMARY KEY ( USER_ID_1, USER_ID_2 )
 );

CREATE INDEX IF NOT EXISTS idx_friends_user_id_2 ON PUBLIC.FRIENDS ( USER_ID_2 );

CREATE INDEX IF NOT EXISTS idx_friends_user_id_1 ON PUBLIC.FRIENDS ( USER_ID_1 );

ALTER TABLE PUBLIC.FRIENDS ADD CONSTRAINT IF NOT EXISTS fk_friends_user FOREIGN KEY ( USER_ID_1 ) REFERENCES PUBLIC.USERS( ID ) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE PUBLIC.FRIENDS ADD CONSTRAINT IF NOT EXISTS fk_friends_user_0 FOREIGN KEY ( USER_ID_2 ) REFERENCES PUBLIC.USERS( ID ) ON DELETE CASCADE ON UPDATE CASCADE;








