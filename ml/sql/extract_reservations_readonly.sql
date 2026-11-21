-- Equivalente ejecutable de la consulta usada por src/export_reservations.py.
-- No incluye customer_name y no realiza escrituras sobre tablas del PMS.
SET @as_of_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY);

SELECT
    id,
    check_in_date,
    check_out_date,
    room_type,
    status,
    total_price
FROM reservations
WHERE check_in_date <= @as_of_date
ORDER BY check_in_date, id;

