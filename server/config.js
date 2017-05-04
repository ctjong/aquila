module.exports = {
    secretKey: "5d8738c8e71c4bedb611ce2cf53453a51df5c57b860a490fbf398e6dc743d9c3",
    salt: "8519a3a4c6b94341814676288f8fac45d251ba29fdff48438aed67215024c0b5",
    databaseConnectionString: process.env.AQUILA_DB_CONNECTION_STRING,
    storageConnectionString: process.env.AQUILA_AZURE_STORAGE_CONNECTION_STRING,
    facebookAppSecret: process.env.AQUILA_FB_APP_SECRET,
    facebookAppId: process.env.AQUILA_FB_APP_ID,
    storageContainerName: "aquila",
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