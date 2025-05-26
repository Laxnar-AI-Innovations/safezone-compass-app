
package com.laxnar.hersafezone.data

object FirestoreSchema {
    // Collection names
    const val USERS = "users"
    const val SOS = "sos"
    
    // User document fields
    object UserFields {
        const val EMAIL = "email"
        const val NAME = "name"
        const val PHONE = "phone"
        const val EMERGENCY_CONTACTS = "emergencyContacts"
        const val FCM_TOKEN = "fcmToken"
        const val CREATED_AT = "createdAt"
        const val LAST_SEEN = "lastSeen"
        const val IS_ACTIVE = "isActive"
    }
    
    // SOS document fields
    object SOSFields {
        const val USER_ID = "userId"
        const val TIMESTAMP = "timestamp"
        const val STATUS = "status" // active, resolved, cancelled
        const val MESSAGE = "message"
        const val LOCATION = "location"
        
        // Location nested fields
        object LocationFields {
            const val LATITUDE = "latitude"
            const val LONGITUDE = "longitude"
            const val GEOHASH = "geohash"
            const val ADDRESS = "address"
            const val ACCURACY = "accuracy"
        }
        
        // Response tracking
        const val RESPONDERS = "responders"
        const val RESPONSE_COUNT = "responseCount"
        const val FIRST_RESPONSE_TIME = "firstResponseTime"
    }
    
    // FCM and notification fields
    object FCMFields {
        const val TOKEN = "token"
        const val DEVICE_ID = "deviceId"
        const val PLATFORM = "platform"
        const val APP_VERSION = "appVersion"
        const val UPDATED_AT = "updatedAt"
    }
    
    // Emergency contact fields
    object EmergencyContactFields {
        const val NAME = "name"
        const val PHONE = "phone"
        const val RELATIONSHIP = "relationship"
        const val IS_PRIMARY = "isPrimary"
    }
}
