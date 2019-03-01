package com.moxi.remotefilemanager.model;

import android.os.Environment;
import android.util.Log;

import com.moxi.remotefilemanager.filedata.EasyUIJsonUtil;
import com.moxi.remotefilemanager.filedata.EasyUITreeConverter;
import com.moxi.remotefilemanager.filedata.EasyUITreeNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by King on 2017/8/22.
 */

public class FileData {
    public static final  String E_DOWNLOAD_DIR= Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"DDBooks";
    long fileId = 1;
    List<Resource> list = new ArrayList<>();

    public List<EasyUITreeNode> getResourcesJson(String path) {
        Resource resource = new Resource();
        resource.setName("根目录");
        resource.setId(new Long(1));
        resource.setPid(new Long(0));
        resource.setUrl(path);
        list.add(resource);
        getDirectoryList(new File(path), 1);
        //如果是指定用户只需要指定菜单
        List<EasyUITreeNode> treeList = EasyUIJsonUtil.convert(list, new EasyUITreeConverter() {
            public EasyUITreeNode convert(Object obj) {
                Resource res = (Resource) obj;
                // 只允许生成菜单节点（）
//                if (res.getType() != 0) {
//                    return null;
//                }
                EasyUITreeNode node = new EasyUITreeNode();
                node.setId(res.getId().toString());
                node.setText(res.getName());
                if (res.getId() == 1) {
                    node.setIconCls("ext-icon-house");
                    node.setState("open");
                } else {
                    node.setIconCls("ext-icon-folder_page");
                    if (res.getPid() == 1) {
                        node.setState("open");
                    } else if (res.getState() == 1) {
                        node.setState("closed");
                    } else {
                        node.setState("open");
                    }
                }

                node.setUrl(res.getUrl());
                node.setParentId(res.getPid().toString());
                return node;
            }
        });
        return treeList;
    }

    /**
     * 获取指定路径下的所有文件和文件夹
     *
     * @param filePath
     * @return
     */
    public List<Resource> getFileAndDirectoryWithPath(String filePath) {
        Log.d("TAG",filePath);
        File root = new File(filePath);
        List<Resource> dataRes = new ArrayList<>();
        if (!root.exists())
            return dataRes;
        File[] files = root.listFiles();
        if (files == null || files.length == 0) {
            return dataRes;
        }

        for (File file : files) {
            if (file.isDirectory() && file.canRead()) {
                if (file.getAbsolutePath().contains("sdcard") || file.getAbsolutePath().contains("extsd")) {
                    if (file.getName().startsWith(".")) {
                        continue;
                    }
                    if (file.getAbsolutePath().equals(E_DOWNLOAD_DIR)) continue;
                    if (!file.canRead()) continue;

                    Resource resource = new Resource();
                    resource.setName(file.getName());
                    resource.setUrl(file.getAbsolutePath());
                    File[] children = file.listFiles();
                    boolean isChindre = false;
                    if (children != null && children.length > 0) {
                        for (File chFile : children) {
                            if (chFile.getAbsolutePath().contains("sdcard") || chFile.getAbsolutePath().contains("extsd")) {
                                if (chFile.getName().startsWith(".")) {
                                    continue;
                                }
                                isChindre = true;
                            }
                            break;
                        }
                    }
                    if (isChindre) {
                        resource.setState(1);
                        resource.setIconcls("le_folder_trash.png");
                    } else {
                        resource.setState(0);
                        resource.setIconcls("le_folder.png");
                    }
                    dataRes.add(resource);
                }
            } else {
                if (file.getName().startsWith(".")) {
                    continue;
                }
                Resource resource = new Resource();
                resource.setName(file.getName());
                resource.setState(2);
                resource.setUrl(file.getAbsolutePath());

                int index = file.getName().lastIndexOf(".");
                if (index > 0) {
                    String prx = file.getName().substring(index + 1);
                    if(prx.equals("jpg")||prx.equals("png"))
                    resource.setIconcls("le_mail_ru.png");
                    else if(prx.equals("mp3"))
                        resource.setIconcls("op_music.png");
                    else if(prx.equals("pdf")||prx.equals("mobi")||prx.equals("djvu")||prx.equals("epub")
                            ||prx.equals("doc")||prx.equals("fb2")||prx.equals("rtf")||prx.equals("cbz")||prx.equals("cbr")||prx.equals("odt")
                            ||prx.equals("chm")||prx.equals("txt")){
                        resource.setIconcls("le_file.png");
                    }else  if(prx.equals("rar")||prx.equals("zip")){
                        resource.setIconcls("le_rar.png");
                    }else{
                        resource.setIconcls("op_about.png");
                    }
                }else{
                    resource.setIconcls("le_unknown.png");
                }
                dataRes.add(resource);
            }
        }
        nameSort(dataRes);
        return dataRes;
    }

