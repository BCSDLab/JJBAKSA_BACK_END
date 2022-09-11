
insert into example.image (id, path, created_at, updated_at, is_deleted, original_name) values
    (1, 'path', now(), now(), 0,'origin')
;
-- 테스트 계정
insert into example.user (id, password, nickname, email, profile_image_id, user_type, created_at, updated_at, is_deleted) values
    (1, 'asdf1234', 'Dpwns', 'dpwns@mail.com', 1, 'NORMAL', now(), now(), 0),
    (2, 'asdf1234', 'dpwns2', 'dpwns2@mail.com', 1, 'NORMAL', now(), now(), 0);

insert into example.shop (id, place_id, place_name, x, y, category_name, created_at, updated_at, is_deleted) values
    (1, '병천면', '빽다방', '0', '0', '카페', now(), now(), 0)
;
-- 123 게시글
insert into example.review(id, writer_id, shop_id, content, is_temp, created_at, updated_at, is_deleted) values
                                                                                                          (1, 1, 1,'content1', 0, '2021-05-30 23:53:46', '2021-05-30 23:53:46', 0),
                                                                                                          (2, 1, 1,'content2', 0, '2021-05-30 23:54:46', '2021-05-30 23:54:46', 0),
                                                                                                          (3, 1, 1,'content3', 0, '2021-05-30 23:55:46', '2021-05-30 23:55:46', 0),
                                                                                                        (4, 1, 1,'content4', 0, '2021-05-30 23:52:46', '2021-05-30 23:52:46', 0);
insert into example.user_count(user_id, review_count, friend_count) values(1, 4, 0);

insert into example.shop_count(shop_id, total_rating, rating_count) values(1,0,0)
