-- initializing tables
CREATE TABLE aircraft (
    aircraft_id VARCHAR(10) PRIMARY KEY,
    aircraft_model VARCHAR(25),
    max_capacity INT
);

CREATE TABLE airport (
    airport_id INT PRIMARY KEY,
    name VARCHAR(25),
    country_name VARCHAR(25),
    company_id INT
);

CREATE TABLE booking (
    booking_id INT PRIMARY KEY,
    passenger_id INT,
    ticket_id INT,
    flight_id VARCHAR(5),
    airport_id INT,
    booking_date DATETIME,
    booking_status ENUM('Paid', 'Refunded', 'Pending', 'Rescheduled', 'Completed')
);

CREATE TABLE company (
    company_id INT PRIMARY KEY,
    name VARCHAR(25),
    date_founded DATE
);

CREATE TABLE flight (
    flight_id VARCHAR(5) PRIMARY KEY,
    expected_departure_time DATETIME,
    expected_arrival_time DATETIME,
    actual_departure_time DATETIME,
    actual_arrival_time DATETIME,
    aircraft_id VARCHAR(10),
    origin_airport_id INT,
    dest_airport_id INT,
    flight_status ENUM('Scheduled', 'Delayed', 'Cancelled', 'On Air', 'Arrived'),
    seating_capacity INT
);

CREATE TABLE flight_log (
    log_id INT PRIMARY KEY,
    flight_id VARCHAR(5),
    log_date DATETIME,
    event_type ENUM('Departure', 'Arrival', 'Delay', 'Reschedule', 'Maintenance', 'Weather')
);

CREATE TABLE passenger (
    passenger_id INT PRIMARY KEY,
    passport_id INT,
    contact_number BIGINT,
    email_address VARCHAR(25)
);

CREATE TABLE passport (
    passport_id INT PRIMARY KEY,
    first_name VARCHAR(25),
    middle_name VARCHAR(25),
    last_name VARCHAR(25),
    date_of_birth DATETIME,
    sex ENUM('Male', 'Female', 'Other'),
    nationality VARCHAR(25)
);

CREATE TABLE ticket (
	ticket_id INT PRIMARY KEY,
    passenger_id INT,
    booking_id INT,
    seat_number VARCHAR(5),
    price DECIMAL(10,2)
);

-- Adding foreign key references at the end
-- NOTE!! -> circular reference for booking and ticket
-- eiterh remove booking_id from tickets or remove ticket_id from booking
ALTER TABLE airport
    ADD FOREIGN KEY (company_id) REFERENCES company(company_id);

ALTER TABLE passenger
    ADD FOREIGN KEY (passport_id) REFERENCES passport(passport_id);

ALTER TABLE booking
    ADD FOREIGN KEY (passenger_id) REFERENCES passenger(passenger_id),
    ADD FOREIGN KEY (ticket_id) REFERENCES ticket(ticket_id),
    ADD FOREIGN KEY (flight_id) REFERENCES flight(flight_id),
    ADD FOREIGN KEY (airport_id) REFERENCES airport(airport_id);

ALTER TABLE flight
    ADD FOREIGN KEY (aircraft_id) REFERENCES aircraft(aircraft_id),
    ADD FOREIGN KEY (origin_airport_id) REFERENCES airport(airport_id),
    ADD FOREIGN KEY (dest_airport_id) REFERENCES airport(airport_id);

ALTER TABLE ticket
    ADD FOREIGN KEY (passenger_id) REFERENCES passenger(passenger_id),
    ADD FOREIGN KEY (booking_id) REFERENCES booking(booking_id);

ALTER TABLE flight_log
    ADD FOREIGN KEY (flight_id) REFERENCES flight(flight_id);
    
-- disabling foreign key checks to be able to enter data without references to other tables
SET FOREIGN_KEY_CHECKS = 0;

-- enabling foreign key checks in order to ensure integrity
SET FOREIGN_KEY_CHECKS = 1;
