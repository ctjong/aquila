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

        this.Condition = function (fieldName, operator, fieldValue) {
            this.fieldName = fieldName;
            this.operator = operator;
            this.fieldValue = fieldValue;
            this.getWhereExpression = function(queryObj){
                return "[" + fieldName + "]" + operator + queryObj.addQueryParam(fieldValue);
            };
        };

        _construct();
    }
};