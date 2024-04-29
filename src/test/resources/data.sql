CREATE TABLE users
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    email         VARCHAR(100),
    date_of_birth DATE,
    address       VARCHAR(255),
    phone_number  VARCHAR(15)
);

INSERT INTO users (first_name, last_name, email, date_of_birth, address, phone_number)
VALUES ('John', 'Doe', 'john.doe@example.com', '1990-01-01', '123 Main Street', '555-1234'),
       ('Jane', 'Smith', 'jane.smith@example.com', '1995-02-15', '456 Elm Street', '555-5678');
