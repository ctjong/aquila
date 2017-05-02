module.exports = {
    dependencies: ["jwt", "query", "crypto", "condition"],
    Instance: function(){
        var _this = this;

        //----------------------------------------------
        // CONSTRUCTOR
        //----------------------------------------------

        function _construct(){}

        //----------------------------------------------
        // PUBLIC
        //----------------------------------------------

        this.authorize = function(ctx, action, resourceId, callback){
            var authHeader = ctx.req.get("authorization");
            if(!!authHeader){
                try{
                    var token = authHeader.replace("Bearer ", "");
                    var decoded = _this.jwt.verify(token, ctx.config.secretKey);
                    ctx.userId = decoded.id;
                    ctx.userName = decoded.name;
                    ctx.userRoles = decoded.roles.split(",");
                    ctx.userDomain = decoded.domain;
                }catch(e){
                    throw new Error("401: invalid token");
                }
            }
            if(ctx.userRoles.length === 0){
                ctx.userRoles = ["guest"];
            }
            if(ctx.accessType === "public"){
                checkPermissions(ctx, action, callback);
            }else{
                if(!ctx.userId) {
                    throw new Error("401: Unauthorized");
                }
                addOwnerRoleIfEligible(ctx, action, resourceId, function(){
                    checkPermissions(ctx, action, callback);
                });
            }
        };

        this.authorizeField = function(ctx, fieldName, action){
            if(!_this.getAllowedFields(ctx, action).contains(fieldName.toLowerCase())){
                throw new Error("400: invalid field (" + fieldName + ") or user does not have access to it");
            }
        };

        this.getAllowedFields = function(ctx, action, entity){
            if(!entity) entity = ctx.entity;
            var fields = ctx.config.entities[entity].fields;
            var allowedFields = [];
            for(var fieldName in fields){
                if(!fields.hasOwnProperty(fieldName)) continue;
                var perms = fields[fieldName].perms[action];
                if(perms.containsAny(ctx.userRoles)){
                    allowedFields.push(fieldName);
                }
            }
            return allowedFields;
        };

        this.generateLocalUserToken = function(ctx, userName, password){
            if(!userName || !password) throw new Error("400: invalid login");
            _this.query.quickFind(ctx, ["id", "password", "roles", "domain"], "user", {"userName": userName}, function(user){
                // verify login
                if(!user) throw new Error("400: user not found with userName " + userName);
                if(user.domain !== "local") throw new Error("400: external user login is not supported in this endpoint");
                var hashedInput = _this.hashPassword(ctx, password);
                if(hashedInput !== user.password) throw new Error("400: invalid login");
                // generate token
                var tokenPayload = {id: user.id, name: userName, roles: user.roles, domain: user.domain};
                var token = _this.jwt.sign(tokenPayload, ctx.config.secretKey);
                ctx.res.json({"token": token});
            });
        };

        this.processFbslToken = function(ctx, fbToken) {
            var token = "";
            //TODO
            ctx.res.json({"token": token});
        };

        this.processFbllToken = function(ctx, fbToken) {
            var token = "";
            //TODO
            ctx.res.json({"token": token});
        };

        this.hashPassword = function(ctx, plainPassword){
            var hash = _this.crypto.createHmac('sha512', ctx.config.salt);
            hash.update(plainPassword);
            return hash.digest('hex');
        };

        this.addPrivateReadCondition = function(ctx, conditions, entity){
            addPrivateReadConditionInternal(ctx, conditions, entity === "user" ? "id" : "ownerid");
        };

        //----------------------------------------------
        // PRIVATE
        //----------------------------------------------

        function addOwnerRoleIfEligible(ctx, action, resourceId, callback){
            if(ctx.accessType === "public") return;
            if(action === "read"){
                // for read action, ownership constraint is applied via conditions (see read module).
                ctx.userRoles.push("owner");
                callback();
            }else if(action === "update" || action === "delete") {
                if(!resourceId) throw new Error("400: missing resource id");
                var resourceIdFields = ctx.entity === "user" ? ["id"] : ["ownerid"];
                _this.query.quickFind(ctx, resourceIdFields, ctx.entity, {"id": resourceId}, function(resource){
                    if(!resource) throw new Error("400: resource not found with id " + resourceId);
                    if((ctx.entity === "user" && (ctx.userId !== resource.id)) || (ctx.entity !== "user" && (ctx.userId !== resource.ownerid))) {
                        throw new Error("401: Unauthorized");
                    }
                    ctx.userRoles.push("owner");
                    callback();
                });
            }
        }

        function checkPermissions(ctx, action, callback){
            if(action === "create" || action === "delete"){
                var perms = ctx.config.entities[ctx.entity].perms[action];
                if(!perms.containsAny(ctx.userRoles)) {
                    throw new Error("401: Unauthorized");
                }
            }else{
                var allowedFields = _this.getAllowedFields(ctx, action);
                if(allowedFields.length === 0){
                    throw new Error("401: Unauthorized");
                }
            }
            callback();
        }

        function addPrivateReadConditionInternal(ctx, conditions, fieldName){
            for(var i=0; i<conditions.length; i++) {
                if(conditions[i].fieldName === fieldName && conditions[i].operator === "=") {
                    if(parseInt(conditions[i].fieldValue) !== ctx.userId){
                        throw new Error("401: Unauthorized");
                    }
                }
            }
            conditions.push(new _this.condition.Condition(fieldName, "=", ctx.userId));
        }

        _construct();
    }
};