-- initializing tables
CREATE TABLE aircrafts (
    aircraft_id VARCHAR(10) PRIMARY KEY NOT NULL,
    aircraft_model VARCHAR(25) NOT NULL,
    max_capacity INT NOT NULL
);

CREATE TABLE airports (
    airport_id INT PRIMARY KEY NOT NULL,
    name VARCHAR(25) NOT NULL,
    country_name VARCHAR(25) NOT NULL,
    company_id INT NOT NULL
);

CREATE TABLE bookings (
    booking_id INT PRIMARY KEY NOT NULL,
    passenger_id INT NOT NULL,
    flight_id VARCHAR(5) NOT NULL,
    airport_id INT NOT NULL,
    booking_date DATETIME NOT NULL,
    booking_status ENUM('Paid', 'Refunded', 'Pending', 'Rescheduled', 'Completed') NOT NULL
);

CREATE TABLE companies (
    company_id INT PRIMARY KEY NOT NULL,
    name VARCHAR(25) NOT NULL,
    date_founded DATE NOT NULL,
    contact_number BIGINT NOT NULL
);

CREATE TABLE event_types (
    event_type_id INT PRIMARY KEY NOT NULL,
    event_type_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE flights (
    flight_id VARCHAR(5) PRIMARY KEY NOT NULL,
    expected_departure_time DATETIME NOT NULL,
    expected_arrival_time DATETIME NOT NULL,
    actual_departure_time DATETIME,
    actual_arrival_time DATETIME,
    aircraft_id VARCHAR(10) NOT NULL,
    origin_airport_id INT NOT NULL,
    dest_airport_id INT NOT NULL,
    flight_status ENUM('Scheduled', 'Delayed', 'Cancelled', 'On Air', 'Arrived') NOT NULL,
    seating_capacity INT NOT NULL
);

CREATE TABLE flight_logs (
    log_id INT PRIMARY KEY NOT NULL,
    flight_id VARCHAR(5) NOT NULL,
    log_date DATETIME NOT NULL,
    event_type_id INT NOT NULL
);

CREATE TABLE passengers (
    passenger_id INT PRIMARY KEY NOT NULL,
    passport_id INT NOT NULL,
    contact_number BIGINT NOT NULL,
    email_address VARCHAR(25) NOT NULL
);

CREATE TABLE passports (
    passport_id INT PRIMARY KEY NOT NULL,
    first_name VARCHAR(25) NOT NULL,
    middle_name VARCHAR(25),
    last_name VARCHAR(25) NOT NULL,
    date_of_birth DATE NOT NULL,
    sex ENUM('Male', 'Female', 'Other') NOT NULL,
    nationality VARCHAR(25) NOT NULL,
    place_of_issue VARCHAR(25) NOT NULL,
    issue_date DATE NOT NULL,
    expiration_date DATE NOT NULL
);

CREATE TABLE tickets (
    passenger_id INT NOT NULL,
    booking_id INT NOT NULL,
    seat_number VARCHAR(5) NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

-- foreign keys
ALTER TABLE airports
    ADD FOREIGN KEY (company_id) REFERENCES companies(company_id) ON DELETE CASCADE;

ALTER TABLE passengers
    ADD FOREIGN KEY (passport_id) REFERENCES passports(passport_id) ON DELETE CASCADE;

ALTER TABLE bookings
    ADD FOREIGN KEY (passenger_id) REFERENCES passengers(passenger_id) ON DELETE CASCADE,
    ADD FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE CASCADE,
    ADD FOREIGN KEY (airport_id) REFERENCES airports(airport_id) ON DELETE CASCADE;

ALTER TABLE flights
    ADD FOREIGN KEY (aircraft_id) REFERENCES aircrafts(aircraft_id) ON DELETE CASCADE,
    ADD FOREIGN KEY (origin_airport_id) REFERENCES airports(airport_id) ON DELETE CASCADE,
    ADD FOREIGN KEY (dest_airport_id) REFERENCES airports(airport_id) ON DELETE CASCADE;

ALTER TABLE tickets
    ADD FOREIGN KEY (passenger_id) REFERENCES passengers(passenger_id) ON DELETE CASCADE,
    ADD FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE;

ALTER TABLE flight_logs
    ADD FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE CASCADE,
    ADD FOREIGN KEY (event_type_id) REFERENCES event_types(event_type_id) ON DELETE CASCADE;
