create table city (
  id int primary key,
  name text,
  latitude double precision,
  longitude double precision  
);

create table distance (
	distance double precision,
    fromCity int references city(id),
    toCity int references city(id)
	constraint chk_distance check (fromCity != toCity and toCity != fromCity)
);

insert into city (id, name, latitude, longitude) 
values (1, 'samara', 53.2001, 50.15);

insert into city (id, name, latitude, longitude) 
values (2, 'moskow', 55.7522, 37.6156);

insert into city (id, name, latitude, longitude) 
values (3, 'cyzran', 53.1585, 48.4681);


insert into distance (distance, fromCity, toCity) 
values (180, 1, 2);

insert into distance (distance, fromCity, toCity) 
values (70, 1, 3);

insert into distance (distance, fromCity, toCity) 
values (180, 2, 1);

insert into distance (distance, fromCity, toCity) 
values (1500, 2, 3);

insert into distance (distance, fromCity, toCity) 
values (70, 3, 1);

insert into distance (distance, fromCity, toCity) 
values (1500, 3, 2);
