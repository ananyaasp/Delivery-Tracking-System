DELETE FROM status_log;
DELETE FROM shipment;
DELETE FROM delivery_agent;

INSERT INTO delivery_agent (name, phone, available) VALUES ('Rajesh Kumar', '9876543210', true);
INSERT INTO delivery_agent (name, phone, available) VALUES ('Priya Sharma', '8765432109', true);
INSERT INTO delivery_agent (name, phone, available) VALUES ('Amit Singh', '7654321098', true);
INSERT INTO delivery_agent (name, phone, available) VALUES ('Sneha Patel', '6543210987', true);
INSERT INTO delivery_agent (name, phone, available) VALUES ('Vikram Joshi', '5432109876', true);