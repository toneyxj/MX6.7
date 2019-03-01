// IMxUserSession.aidl
package android.mx.user.aidl;

// Declare any non-default types here with import statements

interface IMxUserSession {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    String getSession(String userId);
}
