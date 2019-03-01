// ILoadPlugin.aidl
package android.mx.plugin.aidl;

// Declare any non-default types here with import statements

interface ILoadPlugin {
// Load local plugin
    void loadPluginFile(String fileName);
// Called app auto restart
    void selfRestaet();
// check versio to update if flag equal 0 check else update
    boolean checkVersion(int flag);
}
