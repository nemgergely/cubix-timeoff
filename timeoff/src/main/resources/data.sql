INSERT INTO employee (name, username, password, manager_id) VALUES
    ('Manager One', 'managerM1', 'passwordM1', null),
    ('Manager Two', 'managerM2', 'passwordM2', null),
    ('Employee 1', 'employeeE1', 'passwordE1', 1),
    ('Employee 2', 'employeeE2', 'passwordE2', 1),
    ('Employee 3', 'employeeE3', 'passwordE3', 1),
    ('Employee 4', 'employeeE4', 'passwordE4', 1),
    ('Employee 5', 'employeeE5', 'passwordE5', 1),
    ('Employee 6', 'employeeE6', 'passwordE6', 2),
    ('Employee 7', 'employeeE7', 'passwordE7', 2),
    ('Employee 8', 'employeeE8', 'passwordE8', 2),
    ('Employee 9', 'employeeE9', 'passwordE9', 2),
    ('Employee 10', 'employeeE10', 'passwordE10', 2);

INSERT INTO employee_roles (employee_id, roles) VALUES
    (1, 'manager, user'),
    (2, 'manager, user'),
    (3, 'user'),
    (4, 'user'),
    (5, 'user'),
    (6, 'user'),
    (7, 'user'),
    (8, 'user'),
    (9, 'user'),
    (10, 'user'),
    (11, 'user'),
    (12, 'user');

INSERT INTO time_off_request (start_date, end_date, requester_id, approver_id, request_status, request_date_time) VALUES
    ('2025-07-01', '2025-07-03', 3, 1, 'PENDING', '2025-01-25 08:00:00'),
    ('2030-07-01', '2030-07-03', 3, 1, 'REJECTED', '2030-01-25 08:00:00'),
    ('2025-08-01', '2025-08-03', 4, 1, 'APPROVED', '2025-01-25 08:00:00'),
    ('2025-09-01', '2025-09-03', 5, 1, 'REJECTED', '2025-01-25 08:00:00'),
    ('2025-10-01', '2025-10-03', 8, 2, 'PENDING', '2024-01-25 08:00:00'),
    ('2025-11-01', '2025-11-03', 9, 2, 'APPROVED', '2024-01-25 08:00:00'),
    ('2025-12-01', '2025-12-03', 10, 2, 'REJECTED', '2024-01-25 08:00:00');
