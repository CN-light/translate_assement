drop table if exists translation;
create table translation(
	id VARCHAR(32),
	ip VARCHAR(16) not null,
	engine VARCHAR(32) not null,
	source VARCHAR(32) not null,
	target VARCHAR(32) not null,
	grade int(3) not null,
	date date not null,
	primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists dictionary;
create table dictionary(
	id VARCHAR(32),
	name VARCHAR(10) not null,
	code VARCHAR(10) not null,
	alias VARCHAR(20) not null,
	primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists text;
create table text(
	id VARCHAR(32),
	type VARCHAR(32) not null,
	text VARCHAR(5000) not null,
	primary key(id)
)	ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists engine;
create table engine(
	id VARCHAR(32),
	name VARCHAR(32) not null,
	language VARCHAR(32) not null,
	abbreviation VARCHAR(10) not null,
	primary key(id)
)	ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into dictionary values(REPLACE(UUID(),"-",""), "chinese", "语言", "中文");
insert into dictionary values(REPLACE(UUID(),"-",""), "english", "语言", "英文");
insert into dictionary values(REPLACE(UUID(),"-",""), "baidu", "翻译引擎", "百度翻译");
insert into dictionary values(REPLACE(UUID(),"-",""), "alibaba", "翻译引擎", "阿里翻译");
insert into dictionary values(REPLACE(UUID(),"-",""), "youdao", "翻译引擎", "有道翻译");
insert into dictionary values(REPLACE(UUID(),"-",""), "tencent", "翻译引擎", "腾讯翻译君");
insert into engine values(REPLACE(UUID(),"-",""), (select id from dictionary where code = "翻译引擎" and name = "youdao"),
(select id from dictionary where code = "语言" and name = "chinese"), "zh-CHS");
insert into engine values(REPLACE(UUID(),"-",""), (select id from dictionary where code = "翻译引擎" and name = "baidu"),
(select id from dictionary where code = "语言" and name = "chinese"), "zh");
insert into engine values(REPLACE(UUID(),"-",""), (select id from dictionary where code = "翻译引擎" and name = "alibaba"),
(select id from dictionary where code = "语言" and name = "chinese"), "zh");
insert into engine values(REPLACE(UUID(),"-",""), (select id from dictionary where code = "翻译引擎" and name = "tencent"),
(select id from dictionary where code = "语言" and name = "chinese"), "zh");
insert into engine values(REPLACE(UUID(),"-",""), (select id from dictionary where code = "翻译引擎" and name = "youdao"),
(select id from dictionary where code = "语言" and name = "english"), "en");
insert into engine values(REPLACE(UUID(),"-",""), (select id from dictionary where code = "翻译引擎" and name = "baidu"),
(select id from dictionary where code = "语言" and name = "english"), "en");
insert into engine values(REPLACE(UUID(),"-",""), (select id from dictionary where code = "翻译引擎" and name = "alibaba"),
(select id from dictionary where code = "语言" and name = "english"), "en");
insert into engine values(REPLACE(UUID(),"-",""), (select id from dictionary where code = "翻译引擎" and name = "tencent"),
(select id from dictionary where code = "语言" and name = "english"), "en");