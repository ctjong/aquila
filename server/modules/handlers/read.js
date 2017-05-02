module.exports = {
    dependencies: ["helper", "condition", "query", "auth"],
    Instance: function() {
        var _this = this;

        //----------------------------------------------
        // CONSTRUCTOR
        //----------------------------------------------

        function _construct(){}

        //----------------------------------------------
        // PUBLIC
        //----------------------------------------------

        this.execute = function(ctx, requestParams, isFullMode){
            var orderByField = !requestParams.orderByField ? "id" : requestParams.orderByField;
            _this.auth.authorizeField(ctx, orderByField, "read");

            // get pagination info and conditions
            var skip = isNaN(parseInt(requestParams.skip)) ? 0 : parseInt(requestParams.skip);
            var take = isNaN(parseInt(requestParams.take)) ? 10 : parseInt(requestParams.take);
            var conditions = getConditions(ctx, requestParams);

            // execute
            var fields = _this.auth.getAllowedFields(ctx, "read");
            _this.query.select(ctx, fields, ctx.entity, conditions, isFullMode, orderByField, skip, take, function(dbResponse){
                var preparedResponse = dbResponse;
                resolveForeignKeys(ctx, preparedResponse, function(resolvedResponse){
                    ctx.res.json(resolvedResponse);
                });
            });
        };

        //----------------------------------------------
        // PRIVATE
        //----------------------------------------------

        function getConditions(ctx, requestParams){
            var conditions = [];
            if(!!requestParams.conditions){
                var conditionsString = decodeURIComponent(requestParams.conditions);
                var strings = conditionsString.split("&");
                for(var i=0; i<strings.length; i++){
                    var condition = parseCondition(ctx, strings[i]);
                    if(condition === null) continue;
                    conditions.push(condition);
                }
            }
            if(!!requestParams.id){
                conditions.push(new _this.condition.Condition("id", "=", requestParams.id));
            }
            if(ctx.accessType === "private"){
                _this.auth.addPrivateReadCondition(ctx, conditions, ctx.entity);
            }
            return conditions;
        }

        function parseCondition(ctx, conditionString){
            var operands = null;
            var operator = null;
            var fieldValue = null;
            var operators = ["=", "<", "<=", ">", ">="];
            for(var i=0; i<operators.length; i++){
                if(conditionString.contains(operators[i])){
                    operands = conditionString.split(operators[i]);
                    operator = operators[i];
                    break;
                }
            }
            if(!operands || operands.length < 2) return null;
            var fieldName = operands[0];
            _this.auth.authorizeField(ctx, fieldName, "read");
            if(fieldName.contains("integer")) {
                fieldValue = parseInt(operands[1]);
            }else if(fieldName.contains("float")){
                fieldValue = parseFloat(operands[1]);
            }else{
                fieldValue = operands[1];
            }
            return new _this.condition.Condition(fieldName, operator, fieldValue);
        }

        function resolveForeignKeys(ctx, dataArray, callback){
            if(dataArray.length === 0) { callback(dataArray); return; }
            var resolveOp = {items: {}, queriesCount: 0};
            var foreignKeys = {};
            var foreignKeyFound = false;
            var fields = ctx.config.entities[ctx.entity].fields;
            for(var fieldName in fields){
                if(!fields.hasOwnProperty(fieldName)) continue;
                if(!!fields[fieldName].foreignKey){
                    foreignKeyFound = true;
                    foreignKeys[fieldName] = fields[fieldName].foreignKey;
                }
            }
            if(!foreignKeyFound){ callback(dataArray); return; }
            for(var foreignKey in foreignKeys){
                if(!foreignKeys.hasOwnProperty(foreignKey)) continue;
                if(!resolveOp.items.hasOwnProperty[foreignKey]) resolveOp.items[foreignKey] = {};
                for(var i=0; i<dataArray.length; i++){
                    resolveForeignKey(ctx, dataArray, i, foreignKeys, foreignKey, resolveOp, callback);
                }
            }
        }

        function resolveForeignKey(ctx, dataArray, dataIndex, foreignKeys, foreignKey, resolveOp, callback){
            var dataItem = dataArray[dataIndex];
            if(!dataItem.hasOwnProperty(foreignKey)) {
                return;
            }

            // check if foreignKeyVal is being resolved. 
            // if yes, add it to the list of items that needs to be resolved using foreignKeyVal.
            var foreignKeyVal = dataItem[foreignKey];
            if(resolveOp.items[foreignKey].hasOwnProperty(foreignKeyVal)){
                resolveOp.items[foreignKey][foreignKeyVal].push(dataItem);
                return;
            }

            // add new list of data items to be resolved using foreignKeyVal
            var foreignEntity = foreignKeys[foreignKey].foreignEntity;
            var resolvedKeyName = foreignKeys[foreignKey].resolvedKeyName;
            resolveOp.items[foreignKey][foreignKeyVal] = [dataItem];

            // create conditions list for getting the resolved data from db
            var conditions = [];
            conditions.push(new _this.condition.Condition("id", "=", foreignKeyVal));
            if(ctx.accessType === "private"){
                _this.auth.addPrivateReadCondition(ctx, conditions, foreignEntity);
            }

            // execute query
            resolveOp.queriesCount++;
            var fields = _this.auth.getAllowedFields(ctx, "read", foreignEntity);
            _this.query.select(ctx, fields, foreignEntity, conditions, false, "id", 0, 1, function(resolvedItems){
                var resolvedItem = resolvedItems[0];
                if(!resolvedItem) resolvedItem = null;
                var dataItems = resolveOp.items[foreignKey][foreignKeyVal];
                for(var j=0; j<dataItems.length; j++){
                    dataItems[j][resolvedKeyName] = resolvedItem;
                }
            }, function(){
                resolveOp.queriesCount--;
                if(resolveOp.queriesCount <= 0) callback(dataArray);
            });
        }

        _construct();
    }
};