create table tb_company (
    id bigint not null auto_increment,
    name varchar(255) not null,
    email varchar(255) not null,
    cnpj varchar(14) not null,
    database_name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

alter table tb_company 
    add constraint UKruse2bynlu533jtqlc6r4g4xh unique (cnpj),
    add constraint UKgq2acy6se5ohgkps24kpeuxgk unique (database_name),
    add constraint UKa895hjqwnuln8njdho9nxsqnd unique (email);
