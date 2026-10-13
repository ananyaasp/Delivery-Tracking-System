DELETE FROM status_log;
DELETE FROM shipment;
DELETE FROM delivery_agent;

INSERT INTO delivery_agent (name, phone, available, version) VALUES ('Rajesh Kumar', '9876543210', true, 0);
INSERT INTO delivery_agent (name, phone, available, version) VALUES ('Priya Sharma', '8765432109', true, 0);
INSERT INTO delivery_agent (name, phone, available, version) VALUES ('Amit Singh', '7654321098', true, 0);
INSERT INTO delivery_agent (name, phone, available, version) VALUES ('Sneha Patel', '6543210987', true, 0);
INSERT INTO delivery_agent (name, phone, available, version) VALUES ('Vikram Joshi', '5432109876', true, 0);