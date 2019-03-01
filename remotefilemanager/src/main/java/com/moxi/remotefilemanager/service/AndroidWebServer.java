package com.moxi.remotefilemanager.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.moxi.remotefilemanager.model.FileData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by King on 2017/8/16.
 */

public class AndroidWebServer extends NanoHTTPD {

    private Context context;

    public AndroidWebServer(int port, Context context) {
        super(port);
        this.context = context;
    }

    public AndroidWebServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {

        String url = session.getUri();
        Log.d("TAG", url);

        if (url.contains("downfile")) {
            Method method = session.getMethod();
            Map<String, String> files = new HashMap<>();
            if (Method.POST.equals(method) || Method.PUT.equals(method)) {
                try {
                    session.parseBody(files);
                } catch (IOException ioe) {
                    return newFixedLengthResponse("Internal Error IO Exception: " + ioe.getMessage());
                } catch (ResponseException re) {
                    return newFixedLengthResponse(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
                }
            }
            Map<String, String> params = session.getParms();
            String paramsKey;
            String filePath = "";
            String fileName = "";
            for (Map.Entry<String, String> entry : params.entrySet()) {
                paramsKey = entry.getKey();
                if (paramsKey.contains("filePath")) {
                    try {
                        filePath = URLDecoder.decode(entry.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                if (paramsKey.contains("fileName")) {
                    try {
                        fileName = URLDecoder.decode(entry.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            Response res = null;
            File file = new File(filePath);
            if (file != null && file.exists()&& !file.isDirectory()) {
                InputStream is = null;
                try {
                    is = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                res = newFixedLengthResponse(Response.Status.OK, "application/octet-stream", is,file.length());
                res.addHeader("Content-Length", String.valueOf(file.length()));
                res.addHeader("Content-Disposition", "attachment;filename="+fileName );
                return res;
            }
            return newFixedLengthResponse("error");
        }

        String msg = "<html><body><h1>Hello server</h1>\n";
        String result = msg + "</body></html>\n";
        Map<String, String> parms = session.getParms();
        String type;
        type = parms.get("action");
//        Log.e("-----", type);
        if (type != null && type.equals("2")) {
            Log.e("-----", type);
            String path = parms.get("filePath");
            try {
                path= URLDecoder.decode(path, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(path))
                path = "/mnt/sdcard";
            FileData fileData = new FileData();
            msg = JSON.toJSONString(fileData.getResourcesJson(path));
            Log.e("msg---", msg);
            return newFixedLengthResponse(msg);
        }
        if (type != null && type.equals("21")) {
            Log.e("-----", type);
            String path = parms.get("filePath");
            try {
                path= URLDecoder.decode(path, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(path))
                path = "/mnt/sdcard";
            FileData fileData = new FileData();
            msg = JSON.toJSONString(fileData.getFileAndDirectoryWithPath(path));
            Log.e("msg---", msg);
            return newFixedLengthResponse(msg);
        }
        if (type != null && type.equals("22")) {
            return newFixedLengthResponse(meunMethod(parms));
        }
//        if(type != null && type.equals("22")){
//            return newFixedLengthResponse(meunMethod(parms));
//        }
        if (type != null && type.equals("3")) {
            String path = parms.get("filePath");
            if (TextUtils.isEmpty(path))
                path = "/mnt/sdcard";
            Log.e("-----", type + "path:" + path);
            FileData fileData = new FileData();
            msg = JSON.toJSONString(fileData.getFileList(new File(path)));
            Log.e("msg---", msg);
            return newFixedLengthResponse(msg);
        } else if (type != null && type.equals("5")) {//检查文件是否已经存在
            String filePath = parms.get("filePath");
            String fileName = parms.get("fileName");
            Log.d("TAG", filePath + File.separator + fileName);
            if (new File(filePath + File.separator + fileName).exists())
                return newFixedLengthResponse("1");
            return newFixedLengthResponse("0");

        } else if (type != null && type.equals("4")) {
            Method method = session.getMethod();
            // ▼ 1、parse post body ▼
            Map<String, String> files = new HashMap<>();
            if (Method.POST.equals(method) || Method.PUT.equals(method)) {
                try {
                    session.parseBody(files);
                } catch (IOException ioe) {
                    return newFixedLengthResponse("Internal Error IO Exception: " + ioe.getMessage());
                } catch (ResponseException re) {
                    return newFixedLengthResponse(re.getStatus(), MIME_PLAINTEXT, re.getMessage());
                }
            }
            //after the body parsed, by default nanoHTTPD will save the file to cache and put it into params( "image_file_1" as key and the value is "logo-square1.png");
            //files key is just like "image_file_1", and the value is nanoHTTPD's template file path in cache
            // ▲ 1、parse post body ▲
            // ▼ 2、copy file to target path xiaoyee ▼
            Map<String, String> params = session.getParms();
            String paramsKey;
            String fileNameValue;
            String filePathValue = "";
            String directory = "";
            File tmpFile = null;
            File targetFile = null;
            String fileName = "";

            for (Map.Entry<String, String> entry : params.entrySet()) {
                paramsKey = entry.getKey();
                if (paramsKey.contains("filepathvalue")) {
                    try {
                      String  path = URLDecoder.decode(entry.getValue(), "utf-8");
                        filePathValue = path;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                if (paramsKey.contains("fileName")) {
                    try {
                        fileNameValue = URLDecoder.decode(entry.getValue(), "utf-8");
                        fileName = fileNameValue;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                if (paramsKey.contains("uploadfile")) {
                    final String tmpFilePath = files.get(paramsKey);
                    Log.d("TAG","tmpFilePath:"+tmpFilePath);
                    tmpFile = new File(tmpFilePath);
                }
                if (paramsKey.contains("directory")) {
                    try {
                        directory = URLDecoder.decode(entry.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d("TAG", "length:" + tmpFile.length());
            String saveFilePath;
            if (!TextUtils.isEmpty(directory)) {
                int last = directory.lastIndexOf("/");
                directory = directory.substring(0, last);
                if (!new File(filePathValue + "/" + directory).exists()) {
                    new File(filePathValue + "/" + directory).mkdir();
                }
                saveFilePath = filePathValue + "/" + directory + "/" + fileName;
            } else {
                saveFilePath = filePathValue + "/" + fileName;
            }

            Log.d("TAG", saveFilePath);

            targetFile = new File(saveFilePath);
            boolean status = copyFile(tmpFile, targetFile);
            if (status) {
                tmpFile.delete();
                return newFixedLengthResponse("文件复制成功");
            } else {
                return newFixedLengthResponse("Error 404: File not found");
            }
        } else {
            InputStream is;
            try {
                //is = context.getAssets().open("index.html");
                is = context.getAssets().open("desktop.html");
                int lenght = is.available();
                byte[] buffer = new byte[lenght];
                is.read(buffer);
                result = new String(buffer, "utf8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newFixedLengthResponse(result);//msg + "</body></html>\n"
        }
    }


    private boolean copyFile(File source, File target) {

        try {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(target);

            byte[] buf = new byte[65536];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                Log.d("TAG", "len:" + len);
            }
            in.close();
            out.close();
        } catch (IOException ioe) {
            return false;
        }
//        }
        return true;
    }

    private String meunMethod(Map<String, String> parms) {
        String method = parms.get("method");
        String path = parms.get("filePath");
        try {
            path= URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("TAG","meunMethod:"+path);
        if ("creatDir".equals(method)) {
            String newFileName = parms.get("newFileName");
            String newDir = path + "/" + newFileName;
            if (!new File(newDir).exists()) {
                new File(newDir).mkdir();
            }
        } else if ("delDir".equals(method)) {
            String dirPath = parms.get("dirPath");
            Log.d("TAG", dirPath);
            delFile(dirPath);
        } else if ("reNameFile".equals(method)) {
            String dirPath = parms.get("dirPath");
            String newFileName = parms.get("newFileName");

            String newDir = path + "/" + newFileName;
            File oldfile = new File(dirPath);
            File newfile = new File(newDir);
            if (oldfile.exists()) {
                oldfile.renameTo(newfile);
            }
        } else if ("copyfile".equals(method)) {
            String fromPath = parms.get("dirPath");
            copy(fromPath, path);
        }
        return "{\"code\":200,\"msg\":\"ok\"}";
    }

    private int delFile(String filePath) {
        File file = new File(filePath);
        File[] currentFiles = file.listFiles();
        if (currentFiles == null || currentFiles.length == 0) {
            file.delete();
            return 0;
        }
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory()) {
                delFile(currentFiles[i].getPath());
            } else {
                currentFiles[i].delete();
            }
        }
        File dirfile = new File(filePath);
        dirfile.delete();

        return 0;
    }

    public int copy(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return -1;
        }

        if (!root.isDirectory()) {
            CopySdcardFile(root.getPath(), toFile + "/" + root.getName());
            return 0;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        String dirName = root.getName();
        //目标目录
        File targetDir = new File(toFile + "/" + dirName);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
            {
                copy(currentFiles[i].getPath() + "/", toFile + "/" + dirName + "/" + currentFiles[i].getName() + "/");

            } else//如果当前项为文件则进行文件拷贝
            {
                CopySdcardFile(currentFiles[i].getPath(), toFile + "/" + dirName + "/" + currentFiles[i].getName());
            }
        }
        return 0;
    }


    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public int CopySdcardFile(String fromFile, String toFile) {
        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex) {
            return -1;
        }
    }

}