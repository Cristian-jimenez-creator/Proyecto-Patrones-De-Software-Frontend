-- Modelo reconstruido desde mermaid-diagram.png para importar como ERD en Visual Paradigm.
-- En Visual Paradigm Desktop: Tools > DB > Reverse DDL (o Database > Reverse DDL).
-- Tipos VARCHAR y longitudes son decisiones prácticas donde la imagen solo indica "string".

CREATE TABLE PISCINA (
  id_piscina INTEGER PRIMARY KEY,
  nombre VARCHAR(150),
  ubicacion VARCHAR(255),
  capacidad INTEGER,
  descripcion VARCHAR(1000),
  estado VARCHAR(50)
);

CREATE TABLE ZONA (
  id_zona INTEGER PRIMARY KEY,
  id_piscina INTEGER NOT NULL,
  nombre VARCHAR(150),
  descripcion VARCHAR(1000),
  CONSTRAINT fk_zona_piscina FOREIGN KEY (id_piscina) REFERENCES PISCINA(id_piscina)
);

CREATE TABLE CAMARA (
  id_camara INTEGER PRIMARY KEY,
  id_piscina INTEGER NOT NULL,
  id_zona INTEGER NOT NULL,
  nombre VARCHAR(150),
  ubicacion VARCHAR(255),
  resolucion VARCHAR(50),
  url_stream VARCHAR(1000),
  estado VARCHAR(50),
  CONSTRAINT fk_camara_piscina FOREIGN KEY (id_piscina) REFERENCES PISCINA(id_piscina),
  CONSTRAINT fk_camara_zona FOREIGN KEY (id_zona) REFERENCES ZONA(id_zona)
);

CREATE TABLE DETECCION_IA (
  id_deteccion INTEGER PRIMARY KEY,
  id_camara INTEGER NOT NULL,
  tipo_evento VARCHAR(100),
  nivel_riesgo VARCHAR(50),
  confianza DECIMAL(8,6),
  detectada_en TIMESTAMP,
  resultado_ia VARCHAR(2000),
  CONSTRAINT fk_deteccion_camara FOREIGN KEY (id_camara) REFERENCES CAMARA(id_camara)
);

CREATE TABLE ROL (
  id_rol INTEGER PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  descripcion VARCHAR(1000)
);

CREATE TABLE USUARIO (
  id_usuario INTEGER PRIMARY KEY,
  id_rol INTEGER NOT NULL,
  nombre VARCHAR(150),
  correo VARCHAR(320) UNIQUE,
  password_hash VARCHAR(255),
  estado VARCHAR(50),
  creado_en TIMESTAMP,
  CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES ROL(id_rol)
);

CREATE TABLE ALERTA (
  id_alerta INTEGER PRIMARY KEY,
  id_deteccion INTEGER NOT NULL,
  gravedad VARCHAR(50),
  estado VARCHAR(50),
  generada_en TIMESTAMP,
  cerrada_en TIMESTAMP,
  CONSTRAINT fk_alerta_deteccion FOREIGN KEY (id_deteccion) REFERENCES DETECCION_IA(id_deteccion)
);

CREATE TABLE EVIDENCIA (
  id_evidencia INTEGER PRIMARY KEY,
  id_deteccion INTEGER NOT NULL,
  tipo VARCHAR(100),
  url_segura VARCHAR(1000),
  capturada_en TIMESTAMP,
  eliminar_en TIMESTAMP,
  CONSTRAINT fk_evidencia_deteccion FOREIGN KEY (id_deteccion) REFERENCES DETECCION_IA(id_deteccion)
);

CREATE TABLE INCIDENTE (
  id_incidente INTEGER PRIMARY KEY,
  id_deteccion INTEGER NOT NULL,
  clasificacion VARCHAR(100),
  estado VARCHAR(50),
  registrado_en TIMESTAMP,
  cerrado_en TIMESTAMP,
  CONSTRAINT fk_incidente_deteccion FOREIGN KEY (id_deteccion) REFERENCES DETECCION_IA(id_deteccion)
);

CREATE TABLE REGISTRO_ACTIVIDAD (
  id_registro INTEGER PRIMARY KEY,
  id_usuario INTEGER NOT NULL,
  accion VARCHAR(100),
  entidad VARCHAR(100),
  entidad_id INTEGER,
  registrada_en TIMESTAMP,
  CONSTRAINT fk_registro_usuario FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario)
);

CREATE TABLE NOTIFICACION (
  id_notificacion INTEGER PRIMARY KEY,
  id_alerta INTEGER NOT NULL,
  id_usuario INTEGER NOT NULL,
  canal VARCHAR(50),
  estado_envio VARCHAR(50),
  enviada_en TIMESTAMP,
  leida_en TIMESTAMP,
  CONSTRAINT fk_notificacion_alerta FOREIGN KEY (id_alerta) REFERENCES ALERTA(id_alerta),
  CONSTRAINT fk_notificacion_usuario FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario)
);

CREATE TABLE HISTORIAL_ALERTA (
  id_historial INTEGER PRIMARY KEY,
  id_alerta INTEGER NOT NULL,
  id_usuario_actor INTEGER NOT NULL,
  id_usuario_destino INTEGER,
  accion VARCHAR(100),
  comentario VARCHAR(2000),
  realizada_en TIMESTAMP,
  CONSTRAINT fk_historial_alerta FOREIGN KEY (id_alerta) REFERENCES ALERTA(id_alerta),
  CONSTRAINT fk_historial_actor FOREIGN KEY (id_usuario_actor) REFERENCES USUARIO(id_usuario),
  CONSTRAINT fk_historial_destino FOREIGN KEY (id_usuario_destino) REFERENCES USUARIO(id_usuario)
);

CREATE TABLE OBSERVACION_INCIDENTE (
  id_observacion INTEGER PRIMARY KEY,
  id_incidente INTEGER NOT NULL,
  id_usuario INTEGER NOT NULL,
  contenido VARCHAR(2000),
  creada_en TIMESTAMP,
  CONSTRAINT fk_observacion_incidente FOREIGN KEY (id_incidente) REFERENCES INCIDENTE(id_incidente),
  CONSTRAINT fk_observacion_usuario FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario)
);
