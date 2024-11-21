SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM aircrafts;
DELETE FROM airports;
DELETE FROM companies;
DELETE FROM event_types;
DELETE FROM passengers;
DELETE FROM passports;
DELETE FROM flights;
DELETE FROM bookings;
DELETE FROM tickets;
DELETE FROM flight_logs;

SET FOREIGN_KEY_CHECKS = 1;