alter table review drop foreign key review_ibfk_2;
alter table review drop shop_id;

alter table review add google_place_id varchar(255);
alter table review add foreign key(google_place_id) references google_shop(place_id) ON DELETE SET NULL;