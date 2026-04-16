CREATE SCHEMA project;
USE project;

-- Vehicles table
CREATE TABLE Vehicles (
  VIN VARCHAR(17) NOT NULL,
  LicensePlateNumber VARCHAR(10),
  Year INT NOT NULL,
  Make VARCHAR(50),
  Model VARCHAR(50),
  Color VARCHAR(50),
  RegistrationStatus BOOLEAN,
  StolenStatus BOOLEAN,
  WarrantStatus BOOLEAN,
  Notes TEXT,
  PRIMARY KEY (VIN)
);

-- Drivers table
CREATE TABLE Drivers (
  DLNumber VARCHAR(20) NOT NULL,
  FirstName VARCHAR(50),
  LastName VARCHAR(50),
  DateOfBirth DATE,
  LicenseStatus VARCHAR(20),
  WarrantStatus BOOLEAN,
  DrivingRecord TEXT,
  Notes TEXT,
  PRIMARY KEY (DLNumber)
);

-- Citations table
CREATE TABLE Citations (
  CitationID INT NOT NULL AUTO_INCREMENT,
  Officer VARCHAR(50),
  Type VARCHAR(50),
  DLNumber VARCHAR(20),
  VIN VARCHAR(17),
  Date DATETIME,
  FineAmount REAL,
  PaymentStatus VARCHAR(20),
  TrafficSchool TEXT,
  Notes TEXT,
  PRIMARY KEY (CitationID),
  FOREIGN KEY (DLNumber) REFERENCES Drivers(DLNumber),
  FOREIGN KEY (VIN) REFERENCES Vehicles(VIN)
);

-- Users table
CREATE TABLE Users (
  Username VARCHAR(50) NOT NULL,
  Password VARCHAR(50),
  Type VARCHAR(20),
  Status VARCHAR(20),
  PRIMARY KEY (Username)
);
