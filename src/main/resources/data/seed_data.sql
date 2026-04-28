INSERT INTO users (id, first_name, last_name, username, password, is_active) VALUES
(1, 'John', 'Doe', 'John.Doe', 'pass123456', true),
(2, 'Jane', 'Smith', 'Jane.Smith', 'pass456789', true),
(3, 'Alex', 'Brown', 'Alex.Brown', 'pass789123', true),
(4, 'Emily', 'Davis', 'Emily.Davis', 'pass321654', false),
(5, 'Michael', 'Wilson', 'Michael.Wilson', 'pass654987', true),

(6, 'Mike', 'Johnson', 'Mike.Johnson', 'trainerpass1', true),
(7, 'Sarah', 'Williams', 'Sarah.Williams', 'trainerpass2', true),
(8, 'David', 'Miller', 'David.Miller', 'trainerpass3', true),
(9, 'Laura', 'Garcia', 'Laura.Garcia', 'trainerpass4', false),
(10, 'Chris', 'Martinez', 'Chris.Martinez', 'trainerpass5', true);

INSERT INTO trainees (id, date_of_birth, address) VALUES
(1, '1990-05-15', '123 Main St, New York, NY'),
(2, '1992-08-22', '456 Oak Ave, Los Angeles, CA'),
(3, '1988-03-10', '789 Pine Rd, Chicago, IL'),
(4, '1995-11-05', '321 Cedar St, Houston, TX'),
(5, '1991-07-19', '654 Birch Ln, Phoenix, AZ');

INSERT INTO trainers (id, specialization_id) VALUES
(6, 4),
(7, 2),
(8, 3),
(9, 5),
(10, 1);

INSERT INTO trainings (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES
(1, 3, 6, 'Morning Stretch', 4, '2026-04-20', 30),
(2, 1, 7, 'Yoga Flow', 2, '2026-04-21', 60),
(3, 2, 8, 'Zumba Dance', 3, '2026-04-22', 45),
(4, 5, 9, 'Strength Training', 5, '2026-04-23', 50),
(5, 5, 10, 'Full Body Fitness', 1, '2026-04-24', 70);

INSERT INTO trainee_trainer (trainee_id, trainer_id) VALUES
(3, 6),
(1, 7),
(2, 8),
(5, 9),
(5, 10);