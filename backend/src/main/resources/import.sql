-- 1. Hoteles (5 registros)
INSERT INTO hoteles (nombre, direccion, ciudad, habitaciones, fecha_creacion) VALUES ('Gran Hotel UPN', 'Av. El Sol 123', 'Lima', 50, CURDATE());
INSERT INTO hoteles (nombre, direccion, ciudad, habitaciones, fecha_creacion) VALUES ('Hotel Mar y Sol', 'Calle Arena 456', 'Trujillo', 30, CURDATE());
INSERT INTO hoteles (nombre, direccion, ciudad, habitaciones, fecha_creacion) VALUES ('Sierra Blanca Resort', 'Pje. Los Andes 789', 'Cusco', 45, CURDATE());
INSERT INTO hoteles (nombre, direccion, ciudad, habitaciones, fecha_creacion) VALUES ('Selva Viva Lodge', 'Km 15 Rio Amazonas', 'Iquitos', 20, CURDATE());
INSERT INTO hoteles (nombre, direccion, ciudad, habitaciones, fecha_creacion) VALUES ('Arequipa Plaza Hotel', 'Calle Mercaderes 101', 'Arequipa', 35, CURDATE());

-- 2. Servicios (10 registros)
INSERT INTO hotel_services (name, description, price, icon, category, is_active) VALUES ('Restaurante Gourmet', 'Servicio de cena y desayuno a la carta.', 45.00, 'bi-cup-hot', 'FOOD', true);
INSERT INTO hotel_services (name, description, price, icon, category, is_active) VALUES ('Spa & Relax', 'Masajes terapéuticos y sauna.', 80.00, 'bi-flower1', 'WELLNESS', true);
INSERT INTO hotel_services (name, description, price, icon, category, is_active) VALUES ('Gimnasio 24/7', 'Equipamiento completo de cardio y pesas.', 0.00, 'bi-bicycle', 'GENERAL', true);
INSERT INTO hotel_services (name, description, price, icon, category, is_active) VALUES ('Transporte al Aeropuerto', 'Traslado privado en van de lujo.', 25.00, 'bi-car-front', 'TRANSPORT', true);
INSERT INTO hotel_services (name, description, price, icon, category, is_active) VALUES ('Lavandería Express', 'Lavado y planchado en 4 horas.', 15.00, 'bi-droplet', 'GENERAL', true);
INSERT INTO hotel_services (name, description, price, icon, category, is_active) VALUES ('Tour Arqueológico', 'Guía bilingüe por sitios históricos.', 60.00, 'bi-map', 'TRANSPORT', true);
INSERT INTO hotel_services (name, description, price, icon, category, is_active) VALUES ('Room Service 24h', 'Pedidos a la habitación sin recargo.', 10.00, 'bi-bell', 'FOOD', true);
INSERT INTO hotel_services (name, description, price, icon, category, is_active) VALUES ('Piscina Climatizada', 'Acceso libre a piscina y toallas.', 0.00, 'bi-water', 'WELLNESS', true);
INSERT INTO hotel_services (name, description, price, icon, category, is_active) VALUES ('Wi-Fi Premium', 'Conexión de alta velocidad 1Gbps.', 5.00, 'bi-wifi', 'GENERAL', true);
INSERT INTO hotel_services (name, description, price, icon, category, is_active) VALUES ('Alquiler de Bicicletas', 'Bicicletas de montaña para paseo.', 12.00, 'bi-bicycle', 'TRANSPORT', true);

-- 3. Staff (9 registros con contraseñas cifradas con BCrypt: '123456')
INSERT INTO staff_users (full_name, email, role, status, password, last_access) VALUES ('Administrador General', 'admin@luxuryhotel.com', 'ADMIN', 'ACTIVO', '$2a$10$sbJ9QavygOXEc5oqbn9Pd.adJIYNTfwqhrO/QXHeVAkrcMvXk9YpG', NOW());
INSERT INTO staff_users (full_name, email, role, status, password, last_access) VALUES ('Juan Perez', 'juan.perez@luxuryhotel.com', 'RECEPCIONISTA', 'ACTIVO', '$2a$10$sbJ9QavygOXEc5oqbn9Pd.adJIYNTfwqhrO/QXHeVAkrcMvXk9YpG', NOW());
INSERT INTO staff_users (full_name, email, role, status, password, last_access) VALUES ('Maria Garcia', 'maria.garcia@luxuryhotel.com', 'SUPERVISOR', 'ACTIVO', '$2a$10$sbJ9QavygOXEc5oqbn9Pd.adJIYNTfwqhrO/QXHeVAkrcMvXk9YpG', NOW());
INSERT INTO staff_users (full_name, email, role, status, password, last_access) VALUES ('Carlos Ruiz', 'carlos.ruiz@luxuryhotel.com', 'RECEPCIONISTA', 'ACTIVO', '$2a$10$sbJ9QavygOXEc5oqbn9Pd.adJIYNTfwqhrO/QXHeVAkrcMvXk9YpG', NOW());
INSERT INTO staff_users (full_name, email, role, status, password, last_access) VALUES ('Lucia Mendoza', 'lucia.mendo@luxuryhotel.com', 'RECEPCIONISTA', 'INACTIVO', '$2a$10$sbJ9QavygOXEc5oqbn9Pd.adJIYNTfwqhrO/QXHeVAkrcMvXk9YpG', NOW());
INSERT INTO staff_users (full_name, email, role, status, password, last_access) VALUES ('Roberto Diaz', 'roberto.diaz@luxuryhotel.com', 'ADMIN', 'ACTIVO', '$2a$10$sbJ9QavygOXEc5oqbn9Pd.adJIYNTfwqhrO/QXHeVAkrcMvXk9YpG', NOW());
INSERT INTO staff_users (full_name, email, role, status, password, last_access) VALUES ('Elena Torres', 'elena.torres@luxuryhotel.com', 'SUPERVISOR', 'ACTIVO', '$2a$10$sbJ9QavygOXEc5oqbn9Pd.adJIYNTfwqhrO/QXHeVAkrcMvXk9YpG', NOW());
INSERT INTO staff_users (full_name, email, role, status, password, last_access) VALUES ('Pedro Gomez', 'pedro.gomez@luxuryhotel.com', 'RECEPCIONISTA', 'ACTIVO', '$2a$10$sbJ9QavygOXEc5oqbn9Pd.adJIYNTfwqhrO/QXHeVAkrcMvXk9YpG', NOW());
INSERT INTO staff_users (full_name, email, role, status, password, last_access) VALUES ('Ana Martinez', 'ana.limpieza@luxuryhotel.com', 'LIMPIEZA', 'ACTIVO', '$2a$10$sbJ9QavygOXEc5oqbn9Pd.adJIYNTfwqhrO/QXHeVAkrcMvXk9YpG', NOW());

