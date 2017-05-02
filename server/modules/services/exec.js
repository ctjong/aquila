module.exports = {
    dependencies: [],
    Instance: function(){
        var _this = this;

        //----------------------------------------------
        // CONSTRUCTOR
        //----------------------------------------------

        function _construct(){}

        //----------------------------------------------
        // PUBLIC
        //----------------------------------------------

        this.handleError = function(ctx, err) {
            var errorObj = null;
            if(typeof(err) === "string"){
                errorObj = new Error(err);
            }else{
                errorObj = err;
            }
            try{
                console.error(errorObj);
                var statusCode = parseInt(errorObj.message.split(":")[0]);
                ctx.res.status(statusCode).send(errorObj.message);
            }catch(newErr){
                ctx.res.status(500).send(errorObj.message);
            }
        };

        this.safeExecute = function(ctx, fn){
            try{
                fn();
            }catch(err){
                _this.handleError(ctx, err);
            }
        };

        _construct();
    }
};