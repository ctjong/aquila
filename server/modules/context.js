module.exports = {
    Context: function(req, res, entity, accessType){
        var _this = this;
        var errorObj = null;

        //----------------------------------------------
        // CONSTRUCTOR
        //----------------------------------------------

        function _construct(){
            try{
                _this.req = req;
                _this.res = res;
                _this.entity = entity;
                _this.config = require("../config");
                if(_this.entity !== "asset" && !_this.config.entities.hasOwnProperty(_this.entity)) {
                    throw new Error("400: invalid entity " + _this.entity);
                }
                _this.accessType = accessType;
            }catch(ex2){
                errorObj = ex2;
            }
            if(errorObj) throw errorObj;
        }

        //----------------------------------------------
        // PUBLIC
        //----------------------------------------------

        this.req = null;
        this.res = null;
        this.config = null;
        this.entity = null;
        this.accessType = null;
        this.userId = null;
        this.userName = null;
        this.userRoles = [];
        this.userDomain = null;

        _construct();
    }
};