-- 4. Reservas (15 registros)
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Carlos Sanchez', '2026-06-01', '2026-06-05', 'SUITE', 'CONFIRMADA', 500.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Ana Lopez', '2026-06-10', '2026-06-12', 'SIMPLE', 'PENDIENTE', 150.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Luis Miguel', '2026-07-01', '2026-07-07', 'DOBLE', 'CONFIRMADA', 850.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Sofia Vergara', '2026-05-15', '2026-05-20', 'SUITE', 'CANCELADA', 1200.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Ricardo Gareca', '2026-08-10', '2026-08-15', 'DOBLE', 'PENDIENTE', 450.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Paolo Guerrero', '2026-09-01', '2026-09-05', 'SIMPLE', 'CONFIRMADA', 300.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Christian Cueva', '2026-06-20', '2026-06-22', 'SIMPLE', 'PENDIENTE', 200.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Gianluca Lapadula', '2026-07-15', '2026-07-25', 'SUITE', 'CONFIRMADA', 2500.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Andre Carrillo', '2026-10-01', '2026-10-05', 'DOBLE', 'PENDIENTE', 600.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Yoshimar Yotun', '2026-05-25', '2026-05-30', 'SIMPLE', 'CANCELADA', 400.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Pedro Gallese', '2026-11-10', '2026-11-15', 'DOBLE', 'CONFIRMADA', 550.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Luis Advincula', '2026-12-20', '2026-12-25', 'SUITE', 'PENDIENTE', 1800.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Renato Tapia', '2026-07-05', '2026-07-10', 'SIMPLE', 'CONFIRMADA', 450.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Edison Flores', '2026-08-01', '2026-08-03', 'DOBLE', 'CONFIRMADA', 320.00);
INSERT INTO reservations (customer_name, check_in_date, check_out_date, room_type, status, total_price) VALUES ('Alexander Callens', '2026-09-15', '2026-09-20', 'SUITE', 'PENDIENTE', 1400.00);

-- 5. Detalles de Servicios en Reservas (Asociar servicios a las reservas anteriores)
-- Carlos Sanchez (Reserva 1) - Restaurante y Spa
INSERT INTO reservation_service_details (reservation_id, service_id, name, quantity, price) VALUES (1, 1, 'Restaurante Gourmet', 1, 45.00);
INSERT INTO reservation_service_details (reservation_id, service_id, name, quantity, price) VALUES (1, 2, 'Spa & Relax', 1, 80.00);

-- Luis Miguel (Reserva 3) - Transporte y WiFi
INSERT INTO reservation_service_details (reservation_id, service_id, name, quantity, price) VALUES (3, 4, 'Transporte al Aeropuerto', 2, 25.00);
INSERT INTO reservation_service_details (reservation_id, service_id, name, quantity, price) VALUES (3, 9, 'Wi-Fi Premium', 1, 5.00);

-- Gianluca Lapadula (Reserva 8) - Varios servicios
INSERT INTO reservation_service_details (reservation_id, service_id, name, quantity, price) VALUES (8, 1, 'Restaurante Gourmet', 3, 45.00);
INSERT INTO reservation_service_details (reservation_id, service_id, name, quantity, price) VALUES (8, 2, 'Spa & Relax', 2, 80.00);
INSERT INTO reservation_service_details (reservation_id, service_id, name, quantity, price) VALUES (8, 6, 'Tour Arqueológico', 1, 60.00);

-- Paolo Guerrero (Reserva 6) - Lavanderia
INSERT INTO reservation_service_details (reservation_id, service_id, name, quantity, price) VALUES (6, 5, 'Lavandería Express', 1, 15.00);
