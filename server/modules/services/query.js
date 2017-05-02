module.exports = {
    dependencies: ["helper", "condition", "sql", "exec"],
    Instance: function(){
        var _this = this;

        //----------------------------------------------
        // CONSTRUCTOR
        //----------------------------------------------

        function _construct(){}

        //----------------------------------------------
        // PUBLIC
        //----------------------------------------------

        this.quickFind = function(ctx, fields, entity, conditionMap, successCb, completeCb){
            var conditions = [];
            for(var key in conditionMap){
                if(!conditionMap.hasOwnProperty(key)) continue;
                conditions.push(new _this.condition.Condition(key, "=", conditionMap[key]));
            }
            _this.select(ctx, fields, entity, conditions, false, "id", 0, 1, function(responseArr){
                successCb(responseArr[0]);
            }, completeCb);
        };

        this.select = function (ctx, fields, entity, conditions, isFullMode, orderByField, skip, take, successCb, completeCb){
            var queryObj = new query();
            var tableName = entity + "table";
            queryObj.queryString = "select ";
            for(var i=0; i<fields.length; i++){
                var fieldName = fields[i];
                if(!isFullMode && fieldName.contains("richtext")) continue;
                queryObj.queryString += (i === 0 ? "": ",") + "[" + fieldName + "]";
            }
            queryObj.queryString += " from [" + tableName + "] where 1=1 ";
            queryObj.queryString += " and (1=1";
            for (i=0; i<conditions.length; i++){
                var condition = conditions[i];
                queryObj.queryString += " and " + condition.getWhereExpression(queryObj);
            }
            queryObj.queryString += ") order by [" + orderByField + "] ";
            queryObj.queryString += "OFFSET (" + queryObj.addQueryParam(skip) + ") ROWS FETCH NEXT (" + queryObj.addQueryParam(take) + ") ROWS ONLY";
            queryObj.execute(ctx, successCb, completeCb);
        };

        this.insert = function(ctx, entity, fieldNames, fieldValues, successCb, completeCb){
            var queryObj = new query();
            var tableName = entity + "table";
            var fieldNamesStr = "[" + fieldNames.join("],[") + "]";
            queryObj.queryString = "insert into [" + tableName + "] (" + fieldNamesStr + ") values (";
            for(var i=0; i<fieldValues.length; i++){
                queryObj.queryString += (i===0 ? "" : ",") + queryObj.addQueryParam(fieldValues[i]);
            }
            queryObj.queryString += "); select SCOPE_IDENTITY() as [identity];";
            queryObj.execute(ctx, successCb, completeCb);
        };

        this.update = function(ctx, entity, updateFields, conditions, successCb, completeCb){
            var queryObj = new query();
            var tableName = entity + "table";
            var setClause = "";
            for(var fieldName in updateFields){
                if(!updateFields.hasOwnProperty(fieldName)) continue;
                setClause += (setClause === "" ? "": ",") + fieldName + "=" + queryObj.addQueryParam(updateFields[fieldName]);
            }
            var whereClause = "";
            for(var i=0; i<conditions.length; i++){
                whereClause += (whereClause === "" ? "": " and ") + conditions[i].getWhereExpression(queryObj);
            }
            queryObj.queryString = "update [" + tableName + "] set " + setClause + " where " + whereClause;
            queryObj.execute(ctx, successCb, completeCb);
        };

        this.delete = function(ctx, entity, id, successCb, completeCb){
            var queryObj = new query();
            var tableName = entity + "table";
            var condition = new _this.condition.Condition("id", "=", id);
            queryObj.queryString = "delete from [" + tableName + "] where " + condition.getWhereExpression(queryObj);
            queryObj.execute(ctx, successCb, completeCb);
        };

        //----------------------------------------------
        // INTERNAL OBJECTS
        //----------------------------------------------

        function query(){
            var queryObj = this;
            var paramsCounter = 0;
            var queryParams = {};

            this.queryString = null;

            this.addQueryParam = function(paramValue){
                queryParams["value" + paramsCounter] = paramValue;
                return "@value" + paramsCounter++;
            };
        
            this.execute = function(ctx, successCb, completeCb){
                console.log("-------------------------------------------------");
                console.log("Sending query to database:");
                console.log(queryObj.queryString);
                console.log("Query parameters:");
                console.log(queryParams);
                var connection = new _this.sql.Connection(ctx.config.databaseConnectionString);
                connection.connect(function(err){
                    if(err){
                        if(!!completeCb) {
                            _this.exec.safeExecute(ctx, completeCb);
                        }
                        console.log(err);
                        throw new Error("500: error while connecting to database");
                    }
                    var request = new _this.sql.Request(connection);
                    for(var key in queryParams){
                        if(!queryParams.hasOwnProperty(key)) continue;
                        var paramValue = queryParams[key];
                        if(typeof(paramValue) === "number" && Math.abs(paramValue) > 2147483647){
                            request.input(key, _this.sql.BigInt, paramValue);
                        }else{
                            request.input(key, paramValue);
                        }
                    }
                    request.query(queryObj.queryString, function(err, recordset){
                        if(err){
                            if(!!completeCb) {
                                _this.exec.safeExecute(ctx, completeCb);
                            }
                            console.log(err);
                            throw new Error("500: error while sending query to database");
                        }else{
                            _this.exec.safeExecute(ctx, function(){
                                successCb(recordset);
                            });
                            if(!!completeCb) {
                                _this.exec.safeExecute(ctx, completeCb);
                            }
                        }
                    });
                });
                console.log("-------------------------------------------------");
            };
        }
        _construct();
    }
};