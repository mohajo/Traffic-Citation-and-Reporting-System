-- Sample insert data
USE project; 

INSERT INTO Users (Username, Password, Type, Status)
VALUES 
    ('admin', 'password', 'local', 'active'),
    ('john', 'password', 'local', 'active'),
    ('jane', 'password', 'local', 'inactive'),
    ('jim', 'password', 'provincial', 'active'),
    ('jack', 'password', 'provincial', 'inactive');

INSERT INTO Vehicles (VIN, LicensePlateNumber, Year, Make, Model, Color, RegistrationStatus, StolenStatus, WarrantStatus, Notes) VALUES 
('1GNSKAKC9FR288113', 'ABC123', 2013, 'Chevrolet', 'Tahoe', "red", true, false, false, 'Red SUV'), 
('1FMJU2A57CEF33227', 'XYZ789', 2005, 'Ford', 'Expedition', "white", true, false, false, 'White SUV'), 
('1N4AA6AP3HC387645', 'DEF456', 2010, 'Nissan', 'Maxima', "silver", true, false, false, 'Silver sedan'), 
('2T2HK31U48C065630', 'GHI789', 2014, 'Lexus', 'RX', "blue", true, false, false, 'Blue SUV'), 
('3FA6P0D92JR202122', 'JKL012', 2010, 'Ford', 'Fusion', " black", true, false, false, 'Black sedan');

INSERT INTO Drivers (DLNumber, FirstName, LastName, DateOfBirth, LicenseStatus, WarrantStatus, DrivingRecord, Notes) VALUES 
('1324-4312-1243-4312', 'John', 'Doe', '1980-01-01', 'Active', false, 'Clean', 'No notes'), 
('6356-6543-6453-6543', 'Jane', 'Doe', '1990-01-01', 'Not Active', false, 'Clean', 'No notes'), 
('4321-5133-5432-5432', 'Bob', 'Smith', '1985-01-01', 'Suspended', false, 'Clean', 'No notes'), 
('5432-5432-7457-6345', 'Alice', 'Johnson', '1975-01-01', 'Active', false, 'Clean', 'No notes'), 
('1244-5123-6543-6543', 'Tom', 'Williams', '1995-01-01', 'Active', false, 'Clean', 'No notes');

INSERT INTO Citations (Officer, Type, DLNumber, VIN, Date, FineAmount, PaymentStatus, TrafficSchool, Notes) VALUES
('John Smith', 'Speeding', '1244-5123-6543-6543', '1GNSKAKC9FR288113', '2022-01-15 09:30:00', 150.00, 'Paid', 'N/A', 'Driver was going 75 mph in a 55 mph zone.'),
('Jane Doe', 'Seat Belt Violation', '5432-5432-7457-6345', '1FMJU2A57CEF33227', '2022-02-03 11:15:00', 50.00, 'Unpaid', 'N/A', 'Driver was not wearing a seat belt.'),
('Mike Johnson', 'Running Red Light', '4321-5133-5432-5432', '1N4AA6AP3HC387645', '2022-03-22 18:45:00', 200.00, 'Unpaid', 'Attended', 'Driver ran a red light and almost caused an accident.'),
('Sarah Lee', 'Expired Registration', '6356-6543-6453-6543', '2T2HK31U48C065630', '2022-04-11 14:00:00', 75.00, 'Paid', 'N/A', 'Vehicle had expired registration tags.'),
('David Kim', 'No Proof of Insurance', '4321-5133-5432-5432', '3FA6P0D92JR202122', '2022-05-19 10:30:00', 100.00, 'Unpaid', 'N/A', 'Driver was unable to provide proof of insurance.');