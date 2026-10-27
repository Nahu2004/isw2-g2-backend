INSERT INTO equipo (id, torneo_id, nombre) VALUES (1,1,'Alpha'),(2,1,'Beta'),(3,1,'Gamma'),(4,1,'Epsilon'),(5,1,'Delta');
INSERT INTO jugador (id, equipo_id, nombre, apellido, dni) VALUES (1,1,'Ana','Uno','1'),(2,2,'Beto','Dos','2'),(3,3,'Gus','Tres','3');
INSERT INTO partido (id,torneo_id,numero_fecha,equipo_local_id,equipo_visitante_id,cancha_id,arbitro_id,fecha_hora_inicio,estado) VALUES
 (1,1,1,1,2,1,1,'2026-10-01 15:00:00','FINALIZADO'),
 (2,1,2,2,3,1,1,'2026-10-08 15:00:00','FINALIZADO'),
 (3,1,3,1,3,1,1,'2026-10-15 15:00:00','PROGRAMADO'),
 (4,1,3,4,5,1,1,'2026-10-15 17:00:00','EN_JUEGO');
INSERT INTO evento_partido (partido_id,jugador_id,tipo,minuto) VALUES
 (1,1,'GOL',10),(1,2,'GOL',50),(1,1,'GOL',70),(1,2,'AMARILLA',30),
 (2,2,'GOL',20),(2,3,'GOL',60),
 (4,3,'GOL',5);
