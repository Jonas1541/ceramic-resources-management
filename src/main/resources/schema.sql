create table tb_resource (
    id bigint not null auto_increment,
    name varchar(255),
    unit_value decimal(10,2) not null,
    category enum ('COMPONENT','ELECTRICITY','GAS','RAW_MATERIAL','RETAIL','SILICATE','WATER'),
    primary key (id)
) engine=InnoDB;

create table tb_resource_transaction (
    id bigint not null auto_increment,
    created_at datetime default current_timestamp not null,
    updated_at datetime default current_timestamp on update current_timestamp not null,
    quantity double not null,
    type enum ('INCOMING','OUTGOING'),
    resource_id bigint not null,
    primary key (id),
    foreign key (resource_id) references tb_resource (id)
) engine=InnoDB;

CREATE TABLE tb_machine (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    power DOUBLE NOT NULL
);

alter table tb_resource 
    add constraint UKaunvlvm32xb4e6590jc9oooq unique (name);

-- ========================================
-- Tabela principal: tb_batch
-- ========================================
CREATE TABLE tb_batch (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ========================================
-- Tabela intermediária: tb_batch_resource_usage
-- ========================================
CREATE TABLE tb_batch_resource_usage (
    id BIGINT NOT NULL AUTO_INCREMENT,
    initial_quantity DOUBLE NOT NULL,
    umidity DOUBLE NOT NULL,
    added_quantity DOUBLE NOT NULL,
    batch_id BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    
    PRIMARY KEY (id),
    CONSTRAINT fk_batch_res_usage_batch 
        FOREIGN KEY (batch_id) REFERENCES tb_batch (id),
    CONSTRAINT fk_batch_res_usage_resource 
        FOREIGN KEY (resource_id) REFERENCES tb_resource (id)
) ENGINE=InnoDB;

-- ========================================
-- Tabela intermediária: tb_batch_machine_usage
-- ========================================
CREATE TABLE tb_batch_machine_usage (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usage_time BIGINT NOT NULL,
    batch_id BIGINT NOT NULL,
    machine_id BIGINT NOT NULL,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    
    PRIMARY KEY (id),
    CONSTRAINT fk_batch_mach_usage_batch 
        FOREIGN KEY (batch_id) REFERENCES tb_batch (id),
    CONSTRAINT fk_batch_mach_usage_machine 
        FOREIGN KEY (machine_id) REFERENCES tb_machine (id)
) ENGINE=InnoDB;

