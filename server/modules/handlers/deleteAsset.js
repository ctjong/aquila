module.exports = {
    dependencies: ["azure", "exec"],
    Instance: function(){
        var _this = this;

        //----------------------------------------------
        // CONSTRUCTOR
        //----------------------------------------------

        function _construct(){}

        //----------------------------------------------
        // PUBLIC
        //----------------------------------------------

        this.execute = function(ctx, assetName){
            var blobService = _this.azure.createBlobService(ctx.config.storageConnectionString);
            var responseString = null;
            var errorObj = null;
            blobService.deleteBlob("orion", assetName, function(error, response){
                _this.exec.safeExecute(ctx, function(){
                    if(error) {
                        throw new Error("400: asset not found");
                    }else{
                        ctx.res.end();
                    }
                });
            });
        };

        _construct();
    }
};