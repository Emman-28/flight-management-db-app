SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO aircrafts (aircraft_id, aircraft_model, max_capacity)
VALUES
    ('S320', 'Shocky 320', 180),
    ('D737', 'Dendro 737', 160),
    ('E190', 'Ember 190', 100),
    ('A350', 'Airbus A350', 300),
    ('T787', 'Titanic 787', 250);

INSERT INTO airports (airport_id, name, country_name, company_id)
VALUES
    (1, 'Hi High International', 'USA', 1),
    (2, 'Blue Skies Airport', 'United Kingdom', 2),
    (3, 'Aeroblast Terminal', 'Singapore', 3),
    (4, 'CloudNine Hub', 'Japan', 4),
    (5, 'On Wings International', 'UAE', 5);

INSERT INTO bookings (booking_id, passenger_id, flight_id, airport_id, booking_date, booking_status)
VALUES
    (101, 1, 'F001', 1, '2024-11-15 08:00:00', 'Paid'),
    (102, 2, 'F002', 2, '2024-11-15 09:30:00', 'Pending'),
    (103, 3, 'F003', 3, '2024-11-15 10:30:00', 'Refunded'),
    (104, 4, 'F004', 4, '2024-11-15 12:00:00', 'Paid'),
    (105, 5, 'F005', 5, '2024-11-15 13:30:00', 'Paid'),
    (106, 3, 'F003', 3, '2024-11-16 10:30:00', 'Paid'),
    (107, 4, 'F004', 4, '2024-11-16 12:00:00', 'Pending'),
    (108, 5, 'F005', 5, '2024-11-16 13:30:00', 'Refunded'),
    (109, 1, 'F001', 1, '2024-11-16 08:00:00', 'Paid'),
    (110, 2, 'F002', 2, '2024-11-16 09:30:00', 'Paid');

INSERT INTO companies (company_id, name, date_founded, contact_number)
VALUES
    (1, 'Hi High Airlines', '2005-07-15', 1234567890),
    (2, 'Mr. Blue Skies Travel', '2010-04-22', 9876543210),
    (3, 'Aeroblast Express', '2018-11-03', 1928374650),
    (4, 'CloudNine Aviation', '2022-09-18', 1122334455),
    (5, 'On The Wings Airlines', '2012-09-10', 9988776655);

INSERT INTO event_types (event_type_id, event_type_name)
VALUES
    (1, 'Departure'),
    (2, 'Arrival'),
    (3, 'Delay'),
    (4, 'Cancellation'),
    (5, 'Change in Departure Time'),
    (6, 'Technical Maintenance'),
    (7, 'Weather Disruption'),
	(8, 'Rescheduling'),
    (9, 'Destination Diversion'),
    (10, 'Medical Emergency');

INSERT INTO flight_logs (log_id, flight_id, log_date, event_type_id)
VALUES
    (1, 'F001', '2024-11-21 08:00:00', 1),
    (2, 'F002', '2024-11-21 09:30:00', 1),
    (3, 'F003', '2024-11-21 10:30:00', 3),
    (4, 'F004', '2024-11-21 12:00:00', 4),
    (5, 'F005', '2024-11-21 13:30:00', 2),
    (6, 'F001', '2024-11-21 10:05:00', 2),
    (7, 'F002', '2024-11-21 11:05:00', 2),
    (8, 'F003', '2024-11-21 12:40:00', 2),
    (9, 'F004', '2024-11-21 14:20:00', 5),
    (10, 'F005', '2024-11-21 15:15:00', 2);

INSERT INTO flights (flight_id, expected_departure_time, expected_arrival_time, actual_departure_time, actual_arrival_time, aircraft_id, origin_airport_id, dest_airport_id, flight_status, seating_capacity)
VALUES
    ('F001', '2024-11-21 08:00:00', '2024-11-21 10:00:00', '2024-11-21 08:10:00', '2024-11-21 10:05:00', 'S320', 1, 2, 'Scheduled', 180),
    ('F002', '2024-11-21 09:30:00', '2024-11-21 11:00:00', '2024-11-21 09:45:00', '2024-11-21 11:05:00', 'D737', 2, 3, 'On Air', 160),
    ('F003', '2024-11-21 10:30:00', '2024-11-21 12:30:00', '2024-11-21 10:35:00', '2024-11-21 12:40:00', 'E190', 3, 4, 'Delayed', 100),
    ('F004', '2024-11-21 12:00:00', '2024-11-21 14:00:00', '2024-11-21 12:10:00', '2024-11-21 14:20:00', 'A350', 4, 5, 'Cancelled', 300),
    ('F005', '2024-11-21 13:30:00', '2024-11-21 15:00:00', '2024-11-21 13:40:00', '2024-11-21 15:15:00', 'T787', 5, 1, 'Arrived', 250);

INSERT INTO passengers (passenger_id, passport_id, contact_number, email_address)
VALUES
    (1, 123456789, 9876543210, 'john.doe@gmail.com'),
    (2, 987654321, 8765432109, 'jane.smith@yahoo.com'),
    (3, 456789123, 7654321098, 'alice@wonderland.com'),
    (4, 654321987, 6543210987, 'bob.builder@gmail.com'),
    (5, 321987654, 5432109876, 'charlie.brown@hotmail.com');

INSERT INTO passports (passport_id, first_name, middle_name, last_name, date_of_birth, sex, nationality, place_of_issue, issue_date, expiration_date)
VALUES
    (123456789, 'John', 'Michael', 'Doe', '1990-05-15', 'Male', 'American', 'USA', '2015-05-15', '2025-05-15'),
    (987654321, 'Jane', 'Elizabeth', 'Smith', '1985-03-10', 'Female', 'British', 'UK', '2010-03-10', '2025-03-10'),
    (456789123, 'Alice', NULL, 'Wonderland', '1992-07-25', 'Female', 'Canadian', 'Canada', '2015-07-25', '2025-07-25'),
    (654321987, 'Bob', 'Alexander', 'Builder', '1988-09-17', 'Male', 'Australian', 'Australia', '2015-09-17', '2025-09-17'),
    (321987654, 'Charlie', NULL, 'Brown', '1995-11-30', 'Other', 'Irish', 'Ireland', '2015-11-30', '2025-11-30');

INSERT INTO tickets (passenger_id, booking_id, seat_number, price)
VALUES
    (1, 101, 'A1', 150.00),
    (2, 102, 'A2', 200.00),
    (3, 103, 'B1', 180.00),
    (4, 104, 'B2', 220.00),
    (5, 105, 'C1', 250.00);
    
SET FOREIGN_KEY_CHECKS = 1;