var fs = require('fs');
var config = require("./config");
var inputPath = "config.js";
var outputPath = "init.sql";
step1();

function step1(){
    fs.writeFile(outputPath, "", function(err){
        if(err) return console.log(err);
        step2();
    });
}

function step2(){
    var dropTableStr = "";
    var createTableStr = "";
    for(var entityName in config.entities){
        var entity = config.entities[entityName];
        if(!config.entities.hasOwnProperty(entityName)) continue;
        if(entityName === "asset") continue;
        dropTableStr += "IF exists (select * from sys.objects where name = '" + entityName + "table') DROP TABLE " + entityName + "table;\n";
        createTableStr += "CREATE TABLE " + entityName + "table (\n";
        for(var fieldName in entity.fields){
            var field = entity.fields[fieldName];
            var fieldType = field.type;
            createTableStr += "[" + fieldName + "] ";
            if(fieldType === "id"){
                createTableStr += "INT NOT NULL IDENTITY(1,1) PRIMARY KEY,\n";
            } else if (fieldType === "string") {
                createTableStr += "VARCHAR (255) NULL,\n";
            } else if (fieldType === "int") {
                createTableStr += "INT NULL,\n";
            } else if (fieldType === "timestamp") {
                createTableStr += "BIGINT NOT NULL,\n";
            } else if (fieldType === "float") {
                createTableStr += "FLOAT NULL,\n";
            } else if (fieldType === "richtext") {
                createTableStr += "TEXT NULL,\n";
            } else if (fieldType === "boolean") {
                createTableStr += "BIT NULL,\n";
            }
        }
        createTableStr += ");\n";
    }
    var str = dropTableStr + createTableStr;
    fs.writeFile(outputPath, str, function(err) {
        if(err) return console.log(err);
        console.log("Done.");
    }); 
}
