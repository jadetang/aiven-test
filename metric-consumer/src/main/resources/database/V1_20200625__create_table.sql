create table metrics
(
    id text not null
        constraint metrics_pk
            primary key,
    machine_identify text not null,
    metric_type text not null,
    metric_value numeric not null,
    metric_time timestamp not null,
    description text not null,
    created_at timestamp default current_timestamp
);

