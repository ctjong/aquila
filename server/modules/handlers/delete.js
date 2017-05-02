module.exports = {
    dependencies: ["auth", "query"],
    Instance: function(){
        var _this = this;

        //----------------------------------------------
        // CONSTRUCTOR
        //----------------------------------------------

        function _construct(){}

        //----------------------------------------------
        // PUBLIC
        //----------------------------------------------

        this.execute = function(ctx, resourceId){
            if(ctx.entity !== "user"){
                _this.query.delete(ctx, ctx.entity, resourceId, function(dbResponse){ 
                    ctx.res.send(dbResponse);
                });
            }else{
                _this.query.quickFind(ctx, ["domain"], "user", {"id": resourceId}, function(user){
                    if(!user) throw new Error("400: invalid user id " + resourceId);
                    if(user.domain !== "local") throw new Error("400: deleting external user is not supported");
                    _this.query.delete(ctx, "user", resourceId, function(dbResponse){ 
                        ctx.res.send(dbResponse);
                    });
                });
            }
        };

        _construct();
    }
};