    public void getDirectoryList(File root, long id) {
        if (!root.exists())
            return;
        File[] files = root.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            fileId++;
            if (file.isDirectory() && file.canRead()) {
                if (file.getAbsolutePath().contains("sdcard") || file.getAbsolutePath().contains("extsd")) {
                    if (file.getName().startsWith(".")) {
                        continue;
                    }
                    Resource resource = new Resource();
                    resource.setName(file.getName());
                    resource.setId(fileId);
                    resource.setPid(id);
                    resource.setUrl(file.getAbsolutePath());

                    File[] children = file.listFiles();
                    boolean isChindre = false;
                    if (children != null && children.length > 0) {
                        for (File chFile : children) {
                            if (chFile.isDirectory() && chFile.canRead()) {
                                if (chFile.getAbsolutePath().contains("sdcard") || chFile.getAbsolutePath().contains("extsd")) {
                                    if (chFile.getName().startsWith(".")) {
                                        continue;
                                    }
                                    isChindre = true;
                                }
                                break;
                            }
                        }
                    }
                    if (isChindre) {
                        resource.setState(1);
                        getDirectoryList(file, fileId);
                    } else {
                        resource.setState(0);
                    }
                    list.add(resource);

                }
            }
        }
    }

    public List<Resource> getFileList(File root) {
        List<Resource> resList = new ArrayList<>();
        if (!root.exists())
            return resList;
        File[] files = root.listFiles();
        if (files == null || files.length == 0) {
            return resList;
        }
        for (File file : files) {
            if (file.isDirectory() && file.canRead()) {

            } else {
                if (file.getName().startsWith(".")) {
                    continue;
                }
                Resource resource = new Resource();
                resource.setUrl(file.getAbsolutePath());
                resource.setName(file.getName());
                String fileSize = "0KB";
                if (file.length() > 1024 * 1024)
                    fileSize = (Math.round(file.length() * 100 / (1024 * 1024)) / 100)
                            + "MB";
                else
                    fileSize = (Math.round(file.length() * 100 / 1024) / 100)
                            + "KB";
                resource.setTarget(fileSize);
                int index = file.getName().lastIndexOf(".");
                if (index > 0)
                    resource.setIconcls(file.getName().substring(index + 1));
                resList.add(resource);
            }
        }
        return resList;
    }


    /**
     * 按名称排序
     */
    private void nameSort(List<Resource> listData) {
        Collections.sort(listData, new NameComparator());
        Collections.sort(listData, new MdirOrFileComparator());
    }

    /**
     * 初次顺序排序
     */
    private static class NameComparator implements Comparator<Resource> {
        @Override
        public int compare(Resource lhs, Resource rhs) {
            if (lhs == null && rhs == null) return 0;
            if (lhs == null || rhs == null) return lhs == null ? 1 : -1;

            String name1 = lhs.getName();
            String name2 = rhs.getName();
            if (name1 == null && name2 == null) {
                return 0;
            } else if (name1 == null && name2 != null) {
                return 1;
            } else if (name1 != null && name2 == null) {
                return -1;
            } else if (name1.equals("") && !name2.equals("")) {
                return 1;
            } else if (!name1.equals("") && name2.equals("")) {
                return -1;
            }
            List<String> list1 = getNumbers(name1);
            List<String> list2 = getNumbers(name2);
            if (list1.size() > 0 && list2.size() > 0) {
                String[] text1 = name1.split("\\d+");
                String[] text2 = name2.split("\\d+");
                if ((text1.length == 0 && text2.length == 0)
                        || (text1.length != 0 && text2.length == 0 && text1[0].equals(""))
                        || (text1.length == 0 && text2.length != 0 && text2[0].equals(""))) {
                    String num1 = list1.get(0);
                    String num2 = list2.get(0);
                    if (num1.length() != num2.length()) {
                        return num1.length() > num2.length() ? 1 : -1;
                    }

                    int type = name1.compareTo(name2);
                    if (type > 0) {
                        return 1;
                    } else if (type < 0) {
                        return -1;
                    }
                }

                int len = text1.length > text2.length ? text2.length : text1.length;

                for (int i = 0; i < len; i++) {
                    if (text1[i].equals(text2[i]) && (i < list1.size() && i < list2.size())) {
                        String num1 = list1.get(i);
                        String num2 = list2.get(i);
                        if (num1.length() != num2.length()) {
                            return num1.length() > num2.length() ? 1 : -1;
                        }
                        int type = name1.compareTo(name2);
                        if (type > 0) {
                            return 1;
                        } else if (type < 0) {
                            return -1;
                        }
                    }
                }
            }
            int type = name1.compareTo(name2);
            if (type > 0) {
                return 1;
            } else if (type < 0) {
                return -1;
            }
            return 0;
        }
    }

    public static List<String> getNumbers(String str) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(str);
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }
    /**
     * 按文件夹与文件排序
     */
    private static class MdirOrFileComparator implements java.util.Comparator<Resource> {

        @Override
        public int compare(Resource lhs, Resource rhs) {
            if (new File(lhs.getUrl()).isFile() && new File(rhs.getUrl()).isDirectory()) {
                return 1;
            } else if (new File(lhs.getUrl()).isDirectory() && new File(rhs.getUrl()).isFile()) {
                return -1;
            }
            return 0;
        }
    }
}
