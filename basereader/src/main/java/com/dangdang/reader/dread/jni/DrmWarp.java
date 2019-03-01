package com.dangdang.reader.dread.jni;

import com.dangdang.zframework.log.LogM;

public class DrmWarp {
    // drm返回值
    public final static int SUCCESS = 1;
    public final static int FAILED = -1;

    // 加解密数据
    private byte[] deCryptData;
    private byte[] enCryptData;

    private String publicKey;

    // public static Object pubPath;
    // public static Object priPath;

    public DrmWarp() {

    }

    public static DrmWarp getInstance() {
        return new DrmWarp();
    }

    public native int init(String pubPath, String priPath);

    /**
     * 初始化每本书的证书
     *
     * @param filepath
     * @param certKey
     * @param productId
     * @return
     */
    public native boolean initBookKey(String filepath, byte[] certKey, String productId, boolean isPreset);

    public native void setBasePackageName(String packageName, boolean isTestEnv);

    /**
     * @return SUCCESS or FAILED
     */
    public native int getPublicKeyN();

    /**
     * @param dataPath 图片路径，可以是zip格式的
     * @param fileType 代表加密或者不加密文件 从BaseJniWarp里取BOOKTYPE_XXXXX;
     * @return
     */
    public native int deCryptPic(String dataPath, int fileType);

    public native int enCrypt(byte[] src);

    public byte[] getDeCryptAfterData() {
        return deCryptData;
    }

    public void setDeCryptAfterData(byte[] deCryptAfterData) {
        LogM.i(getClass().getSimpleName(), " setDeCryptAfterData byte[].length = " + deCryptAfterData.length);
        this.deCryptData = deCryptAfterData;
    }

    public byte[] getEnCryptData() {
        return enCryptData;
    }

    public void setEnCryptData(byte[] enCryptData) {
        this.enCryptData = enCryptData;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        LogM.d(getClass().getSimpleName(), " setPublicKey = " + publicKey);
    }

    public final native String safe(String verify, String rsaBase64, String source, Integer retCode);

    public native String generateSignature(String verify, String sourceStr, String token);

    /**
     * 在initBookKey后调用, 试验从证书中读取key和version
     * 如果存在rsa密钥失效的情况，会返回相应的错误代码
     *
     * @return SUCCESS        1
     * #define FAILED        -1
     * #define DECRYPT_SECRET_KEY_FAIL -100
     * #define CERT_INVALID -101
     * #define DECRYPT_MAGIC_FAIL -103
     */
    public native int verifyRSAKey();
}
