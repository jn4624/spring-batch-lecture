create table `customer` (
    `id` mediumint(8) unsigned not null auto_increment,
    `firstName` varchar(255) default null,
    `lastName` varchar(255) default null,
    `birthdate` varchar(255),
    primary key (`id`)
) auto_increment=1;

create table `customer_copy` (
    `id` mediumint(8) unsigned not null auto_increment,
    `firstName` varchar(255) default null,
    `lastName` varchar(255) default null,
    `birthdate` varchar(255),
    primary key (`id`)
) auto_increment=1;
