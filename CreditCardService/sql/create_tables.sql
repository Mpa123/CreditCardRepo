DROP table client;
DROP table credit_card_status_type;

CREATE TABLE credit_card_status_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL CHECK (name IN ('Approved', 'Rejected', 'Pending'))
);

CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	OIB VARCHAR(50) UNIQUE NOT NULL,
	credit_card_status_type_id INT REFERENCES credit_card_status_type(id)
);

