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