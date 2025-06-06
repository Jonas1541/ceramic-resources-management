-- Arquivo: mysql-init/init.sql

-- Cria a tabela tb_company se ela não existir no banco de dados 'main_db'.
-- O docker-compose já cria o banco 'main_db', então podemos usá-lo diretamente.
USE main_db;

CREATE TABLE IF NOT EXISTS tb_company (
  id bigint NOT NULL AUTO_INCREMENT,
  cnpj varchar(255) DEFAULT NULL UNIQUE,
  created_at datetime DEFAULT CURRENT_TIMESTAMP,
  last_activity_at datetime DEFAULT NULL,
  database_port int DEFAULT NULL,
  database_name varchar(255) DEFAULT NULL,
  database_url varchar(255) DEFAULT NULL,
  email varchar(255) DEFAULT NULL UNIQUE,
  name varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  updated_at datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  marked_for_deletion BOOLEAN NOT NULL DEFAULT FALSE,
  deletion_scheduled_at DATETIME DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Concede ao usuário 'app_admin' todas as permissões em todos os bancos de dados (*.*).
-- Isso é necessário para que a aplicação possa criar e gerenciar os bancos dos tenants.
GRANT ALL PRIVILEGES ON *.* TO 'app_admin'@'%';
FLUSH PRIVILEGES;