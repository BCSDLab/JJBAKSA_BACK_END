ALTER TABLE scrap DROP FOREIGN KEY scrap_ibfk_2;

ALTER TABLE scrap ADD CONSTRAINT scrap_google_shop_fk FOREIGN KEY (shop_id) REFERENCES google_shop (id) ON DELETE SET NULL;