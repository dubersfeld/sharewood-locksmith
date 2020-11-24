-- CREATE DATABASE sharewood;

-- photos only in this database

\c sharewood

DROP TABLE IF EXISTS photo;

CREATE TABLE photo(
    id        	serial PRIMARY KEY,
    title  	VARCHAR(20) NULL,
    username    VARCHAR(20) NOT NULL,
    shared	BOOLEAN NOT NULL
);

DROP TABLE IF EXISTS sharewooduser;

CREATE TABLE sharewooduser(
    id        	serial PRIMARY KEY,
    username  	VARCHAR(20) NOT NULL,
    email       VARCHAR(40) NOT NULL,
    firstname	VARCHAR(20) NOT NULL,
    lastname    VARCHAR(20) NOT NULL,
    roles       VARCHAR(50) NOT NULL
);


INSERT INTO photo (title, username, shared) VALUES (
  'photo1', 'alice', false
);

INSERT INTO photo (title, username, shared) VALUES (
  'photo2', 'carol', false
);

INSERT INTO photo (title, username, shared) VALUES (
  'photo3', 'alice', false
);

INSERT INTO photo (title, username, shared) VALUES (
  'photo4', 'carol', false
);

INSERT INTO photo (title, username, shared) VALUES (
  'photo5', 'alice', false
);

INSERT INTO photo (title, username, shared) VALUES (
  'photo6', 'carol', false
);


-- Populate sharewooduser table

INSERT INTO sharewooduser (username, email, firstname, lastname, roles) VALUES (
  'alice', 'alice.cooper@sharewood.com', 'Alice', 'Cooper', 'SHAREWOOD_USER'
);

INSERT INTO sharewooduser (username, email, firstname, lastname, roles) VALUES (
  'carol', 'carol.baker@sharewood.com', 'Carol', 'Baker', 'SHAREWOOD_USER,SHAREWOOD_CURATOR'
);






