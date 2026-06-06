-- STEP 1: DATABASE
CREATE DATABASE HRMS_DB;
USE HRMS_DB;
-- STEP 2: CREATE TABLES
-- Departments
CREATE TABLE Departments (
    Dept_ID      INT          PRIMARY KEY,
    Dept_Name    VARCHAR(100) NOT NULL,
    Manager_Name VARCHAR(100)
);
-- Employees
CREATE TABLE Employees (
    Emp_ID       INT          PRIMARY KEY IDENTITY(1,1),
    Emp_Name     VARCHAR(100) NOT NULL,
    Dept_ID      INT          NOT NULL,
    Joining_Date DATE         DEFAULT GETDATE(),
    CONSTRAINT fk_Emp_Dept
        FOREIGN KEY (Dept_ID) REFERENCES Departments(Dept_ID)
        ON DELETE CASCADE
);
GO
-- Salaries
CREATE TABLE Salaries (
    Salary_ID    INT             PRIMARY KEY IDENTITY(1,1),
    Emp_ID       INT             NOT NULL UNIQUE,   -- one salary per employee
    Basic_Salary DECIMAL(10,2)   NOT NULL,
    Bonus        DECIMAL(10,2)   DEFAULT 0,
    CONSTRAINT chk_Salary
        CHECK (Basic_Salary > 0),
    CONSTRAINT fk_Sal_Emp
        FOREIGN KEY (Emp_ID) REFERENCES Employees(Emp_ID)
        ON DELETE CASCADE
);
CREATE TABLE Attendance (
    Att_ID   INT         PRIMARY KEY IDENTITY(1,1),
    Emp_ID   INT         NOT NULL,
    Att_Date DATE        DEFAULT GETDATE(),
    Status   VARCHAR(10) NOT NULL,
    CONSTRAINT chk_Att_Status
        CHECK (Status IN ('Present', 'Absent', 'Leave')),
    CONSTRAINT fk_Att_Emp
        FOREIGN KEY (Emp_ID) REFERENCES Employees(Emp_ID)
        ON DELETE CASCADE
);
-- Leave Management
CREATE TABLE Leave_Mgmt (
    Leave_ID   INT         PRIMARY KEY IDENTITY(1,1),
    Emp_ID     INT         NOT NULL,
    Leave_Type VARCHAR(20) NOT NULL,
    Start_Date DATE        NOT NULL,
    End_Date   DATE        NOT NULL,
    Status     VARCHAR(20) DEFAULT 'Pending',
    CONSTRAINT chk_Leave_Type
        CHECK (Leave_Type IN ('Annual', 'Sick', 'Casual')),
    CONSTRAINT chk_Leave_Status
        CHECK (Status IN ('Pending', 'Approved', 'Rejected')),
    CONSTRAINT fk_Leave_Emp
        FOREIGN KEY (Emp_ID) REFERENCES Employees(Emp_ID)
        ON DELETE CASCADE
);
-- Salary History (Transaction Log)
CREATE TABLE Salary_History (
    History_ID   INT           PRIMARY KEY IDENTITY(1,1),
    Emp_ID       INT           NOT NULL,
    Basic_Salary DECIMAL(10,2),
    Change_Date  DATETIME      DEFAULT GETDATE(),
    Action       VARCHAR(20),
    FOREIGN KEY (Emp_ID) REFERENCES Employees(Emp_ID)
    ON DELETE CASCADE
);
GO
-- STEP 3: INSERT  DATA
-- Departments
INSERT INTO Departments (Dept_ID, Dept_Name, Manager_Name) VALUES
(10, 'IT',      'Mr. Kamran'),
(20, 'HR',      'Ms. Ayesha'),
(30, 'Finance', 'Mr. Tariq'),
(40, 'Admin',   'Ms. Sana');
-- Employees
INSERT INTO Employees (Emp_Name, Dept_ID, Joining_Date) VALUES
('Ali Hassan',     10, '2022-01-15'),
('Sara Khan',      20, '2022-03-20'),
('Ahmed Raza',     10, '2023-06-01'),
('Zara Malik',     30, '2021-11-10'),
('Usman Ahmed',    40, '2023-08-05'),
('Hina Baig',      20, '2024-01-20'),
('Bilal Chaudhry', 10, '2022-09-15');
-- Salaries
INSERT INTO Salaries (Emp_ID, Basic_Salary, Bonus) VALUES
(1, 75000, 7500),
(2, 60000, 6000),
(3, 80000, 8000),
(4, 90000, 9000),
(5, 55000, 5500),
(6, 65000, 6500),
(7, 70000, 7000);
-- Attendance
INSERT INTO Attendance (Emp_ID, Att_Date, Status) VALUES
(1, '2024-12-01', 'Present'),
(1, '2024-12-02', 'Present'),
(2, '2024-12-01', 'Absent'),
(2, '2024-12-02', 'Present'),
(3, '2024-12-01', 'Leave'),
(3, '2024-12-02', 'Present'),
(4, '2024-12-01', 'Present'),
(5, '2024-12-01', 'Present');
-- Leave Management
INSERT INTO Leave_Mgmt (Emp_ID, Leave_Type, Start_Date, End_Date, Status) VALUES
(1, 'Annual', '2024-12-10', '2024-12-14', 'Approved'),
(2, 'Sick',   '2024-12-05', '2024-12-06', 'Approved'),
(3, 'Casual', '2024-12-20', '2024-12-20', 'Pending'),
(4, 'Annual', '2024-12-25', '2024-12-31', 'Pending'),
(5, 'Sick',   '2024-12-03', '2024-12-04', 'Rejected'),
(6, 'Casual', '2024-12-15', '2024-12-15', 'Approved');
-- STEP 4: TRIGGERS (Bonus Marks)
-- Trigger 1: Salary Identity Fix
IF OBJECT_ID('trg_FixSalaryId','TR') IS NOT NULL
    DROP TRIGGER trg_FixSalaryId;
