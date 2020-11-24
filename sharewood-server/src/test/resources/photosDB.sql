-- CREATE DATABASE sharewood;

DROP TABLE IF EXISTS photo;

CREATE TABLE photo(
    id        	serial PRIMARY KEY,
    title  	VARCHAR(20) NULL,
    username    VARCHAR(20) NOT NULL,
    shared	BOOLEAN NOT NULL
);


INSERT INTO photo (title, username, shared) VALUES (
  'photo1', 'alice', false
);

INSERT INTO photo (title, username, shared) VALUES (
  'photo2', 'carol', true
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





