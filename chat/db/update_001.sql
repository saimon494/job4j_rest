create table person
(
    id       serial primary key not null,
    login    varchar(1000),
    password varchar(1000),
    role_id  serial
);
insert into person(login, password, role_id)
values ('user', 'pass', 1);
insert into person(login, password, role_id)
values ('admin', 'admin', 2);

create table room
(
    id   serial primary key not null,
    name varchar(1000)
);

insert into room(name)
values ('room');

create table message
(
    id        serial primary key not null,
    text      text,
    time      timestamp,
    person_id serial,
    room_id   serial
);

insert into message(text, time, person_id, room_id)
values ('user text', now(), 1, 1);
insert into message(text, time, person_id, room_id)
values ('admin text', now(), 2, 1);

create table role
(
    id   serial primary key not null,
    name varchar(100)
);

insert into role(id, name)
values (1, 'ROLE_USER');
insert into role(id, name)
values (2, 'ROLE_ADMIN');