GO
CREATE TRIGGER trg_FixSalaryId
ON Salaries AFTER INSERT
AS BEGIN
    DECLARE @m INT;
    SELECT @m = MAX(Salary_ID) FROM Salaries;
    DBCC CHECKIDENT('Salaries', RESEED, @m);
END;
-- Trigger 2: Leave Identity Fix
IF OBJECT_ID('trg_FixLeaveId','TR') IS NOT NULL
    DROP TRIGGER trg_FixLeaveId;
GO
CREATE TRIGGER trg_FixLeaveId
ON Leave_Mgmt AFTER INSERT
AS BEGIN
    DECLARE @m INT;
    SELECT @m = MAX(Leave_ID) FROM Leave_Mgmt;
    DBCC CHECKIDENT('Leave_Mgmt', RESEED, @m);
END;
-- Trigger 3: Attendance Identity Fix
IF OBJECT_ID('trg_FixAttId','TR') IS NOT NULL
    DROP TRIGGER trg_FixAttId;
GO
CREATE TRIGGER trg_FixAttId
ON Attendance AFTER INSERT
AS BEGIN
    DECLARE @m INT;
    SELECT @m = MAX(Att_ID) FROM Attendance;
    DBCC CHECKIDENT('Attendance', RESEED, @m);
END;
-- STEP 5: VERIFY DATA
SELECT * FROM Departments;
SELECT * FROM Employees;
SELECT * FROM Salaries;
SELECT * FROM Attendance;
SELECT * FROM Leave_Mgmt;
-- STEP 6: JOIN QUERIES
-- Employee + Department
SELECT
    e.Emp_ID,
    e.Emp_Name,
    d.Dept_Name,
    d.Manager_Name,
    e.Joining_Date
FROM Employees e
INNER JOIN Departments d ON e.Dept_ID = d.Dept_ID;
GO
-- Employee + Salary (sorted by highest)
SELECT
    e.Emp_Name,
    d.Dept_Name,
    s.Basic_Salary,
    s.Bonus,
    (s.Basic_Salary + s.Bonus) AS Total_Salary
FROM Employees e
JOIN Departments d ON e.Dept_ID = d.Dept_ID
JOIN Salaries   s ON e.Emp_ID  = s.Emp_ID
ORDER BY Total_Salary DESC;
-- STEP 7: REPORTING QUERIES
-- Total Employees
SELECT COUNT(*) AS Total_Employees FROM Employees;
-- Salary Stats
SELECT
    AVG(Basic_Salary) AS Avg_Basic_Salary,
    SUM(Basic_Salary) AS Total_Salary_Bill,
    MAX(Basic_Salary) AS Highest_Salary,
    MIN(Basic_Salary) AS Lowest_Salary
FROM Salaries;
-- Department-wise Report
SELECT
    d.Dept_Name,
    d.Manager_Name,
    COUNT(e.Emp_ID)              AS Total_Employees,
    ROUND(AVG(s.Basic_Salary),2) AS Avg_Salary,
    SUM(s.Basic_Salary)          AS Total_Salary_Bill
FROM Departments d
LEFT JOIN Employees e ON d.Dept_ID = e.Dept_ID
LEFT JOIN Salaries  s ON e.Emp_ID  = s.Emp_ID
GROUP BY d.Dept_Name, d.Manager_Name
ORDER BY Total_Employees DESC;
-- Leave Status Summary
SELECT
    Status,
    COUNT(*) AS Total
FROM Leave_Mgmt
GROUP BY Status;
-- Attendance Summary per Employee
SELECT
    e.Emp_Name,
    SUM(CASE WHEN a.Status = 'Present' THEN 1 ELSE 0 END) AS Days_Present,
    SUM(CASE WHEN a.Status = 'Absent'  THEN 1 ELSE 0 END) AS Days_Absent,
    SUM(CASE WHEN a.Status = 'Leave'   THEN 1 ELSE 0 END) AS Days_Leave
FROM Employees e
LEFT JOIN Attendance a ON e.Emp_ID = a.Emp_ID
GROUP BY e.Emp_Name;