DROP TABLE IF EXISTS RESERVATIONS;

CREATE TABLE RESERVATIONS
(
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    campsite_id         VARCHAR(250) NOT NULL,
    username            VARCHAR(250) NOT NULL,
    user_email          VARCHAR(250) NOT NULL,
    start_date          DATE NOT NULL,
    end_date            DATE NOT NULL,
    created_on          DATE NOT NULL,
    updated_on          DATE
);
