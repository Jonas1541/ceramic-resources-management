-- 1) Tabela: tb_resource
CREATE TABLE tb_resource (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    name VARCHAR(255),
    unit_value DECIMAL(10,2) NOT NULL,
    category ENUM ('COMPONENT','ELECTRICITY','GAS','RAW_MATERIAL','RETAIL','SILICATE','WATER'),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

ALTER TABLE tb_resource 
    ADD CONSTRAINT UK_AUNVLVM32XB4E6590JC9OOOQ UNIQUE (name);

-- 2) Tabela: tb_batch
CREATE TABLE tb_batch (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    batch_total_water_cost_at_time DECIMAL(10,2) NOT NULL,
    resource_total_cost_at_time DECIMAL(10,2) NOT NULL,
    machines_energy_consumption_cost_at_time DECIMAL(10,2) NOT NULL,
    batch_final_cost_at_time DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 3) Tabela: tb_machine
CREATE TABLE tb_machine (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    power DOUBLE NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 4) Tabela intermediária: tb_batch_resource_usage
CREATE TABLE tb_batch_resource_usage (
    id BIGINT NOT NULL AUTO_INCREMENT,
    initial_quantity DOUBLE NOT NULL,
    umidity DOUBLE NOT NULL,
    added_quantity DOUBLE NOT NULL,
    total_cost_at_time DECIMAL(10,2) NOT NULL,
    batch_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (batch_id) REFERENCES tb_batch (id),
    FOREIGN KEY (resource_id) REFERENCES tb_resource (id)
) ENGINE=InnoDB;

-- 5) Tabela intermediária: tb_batch_machine_usage
CREATE TABLE tb_batch_machine_usage (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usage_time BIGINT NOT NULL,
    batch_id BIGINT NOT NULL,
    machine_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (batch_id) REFERENCES tb_batch (id),
    FOREIGN KEY (machine_id) REFERENCES tb_machine (id)
) ENGINE=InnoDB;

-- 6) Tabela: tb_glaze
CREATE TABLE tb_glaze (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    color VARCHAR(255) NOT NULL,
    unit_value DECIMAL(10,2) NOT NULL,
    unit_cost DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 7) Tabela intermediária: tb_glaze_resource_usage
-- Relação com tb_glaze => ON DELETE CASCADE
-- Relação com tb_resource => ON DELETE RESTRICT (p/ evitar deletar Resource em uso)
CREATE TABLE tb_glaze_resource_usage (
    id BIGINT NOT NULL AUTO_INCREMENT,
    quantity DOUBLE NOT NULL,
    glaze_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_GLAZE_RES_USAGE_GLZ 
        FOREIGN KEY (glaze_id) REFERENCES tb_glaze (id) ON DELETE CASCADE,
    CONSTRAINT FK_GLAZE_RES_USAGE_RESOURCE 
        FOREIGN KEY (resource_id) REFERENCES tb_resource (id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 8) Tabela intermediária: tb_glaze_machine_usage
-- Relação com tb_glaze => ON DELETE CASCADE
-- Relação com tb_machine => ON DELETE RESTRICT
CREATE TABLE tb_glaze_machine_usage (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usage_time BIGINT NOT NULL,
    glaze_id BIGINT NOT NULL,
    machine_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_GLAZE_MACH_USAGE_GLZ
        FOREIGN KEY (glaze_id) REFERENCES tb_glaze (id) ON DELETE CASCADE,
    CONSTRAINT FK_GLAZE_MACH_USAGE_MACH
        FOREIGN KEY (machine_id) REFERENCES tb_machine (id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 9) Tabela: tb_glaze_transaction
-- Relação com tb_glaze => ON DELETE RESTRICT (não pode deletar Glaze se ainda tiver transações)
CREATE TABLE tb_glaze_transaction (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    quantity DOUBLE NOT NULL,
    TYPE ENUM('INCOMING','OUTGOING') NOT NULL,
    glaze_id BIGINT NOT NULL,
    resource_total_cost_at_time DECIMAL(10,2) NOT NULL,
    machine_energy_consumption_cost_at_time DECIMAL(10,2) NOT NULL,
    glaze_final_cost_at_time DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_GLAZE_TRANSACTION_GLZ
        FOREIGN KEY (glaze_id) REFERENCES tb_glaze (id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- 10) Tabela: tb_resource_transaction
-- Relação com tb_resource => ON DELETE RESTRICT ou outro
-- Relação com tb_batch => ON DELETE CASCADE
-- Relação com tb_glaze_transaction => ON DELETE CASCADE
CREATE TABLE tb_resource_transaction (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    quantity DOUBLE NOT NULL,
    TYPE ENUM ('INCOMING','OUTGOING'),
    resource_id BIGINT NOT NULL,
    batch_id BIGINT NULL,
    glaze_transaction_id BIGINT NULL,
    cost_at_time DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (resource_id) REFERENCES tb_resource (id),
    FOREIGN KEY (batch_id) REFERENCES tb_batch (id) ON DELETE CASCADE,
    FOREIGN KEY (glaze_transaction_id) REFERENCES tb_glaze_transaction (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE tb_product_line (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE tb_product_type (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE tb_product (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    height DOUBLE NOT NULL,
    length DOUBLE NOT NULL,
    width DOUBLE NOT NULL,
    product_type_id BIGINT NOT NULL,
    product_line_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (product_type_id) REFERENCES tb_product_type (id),
    FOREIGN KEY (product_line_id) REFERENCES tb_product_line (id)
) ENGINE=InnoDB;

CREATE TABLE tb_product_transaction (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    outgoing_at DATETIME,
    state ENUM ('GREENWARE', 'BISCUIT', 'GLAZED') NOT NULL,
    outgoing_reason ENUM ('SOLD', 'DEFECT_DISPOSAL'),
    product_id BIGINT NOT NULL,
    glaze_transaction_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (product_id) REFERENCES tb_product (id),
    FOREIGN KEY (glaze_transaction_id) REFERENCES tb_glaze_transaction (id)
) ENGINE=InnoDB
