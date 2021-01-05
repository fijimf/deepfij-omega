
INSERT INTO team(key, name, nickname, logo_url, color1, color2)
VALUES ('dixie-st', 'Dixie State', 'Trailblazers', 'https://i.turner.ncaa.com/sites/default/files/images/logos/schools/bgd/dixie-st.svg', '#BA0C2F', '#BA0C2F');
INSERT INTO team(key, name, nickname, logo_url, color1, color2)
VALUES ('california-baptist', 'California Baptist', 'Lancers', 'https://i.turner.ncaa.com/sites/default/files/images/logos/schools/bgd/california-baptist.svg', '#002554', '#002554');
INSERT INTO team(key, name, nickname, logo_url, color1, color2)
VALUES ('tarleton-st', 'Tarleton St.', 'Texans', 'https://i.turner.ncaa.com/sites/default/files/images/logos/schools/bgd/tarleton-st.svg', '#582C83', '#582C83');
INSERT INTO team(key, name, nickname, logo_url, color1, color2)
VALUES ('bellarmine', 'Bellarmine', 'Knights', 'https://i.turner.ncaa.com/sites/default/files/images/logos/schools/bgd/bellarmine.svg', '#782F40', '#782F40');
INSERT INTO team(key, name, nickname, logo_url, color1, color2)
VALUES ('north-ala', 'North Alabama', 'Lions', 'https://i.turner.ncaa.com/sites/default/files/images/logos/schools/bgd/north-ala.svg', '#582C83', '#582C83');
INSERT INTO team(key, name, nickname, logo_url, color1, color2)
VALUES ('merrimack', 'Merrimack', 'Warriors', 'https://i.turner.ncaa.com/sites/default/files/images/logos/schools/bgd/merrimack.svg', '#003057', '#003057');
INSERT INTO team(key, name, nickname, logo_url, color1, color2)
VALUES ('uc-san-diego', 'UC San Diego', 'Tritons', 'https://i.turner.ncaa.com/sites/default/files/images/logos/schools/bgd/uc-san-diego.svg', '#003057', '#003057');

insert into conference_mapping(season_id, team_id, conference_id)
select a.id, b.id, c.id from season a cross join team b cross join conference c
where b.key = 'dixie-st'  and  c.key ='western-athletic';
insert into conference_mapping(season_id, team_id, conference_id)
select a.id, b.id, c.id from season a cross join team b cross join conference c
where b.key = 'california-baptist'  and  c.key ='western-athletic';
insert into conference_mapping(season_id, team_id, conference_id)
select a.id, b.id, c.id from season a cross join team b cross join conference c
where b.key = 'tarleton-st'  and  c.key ='western-athletic';
insert into conference_mapping(season_id, team_id, conference_id)
select a.id, b.id, c.id from season a cross join team b cross join conference c
where b.key = 'bellarmine'  and  c.key ='atlantic-sun';
insert into conference_mapping(season_id, team_id, conference_id)
select a.id, b.id, c.id from season a cross join team b cross join conference c
where b.key = 'north-ala'  and  c.key ='atlantic-sun';
insert into conference_mapping(season_id, team_id, conference_id)
select a.id, b.id, c.id from season a cross join team b cross join conference c
where b.key = 'merrimack'  and  c.key ='northeast';
insert into conference_mapping(season_id, team_id, conference_id)
select a.id, b.id, c.id from season a cross join team b cross join conference c
where b.key = 'uc-san-diego'  and  c.key ='big-west';
