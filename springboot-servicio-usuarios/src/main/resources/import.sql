INSERT INTO usuarios(username, password, enabled, nombre, apellido, email) VALUES('andres','$2a$10$l22NpZrUvspNoQ0ig2dAWeOdfK3/GrV03N7dNNlt/kiG2TOjxOoxO', 1, 'Andres','Guzman', 'profesor@bolsadeideas.com');
INSERT INTO usuarios(username, password, enabled, nombre, apellido, email) VALUES('admin','$2a$10$Q404FsXK9TqRP20m.1.bUenqCQNJvb.8k3UWKgSpY.xq3642WfHTe', 1, 'John','Doe', 'john.doe@bolsadeideas.com');

INSERT INTO roles (nombre) VALUES ('ROLE_USER');
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN');

INSERT INTO usuarios_roles (usuario_id,role_id) VALUES (1,1);
INSERT INTO usuarios_roles (usuario_id,role_id) VALUES (2,1);
INSERT INTO usuarios_roles (usuario_id,role_id) VALUES (2,2);