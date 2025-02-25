CREATE TABLE tb_company (
  id bigint NOT NULL AUTO_INCREMENT,
  cnpj varchar(255) DEFAULT NULL UNIQUE,
  created_at datetime DEFAULT CURRENT_TIMESTAMP,
  database_port int DEFAULT NULL,
  database_name varchar(255) DEFAULT NULL,
  database_url varchar(255) DEFAULT NULL,
  email varchar(255) DEFAULT NULL UNIQUE,
  name varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  updated_at datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB