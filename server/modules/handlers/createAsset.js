module.exports = {
    dependencies: ["azure", "multiparty", "exec", "guid", "mime"],
    Instance: function(){
        var _this = this;

        //----------------------------------------------
        // CONSTRUCTOR
        //----------------------------------------------

        function _construct(){}

        //----------------------------------------------
        // PUBLIC
        //----------------------------------------------

        this.execute = function(ctx, req){
            var blobService = _this.azure.createBlobService(ctx.config.storageConnectionString);
            var form = new (_this.multiparty.Form)();
            var isFileReceived = false;
            var responseString = null;
            var errorObj = null;
            form.on('part', function(stream) {
                _this.exec.safeExecute(ctx, function(){
                    isFileReceived = true;
                    if (!stream.filename) {
                        throw new Error("400: submitted file is not a valid file");
                    }
                    var size = stream.byteCount - stream.byteOffset;
                    var name = _this.guid.raw() + stream.filename.substring(stream.filename.lastIndexOf("."));
                    blobService.createBlockBlobFromStream("orion", name, stream, size, {
                        contentSettings: { contentType: _this.mime.lookup(name) }
                    }, function(error) {
                        if(error) {
                            throw new Error("500: error while saving file to blob storage");
                        }else{
                            ctx.res.send(name);
                        }
                    });
                });
            });
            form.on('progress', function(bytesReceived, bytesExpected){
                _this.exec.safeExecute(ctx, function(){
                    if(!isFileReceived && bytesReceived >= bytesExpected){
                        throw new Error("400: file not received");
                    }
                });
            });
            form.on('error', function(err){
                _this.exec.safeExecute(ctx, function(){
                    throw new Error("500: error while parsing form data");
                });
            });
            form.parse(req);
        };

        _construct();
    }
};