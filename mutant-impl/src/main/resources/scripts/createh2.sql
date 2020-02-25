create table if not exists summary (
id integer not null auto_increment,
humans integer not null ,
mutants integer not null
);

insert into summary(id, humans, mutants) select 1,0,0 from dual where 1 not in (select id from summary);

create table if not exists evaluations (
id integer default 0,
dna varchar(4000) not null,
mutant boolean not null,
instant timestamp without time zone
)