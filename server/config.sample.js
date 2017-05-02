module.exports = {
    secretKey: "beELV6CRgkuRcU6bnj9zhit6ojAEGW3mlxWgSieFcXQXE7KgtNj8O2tFurHGbvpA",
    salt: "4f263fd660521e51a950a5468bc1bb0c955a332051f501227c0cc030af5c61f9",
    databaseConnectionString: process.env.ORION_DB_CONNECTION_STRING,
    storageConnectionString: process.env.ORION_AZURE_STORAGE_CONNECTION_STRING,
    facebookAppSecret: process.env.ORION_FB_APP_SECRET,
    facebookAppId: process.env.ORION_FB_APP_ID,
    emailVerificationRequired: false,
    captchaEnabled: false,
    passwordReqs: {
        minLength: 8,
        uppercaseChar: false,
        lowercaseChar: false,
        digitChar: false,
        specialChar: false
    },
    roles: ["member", "admin"],
    defaultRole: "member",
    entities: {
        "asset": {
            perms: {create: ["member"], delete: []},
        },
        "user": {
            perms: {create: ["guest"], delete: ["admin"]},
            fields: {
                "id": { type: "id", perms: {read: ["member", "admin"], update: []}, createReq: 0, foreignKey: null },
                "domain": { type: "string", perms: {read: ["member", "admin"], update: []}, createReq: 0, foreignKey: null },
                "domainid": { type: "string", perms: {read: ["member", "admin"], update: []}, createReq: 0, foreignKey: null },
                "roles": { type: "string", perms: {read: ["member", "admin"], update: []}, createReq: 0, foreignKey: null },
                "username": { type: "string", perms: {read: ["member", "admin"], update: ["owner", "admin"]}, createReq: 2, foreignKey: null },
                "password": { type: "string", perms: {read: [], update: []}, createReq: 2, foreignKey: null },
                "email": { type: "string", perms: {read: ["member", "admin"], update: ["owner", "admin"]}, createReq: 2, foreignKey: null },
                "firstname": { type: "string", perms: {read: ["member", "admin"], update: ["owner", "admin"]}, createReq: 1, foreignKey: null },
                "lastname": { type: "string", perms: {read: ["member", "admin"], update: ["owner", "admin"]}, createReq: 1, foreignKey: null },
                "createdtime": { type: "timestamp", perms: {read: ["member", "admin"], update: []}, createReq: 0, foreignKey: null }
            },
        },
        "task": {
            perms: {create: ["member"], delete: ["owner", "admin"]},
            "fields": {
                "id": { type: "id", perms: {read: ["owner", "admin"], update: []}, createReq: 0, foreignKey: null },
                "ownerid": { type: "int", perms: {read: ["owner", "admin"], update: []}, createReq: 0, foreignKey: {
                    foreignEntity: "user",
                    resolvedKeyName: "owner"
                }},
                "taskname": { type: "string", perms: {read: ["owner", "admin"], update: ["owner", "admin"]}, createReq: 2, foreignKey: null },
                "taskdescription": { type: "string", perms: {read: ["owner", "admin"], update: ["owner", "admin"]}, createReq: 1, foreignKey: null },
                "taskdate": { type: "int", perms: {read: ["owner", "admin"], update: ["owner", "admin"]}, createReq: 2, foreignKey: null },
                "createdtime": { type: "timestamp", perms: {read: ["owner", "admin"], update: []}, createReq: 0, foreignKey: null }
            }
        }
    }
};