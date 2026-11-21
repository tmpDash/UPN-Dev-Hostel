-- Hotel Luxury - carga sintética de históricos para el prototipo IA/ML.
-- Compatible con MySQL 8.x. Ejecutar una sola vez en un entorno de desarrollo.
-- No borra ni actualiza datos existentes. Todos los registros se identifican
-- mediante customer_name con prefijo ML-SYN-.
--
-- El esquema actual no relaciona Reservation con Hotel, por lo que los datos
-- representan demanda global de la cadena.

DELIMITER $$

DROP PROCEDURE IF EXISTS seed_ml_historical_reservations$$

CREATE PROCEDURE seed_ml_historical_reservations()
main: BEGIN
    DECLARE v_date DATE DEFAULT '2024-01-01';
    DECLARE v_end DATE;
    DECLARE v_i INT DEFAULT 0;
    DECLARE v_arrivals INT DEFAULT 0;
    DECLARE v_stay INT DEFAULT 0;
    DECLARE v_room_type VARCHAR(20);
    DECLARE v_rate DECIMAL(10,2);
    DECLARE v_inserted INT DEFAULT 0;

    SET v_end = LEAST(DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE('2026-10-31'));

    IF EXISTS (
        SELECT 1
        FROM reservations
        WHERE customer_name LIKE 'ML-SYN-%'
        LIMIT 1
    ) THEN
        SELECT 'Carga omitida: ya existen reservas ML-SYN-.' AS resultado;
        LEAVE main;
    END IF;

    IF v_end < v_date THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La fecha final calculada es anterior a 2024-01-01.';
    END IF;

    START TRANSACTION;

    WHILE v_date <= v_end DO
        -- Entre 9 y 22 llegadas diarias, con mayor demanda en fines de semana
        -- y en julio, agosto y diciembre.
        SET v_arrivals = 9 + MOD(DAYOFYEAR(v_date) * 7 + YEAR(v_date), 7);
        IF DAYOFWEEK(v_date) IN (1, 7) THEN
            SET v_arrivals = v_arrivals + 3;
        END IF;
        IF MONTH(v_date) IN (7, 8, 12) THEN
            SET v_arrivals = v_arrivals + 4;
        END IF;

        SET v_i = 1;
        WHILE v_i <= v_arrivals DO
            SET v_stay = 2 + MOD(DAYOFYEAR(v_date) + v_i * 3, 5);

            CASE MOD(DAYOFYEAR(v_date) + v_i, 10)
                WHEN 0 THEN SET v_room_type = 'SUITE', v_rate = 350.00;
                WHEN 1 THEN SET v_room_type = 'SUITE', v_rate = 350.00;
                WHEN 2 THEN SET v_room_type = 'DOBLE', v_rate = 190.00;
                WHEN 3 THEN SET v_room_type = 'DOBLE', v_rate = 190.00;
                WHEN 4 THEN SET v_room_type = 'DOBLE', v_rate = 190.00;
                ELSE SET v_room_type = 'SIMPLE', v_rate = 120.00;
            END CASE;

            INSERT INTO reservations (
                customer_name,
                check_in_date,
                check_out_date,
                room_type,
                status,
                total_price
            ) VALUES (
                CONCAT('ML-SYN-', DATE_FORMAT(v_date, '%Y%m%d'), '-', LPAD(v_i, 3, '0')),
                v_date,
                DATE_ADD(v_date, INTERVAL v_stay DAY),
                v_room_type,
                'COMPLETADA',
                v_rate * v_stay
            );

            SET v_inserted = v_inserted + 1;
            SET v_i = v_i + 1;
        END WHILE;

        -- Una cancelación cada tres días permite evaluar el indicador sin
        -- convertirla en ocupación realizada.
        IF MOD(DAYOFYEAR(v_date), 3) = 0 THEN
            INSERT INTO reservations (
                customer_name,
                check_in_date,
                check_out_date,
                room_type,
                status,
                total_price
            ) VALUES (
                CONCAT('ML-SYN-', DATE_FORMAT(v_date, '%Y%m%d'), '-C01'),
                v_date,
                DATE_ADD(v_date, INTERVAL (2 + MOD(DAYOFYEAR(v_date), 4)) DAY),
                'DOBLE',
                'CANCELADA',
                0.00
            );
            SET v_inserted = v_inserted + 1;
        END IF;

        SET v_date = DATE_ADD(v_date, INTERVAL 1 DAY);
    END WHILE;

    COMMIT;
    SELECT v_inserted AS reservas_insertadas, 'Datos sintéticos ML-SYN-' AS tipo_datos;
END$$

CALL seed_ml_historical_reservations()$$
DROP PROCEDURE seed_ml_historical_reservations$$

DELIMITER ;

