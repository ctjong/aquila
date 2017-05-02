module.exports = {
    dependencies: ["auth", "helper", "condition", "query"],
    Instance: function(){
        var _this = this;

        //----------------------------------------------
        // CONSTRUCTOR
        //----------------------------------------------

        function _construct(){}

        //----------------------------------------------
        // PUBLIC
        //----------------------------------------------

        this.execute = function(ctx, requestBody, resourceId){
            requestBody = _this.helper.lowercaseKeys(requestBody);
            var fields = _this.auth.getAllowedFields(ctx, "update");
            var updateFields = {};
            for(var i=0; i<fields.length; i++){
                var fieldName = fields[i];
                if(!requestBody.hasOwnProperty(fieldName)) continue;
                updateFields[fieldName] = requestBody[fieldName];
            }
            if(Object.keys(updateFields).length === 0){
                throw new Error("400: bad request");
            }
            if(ctx.entity !== "user") {
                _this.query.update(ctx, ctx.entity, updateFields, [
                    new _this.condition.Condition("id", "=", resourceId)
                ], function(dbResponse){ 
                    ctx.res.send(dbResponse);
                });
            }else{
                _this.query.quickFind(ctx, ["domain"], "user", {"id": resourceId}, function(resource){
                    if(!resource) throw new Error("400: invalid user id " + resourceId);
                    if(resource.domain !== "local") throw new Error("400: updating external user info is not supported");
                    _this.query.update(ctx, ctx.entity, updateFields, [
                        new _this.condition.Condition("id", "=", resourceId)
                    ], function(dbResponse){ 
                        ctx.res.send(dbResponse);
                    });
                });
            }
        };

        _construct();
    }
};