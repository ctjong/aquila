IF exists (select * from sys.objects where name = 'usertable') DROP TABLE usertable;
IF exists (select * from sys.objects where name = 'tasktable') DROP TABLE tasktable;
CREATE TABLE usertable (
[id] INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
[domain] VARCHAR (255) NULL,
[domainid] VARCHAR (255) NULL,
[roles] VARCHAR (255) NULL,
[username] VARCHAR (255) NULL,
[password] VARCHAR (255) NULL,
[email] VARCHAR (255) NULL,
[firstname] VARCHAR (255) NULL,
[lastname] VARCHAR (255) NULL,
[createdtime] BIGINT NOT NULL,
);
CREATE TABLE tasktable (
[id] INT NOT NULL IDENTITY(1,1) PRIMARY KEY,
[ownerid] INT NULL,
[taskname] VARCHAR (255) NULL,
[taskdescription] VARCHAR (255) NULL,
[taskdate] INT NULL,
[createdtime] BIGINT NOT NULL,
);
