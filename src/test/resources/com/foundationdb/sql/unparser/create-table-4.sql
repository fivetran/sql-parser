CREATE TABLE intervals (
  y INTERVAL YEAR,
  y_4 INTERVAL YEAR(4),
  y_m INTERVAL YEAR TO MONTH,
  m INTERVAL MONTH,
  "d" INTERVAL DAY,
  d_s INTERVAL DAY TO SECOND,
  d_2_h INTERVAL DAY(2) TO HOUR,
  d_s_9 INTERVAL DAY TO SECOND(9),
  s INTERVAL SECOND,
  s_6_3 INTERVAL SECOND(6,3)
)