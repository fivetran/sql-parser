create index if not exists idx1 on table1(p.a, p.b, z_order_lat_lon(c.c1, c.c2), c."d", c.e) using left join